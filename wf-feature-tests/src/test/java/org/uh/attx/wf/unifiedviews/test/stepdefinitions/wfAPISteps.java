package org.uh.attx.wf.unifiedviews.test.stepdefinitions;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import cucumber.api.java8.En;
import junit.framework.TestCase;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author stefanne
 */
public class wfAPISteps implements En {
    PlatformServices s = new PlatformServices(false);
    private int notallowed;

    public wfAPISteps() {
        Given("^the wfAPI is running$", () -> {
            try {
                GetRequest get = Unirest.get(s.getWfapi() + "/0.1/workflow?format=json-ld");
                HttpResponse<JsonNode> response1 = get.asJson();
                int result1 = response1.getStatus();
                assertEquals(result1, 200);

            } catch (Exception ex) {
                Logger.getLogger(wfAPISteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }

        });

        When("^I access the \"([^\"]*)\" API and try to send something$", (String arg1) -> {
            try {
                String URL = String.format(s.getWfapi() + "/0.1/%s", arg1);
                HttpResponse<String> postResponse = Unirest.post(URL)
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .asString();

                HttpResponse<String> response1 = postResponse;
                notallowed = response1.getStatus();

            } catch (Exception ex) {
                Logger.getLogger(wfAPISteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        Then("^I should see that operation is Not allowed\\.$", () -> {
            try {
                assertEquals(notallowed, 405);
            } catch (Exception ex) {
                Logger.getLogger(wfAPISteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });


        When("^I access the \"([^\"]*)\" API and try to retrieve something$", (String arg1) -> {
            try {
            String URL = String.format(s.getWfapi() + "/0.1/%s", arg1);
            GetRequest get = Unirest.get(URL);
            HttpResponse<String> response1 = get.asString();
            int result1 = response1.getStatus();
            assertEquals(result1, 200);

            } catch (Exception ex) {
                Logger.getLogger(wfAPISteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

//        This Assumes there is at least one pipeline exists and it has at least one execution and it is public
        Then("^I should get a response with \"([^\"]*)\"\\.$", (String arg1) -> {
            String activity_query =  String.format("PREFIX kaisa: <http://helsinki.fi/library/onto#> " +
                    "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "ASK{ " +
                    "  kaisa:activity%d rdf:type ?object" +
                    "}", 1);
            String workflow_query =  String.format("PREFIX kaisa: <http://helsinki.fi/library/onto#> " +
                    "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "ASK{ " +
                    "  kaisa:workflow%d rdf:type ?object" +
                    "}", 1);

//            Model m = ModelFactory.createDefaultModel();
//            m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
//            m.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
//            m.setNsPrefix("kaisa", "http://helsinki.fi/library/onto#");
//            m.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
//            m.setNsPrefix("pwo", "http://purl.org/spar/pwo/");
//            m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//            m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
//            m.setNsPrefix("schema", "http://schema.org/");
//            m.setNsPrefix("sd", "http://www.w3.org/ns/sparql-service-description#");
//            m.setNsPrefix("xml", "http://www.w3.org/XML/1998/namespace");
//            m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

            try {
                String the_query = (arg1 == "activities") ? activity_query : workflow_query;
                String endpoint = (arg1 == "activities") ? "activity" : "workflow";
                String URL = String.format(s.getWfapi() + "/0.1/%s", endpoint);

                Model m = RDFDataMgr.loadModel(URL);
                Query query = QueryFactory.create(the_query);
                QueryExecution qexec = QueryExecutionFactory.create(query, m);
                Boolean result = qexec.execAsk();
                assertTrue(result);
                qexec.close() ;
            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });


    }
}
