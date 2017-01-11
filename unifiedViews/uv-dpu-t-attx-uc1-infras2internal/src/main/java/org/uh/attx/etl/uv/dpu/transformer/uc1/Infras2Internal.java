package org.uh.attx.etl.uv.dpu.transformer.uc1;

import com.vaadin.server.communication.JSONSerializer;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
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
import info.aduna.io.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.logging.Level;
import jdk.nashorn.internal.objects.NativeArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.model.Graph;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;

import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryBase;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import sun.misc.IOUtils;

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
        
        // model to be serialized
        
        Graph g = new LinkedHashModel();
        ValueFactory f = ValueFactoryImpl.getInstance();

        
        try {
            
            // there should be only one output file
            FilesDataUnit.Entry outputEntry = FilesHelper.createFile(output, "infras.ttl");
            FileOutputStream out = new FileOutputStream(FilesHelper.asFile(outputEntry));
            
            Resource type = f.createURI(config.getBaseName() + "types/infrastructure");


            // get the input files
            Set<FilesDataUnit.Entry> files = FilesHelper.getFiles(input);
            for(FilesDataUnit.Entry entry : files) {
                File file = FilesHelper.asFile(entry);
                // JSON file
                String c = IOUtil.readString(file);
                JSONArray infras = new JSONArray(c);
                for(int i = 0; i < infras.length(); i++) {
                    JSONObject infra = infras.getJSONObject(i);
                    
                    // Using couple of basic properties
                    // urn = base for ID
                    String nameFI = infra.getString("name_FI");
                    String urn = infra.getString("urn");
                    // add statements to the model
                    
                    
                    Resource r = f.createURI(config.getBaseName() + "infra/" + URLEncoder.encode(urn));
                    
                    
                    g.add(f.createStatement(r, RDF.TYPE, type));
                    g.add(f.createStatement(r, DCTERMS.IDENTIFIER, f.createLiteral(urn)));
                    g.add(f.createStatement(r, DCTERMS.TITLE, f.createLiteral(nameFI)));
                    
                }
                
                  
            }
            
            Rio.write(g, out, RDFFormat.TURTLE);
        
        } catch(Exception ex) {
            throw ContextUtils.dpuException(ctx, ex, "infras2internal.error");
        } 
    }
	
}
