package org.uh.hulib.attx.wc.uv.dpu.uc1;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;

import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

/**
 * Main data processing unit class.
 *
 * @author ATTX
 */
@DPU.AsTransformer
public class Infras2Internal extends AbstractDpu<Infras2InternalConfig_V1> {

    private static final Logger log = LoggerFactory.getLogger(Infras2Internal.class);

	public Infras2Internal() {
		super(Infras2InternalVaadinDialog.class, ConfigHistory.noHistory(Infras2InternalConfig_V1.class));
	}
	
     
    @DataUnit.AsInput(name="input")
    public FilesDataUnit input;
    
    @DataUnit.AsOutput(name="output")
    public WritableFilesDataUnit output;
  
   

    @Override
    protected void innerExecute() throws DPUException {
        ContextUtils.sendShortInfo(ctx, "t-attx-uc1-infras2internal.message");
        
        long pipelineID = ctx.getExecMasterContext().getDpuContext().getPipelineId();
        
        // model to be serialized
        
        Graph g = new LinkedHashModel();
        ValueFactory f = ValueFactoryImpl.getInstance();

        
        try {
            
            // there should be only one output file
            FilesDataUnit.Entry outputEntry = FilesHelper.createFile(output, "infras.ttl");
            FileOutputStream out = new FileOutputStream(FilesHelper.asFile(outputEntry));
            
            Resource type = f.createURI(config.getBaseName() + "types/Infrastructure");


            // get the input files
            Set<FilesDataUnit.Entry> files = FilesHelper.getFiles(input);
            for(FilesDataUnit.Entry entry : files) {
                File file = FilesHelper.asFile(entry);
                // JSON file
                String c = new String(Files.readAllBytes(Paths.get(file.toURI())));
                JSONArray infras = new JSONArray(c);
                for(int i = 0; i < infras.length(); i++) {
                    JSONObject infra = infras.getJSONObject(i);
                    
                    // Using couple of basic properties
                    // urn = base for ID
                    String nameEN = infra.getString("name_EN");
                    String urn = infra.getString("urn");
                    // add statements to the model
                    
                    
                    Resource r = f.createURI(config.getBaseName() + pipelineID + "/work/infra/" + URLEncoder.encode(urn));
                    
                    
                    g.add(f.createStatement(r, RDF.TYPE, type));
                    g.add(f.createStatement(r, DCTERMS.IDENTIFIER, f.createURI(urn)));
                    g.add(f.createStatement(r, DCTERMS.TITLE, f.createLiteral(nameEN)));
                    
                }
                
                  
            }
            
            Rio.write(g, out, RDFFormat.TURTLE);
        
        } catch(Exception ex) {
            // TODO: add more logging
            throw ContextUtils.dpuException(ctx, ex, "infras2internal.error");
        } 
    }
	
}
