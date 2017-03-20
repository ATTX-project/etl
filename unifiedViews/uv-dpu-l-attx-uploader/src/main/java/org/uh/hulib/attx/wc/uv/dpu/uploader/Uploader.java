package org.uh.hulib.attx.wc.uv.dpu.uploader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dataunit.rdf.RDFHelper;
import eu.unifiedviews.helpers.dataunit.rdf.RdfDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.virtualgraph.VirtualGraphHelper;
import eu.unifiedviews.helpers.dataunit.virtualgraph.VirtualGraphHelpers;
import eu.unifiedviews.helpers.dataunit.virtualpath.VirtualPathHelpers;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.context.UserContext;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultToleranceUtils;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
 * Main data processing unit class.
 *
 * @author ATTX
 */
@DPU.AsLoader
public class Uploader extends AbstractDpu<UploaderConfig_V1> {

    private static final Logger LOG = LoggerFactory.getLogger(Uploader.class);

    @DataUnit.AsInput(name = "fileInput", optional = true)
    public FilesDataUnit filesInput;

    @DataUnit.AsInput(name = "rdfInput", optional = true)
    public RDFDataUnit rdfInput;

    @DataUnit.AsOutput(name = "fileOutput", optional = true)
    public WritableFilesDataUnit filesOutput;

    @ExtensionInitializer.Init
    public FaultTolerance faultTolerance;

    public Uploader() {
        super(UploaderVaadinDialog.class, ConfigHistory.noHistory(UploaderConfig_V1.class));
    }

    public void sendFile(UserContext ctx, String parliamentBulkInsertLocation, CloseableHttpClient client, String graph, String rdfFormat, String filename, FilesDataUnit.Entry entry) throws DPUException {
        CloseableHttpResponse response = null;
        try {

            URIBuilder uriBuilder;
            uriBuilder = new URIBuilder(parliamentBulkInsertLocation);

            uriBuilder.setPath(uriBuilder.getPath());
            HttpPost httpPost = new HttpPost(uriBuilder.build().normalize());
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .addTextBody("dataFormat", rdfFormat, ContentType.MULTIPART_FORM_DATA)
                    .addBinaryBody("file", new File(URI.create(entry.getFileURIString())), ContentType.APPLICATION_OCTET_STREAM, filename);
            if (graph != null) {
                entityBuilder.addTextBody("graph", graph, ContentType.MULTIPART_FORM_DATA);
            }
            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw ContextUtils.dpuException(ctx, "FilesToParliament.execute.uploadFail", entry.toString(), IOUtils.toString(response.getEntity().getContent()));
            }
            LOG.info("File {} uploaded successfuly", entry);
        } catch (URISyntaxException | IllegalStateException | IOException | DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, ex, "FilesToParliament.execute.exception");
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
                try {
                    response.close();
                } catch (IOException ex) {
                    LOG.warn("Error in close", ex);
                }
            }
        }
    }

    public void sendClear(UserContext ctx, String parliamentSparqlLocation, CloseableHttpClient client, String graph) throws DPUException {
        CloseableHttpResponse response = null;
        try {
            String query = "CLEAR GRAPH <" + graph + ">";
            URIBuilder uriBuilder;
            uriBuilder = new URIBuilder(parliamentSparqlLocation);

            uriBuilder.setPath(uriBuilder.getPath());
            HttpPost httpPost = new HttpPost(uriBuilder.build().normalize());
            EntityBuilder entityBuilder = EntityBuilder.create()
                    .setParameters(new BasicNameValuePair("update", query));

            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);
            response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw ContextUtils.dpuException(ctx, "FilesToParliament.execute.clearFail", graph, IOUtils.toString(response.getEntity().getContent()));
            }
            LOG.info("Graph {} cleared successfuly", graph);
        } catch (URISyntaxException | IllegalStateException | IOException ex) {
            throw ContextUtils.dpuException(ctx, ex, "FilesToParliament.execute.exception");
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
                try {
                    response.close();
                } catch (IOException ex) {
                    LOG.warn("Error in close", ex);
                }
            }
        }
    }

    @Override
    protected void innerExecute() throws DPUException {
        CloseableHttpClient client = null;
        final String globalOutGraph = org.apache.commons.lang3.StringUtils.isEmpty(config.getTargetGraphName()) ? null : config.getTargetGraphName();
        try {
            client = HttpClients.createDefault();
            if (globalOutGraph != null && config.isClearDestinationGraph()) {
                LOG.info("Clearing destination graph");
                sendClear(ctx, config.getEndpointURL() + "update", client, globalOutGraph);
                LOG.info("Cleared destination graph");
            }
            for (FilesDataUnit.Entry entry : FilesHelper.getFiles(filesInput)) {
                String filename = VirtualPathHelpers.getVirtualPath(filesInput, entry.getSymbolicName());
                if (StringUtils.isEmpty(filename)) {
                    filename = entry.getSymbolicName();
                }
                String outGraph = null;
                if (globalOutGraph == null) {
                    String outGraphURIString = VirtualGraphHelpers.getVirtualGraph(filesInput, entry.getSymbolicName());
                    if (outGraphURIString == null) {
                        outGraph = null;
                    } else {
                        outGraph = outGraphURIString;
                    }
                } else {
                    outGraph = globalOutGraph;
                }
                if (globalOutGraph == null && config.isClearDestinationGraph()) {
                    LOG.info("Clearing destination graph");
                    sendClear(ctx, config.getEndpointURL() + "update", client, outGraph);
                    LOG.info("Cleared destination graph");
                }
                sendFile(ctx, config.getEndpointURL() + "upload", client, outGraph, config.getRdfFileFormat().toUpperCase(), filename, entry);

            }

            if (rdfInput != null) {
                final List<RDFDataUnit.Entry> graphs = FaultToleranceUtils.getEntries(faultTolerance, rdfInput,
                        RDFDataUnit.Entry.class);

                if (graphs.size() > 0) {
                    // Create output file.
                    final String outputFileName = "attx-uploader.nt";
                    // Prepare output file entity.
                    final FilesDataUnit.Entry outputFile = faultTolerance.execute(new FaultTolerance.ActionReturn<FilesDataUnit.Entry>() {

                        @Override
                        public FilesDataUnit.Entry action() throws Exception {
                            return FilesDataUnitUtils.createFile(filesOutput, outputFileName);
                        }
                    });
                    exportGraph(graphs, outputFile);

                    String outGraph = null;
                    if (globalOutGraph != null) {
                        outGraph = globalOutGraph;
                    }
                    sendFile(ctx, config.getEndpointURL() + "upload", client, outGraph, config.getRdfFileFormat().toUpperCase(), outputFileName, outputFile);
                }
            } else {
                //no data to be exported, no file being produced. 
                ContextUtils.sendMessage(ctx, DPUContext.MessageType.INFO, "no rdfInput", "");
            }
        } catch (DataUnitException ex) {
            throw ContextUtils.dpuException(ctx, ex, "FilesToParliament.execute.exception");
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ex) {
                    LOG.warn("Error in close", ex);
                }
            }
        }
    }

    private void exportGraph(final List<RDFDataUnit.Entry> sources, FilesDataUnit.Entry target) throws DPUException {
        final File targetFile = FaultToleranceUtils.asFile(faultTolerance, target);
        // Create parent directories.
        targetFile.getParentFile().mkdirs();
        // Prepare inputs.
        final org.openrdf.model.URI[] sourceUris = faultTolerance.execute(new FaultTolerance.ActionReturn<org.openrdf.model.URI[]>() {

            @Override
            public org.openrdf.model.URI[] action() throws Exception {
                return RdfDataUnitUtils.asGraphs(sources);
            }
        });
        try (FileOutputStream outStream = new FileOutputStream(targetFile); OutputStreamWriter outWriter = new OutputStreamWriter(outStream, Charset.forName("UTF-8"))) {
            faultTolerance.execute(rdfInput, new FaultTolerance.ConnectionAction() {

                @Override
                public void action(RepositoryConnection connection) throws Exception {
                    RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, outWriter);
                    // Export data.
                    connection.export(writer, sourceUris);
                }
            });
        } catch (IOException ex) {
            throw ContextUtils.dpuException(ctx, ex, "rdfToFiles.error.output");
        }
    }

}
