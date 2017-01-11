/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.etl.uv.dpu.transformer.uc1;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dpu.test.config.ConfigurationBuilder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.turtle.TurtleParser;
import org.uh.attx.etl.uv.dpu.transformer.uc1.Infras2Internal;
import org.uh.attx.etl.uv.dpu.transformer.uc1.Infras2InternalConfig_V1;

/**
 *
 * @author jkesanie
 */
public class Infras2InternalTest {

    
    private static String baseName = "http://data.hulib.helsinki.fi/attx/";
    
    public Infras2InternalTest() {
    }


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void execute() throws Exception {
        
        Infras2InternalConfig_V1 config = new Infras2InternalConfig_V1();
        Infras2Internal plugin = new Infras2Internal();
        plugin.configure((new ConfigurationBuilder()).setDpuConfiguration(config).toString());

        TestEnvironment environment = new TestEnvironment();

        WritableFilesDataUnit filesInput = environment.createFilesInput("input");
        WritableFilesDataUnit filesOutput = environment.createFilesOutput("output");

        try {
            URL url = getClass().getClassLoader().getResource("infras.json");
            FilesHelper.addFile(filesInput, new File(url.toURI()));

            environment.run(plugin);

            Iterator<FilesDataUnit.Entry> outputEntries = FilesHelper.getFiles(filesOutput).iterator();
            if (!outputEntries.hasNext()) {
                fail("No output files available");
            }
            while (outputEntries.hasNext()) {
                FilesDataUnit.Entry outputEntry = outputEntries.next();
                URL documentURI = new URL(outputEntry.getFileURIString());
                InputStream in = documentURI.openStream();

                ValueFactory f = ValueFactoryImpl.getInstance();
                Model m = Rio.parse(in, documentURI.toString(), RDFFormat.TURTLE);

                String existingURN = "urn:nbn:fi:research-infras-201607251";
                String existingNameFI = "Eurooppalainen sosiaalitutkimus";
                Resource r = f.createURI(baseName + "infra/" + URLEncoder.encode(existingURN));
                assertTrue(m.contains(r, RDF.TYPE, f.createURI(baseName + "types/infrastructure")));
                assertTrue(m.contains(r, DCTERMS.IDENTIFIER, f.createLiteral(existingURN)));
                assertTrue(m.contains(r, DCTERMS.TITLE, f.createLiteral(existingNameFI)));


                in.close();

            }

        } finally {
            // Release resources.
            environment.release();
        }

    }
}
