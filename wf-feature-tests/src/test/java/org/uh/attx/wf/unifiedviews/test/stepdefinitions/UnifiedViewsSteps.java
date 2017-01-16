/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.wf.unifiedviews.test.stepdefinitions;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import cucumber.api.java8.En;
import junit.framework.TestCase;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.query.Dataset ;
import org.apache.jena.sparql.core.DatasetGraph;
import org.json.JSONObject;
import static org.apache.jena.riot.RDFLanguages.TURTLE ;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.jena.vocabulary.DCAT.dataset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author stefanne
 */
public class UnifiedViewsSteps implements En {
    private final String API_USERNAME = "master";
    private final String API_PASSWORD = "commander";
    private int workflow_id = 3;
    private int activity_id;
    private final String PIPELINE = "{ \n" +
            "\t\"name\": \"Pipeline\", \n" +
            "\t\"description\": \"Description\", \n" +
            "\t\"userExternalId\": \"http://www.johnadmin.cz\"\n" +
            "}";
    private final String ACTIVITY = "{\n" +
            "    \"debugging\": false,\n" +
            "     \"userExternalId\": \"http://www.johnadmin.cz\"\n" +
            "}";

    public UnifiedViewsSteps() {
        Given("^the UnifiedViews is running$", () -> {
            try {
                GetRequest get = Unirest.get("http://localhost:8080/master/api/1/pipelines/visible?userExternalId=organization_ext_id").basicAuth(API_USERNAME, API_PASSWORD).header("accept", "application/json");
                HttpResponse<JsonNode> response = get.asJson();
                int result = response.getStatus();
                assertEquals(result, 200);

            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        When("^we add a new pipeline", () -> {
            try {
//                waiting for 2.3.1 UnifiedViews to test this
//                HttpResponse<JsonNode> postResponse = Unirest.post("http://localhost:8080/master/api/1/pipelines")
//                        .header("accept", "application/json")
//                        .header("Content-Type", "application/json")
//                        .basicAuth(API_USERNAME, API_PASSWORD)
//                        .body(PIPELINE)
//                        .asJson();
//
//                JSONObject myObj = postResponse.getBody().getObject();
//                pipeline_id = myObj.getString("id");

                String URL = String.format("http://localhost:8080/master/api/1/pipelines/%s", workflow_id);
                GetRequest get = Unirest.get(URL).basicAuth(API_USERNAME, API_PASSWORD).header("accept", "application/json");
                HttpResponse<JsonNode> response = get.asJson();
                JSONObject myObj = response.getBody().getObject();
                int the_id = myObj.getInt("id");
                assertEquals(the_id, 3);

            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        When("^we run a pipeline", () -> {
            try {
                String URL = String.format("http://localhost:8080/master/api/1/pipelines/%s/executions", workflow_id);
                HttpResponse<JsonNode> postResponse = Unirest.post(URL)
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .basicAuth(API_USERNAME, API_PASSWORD)
                        .body(ACTIVITY)
                        .asJson();

                JSONObject myObj = postResponse.getBody().getObject();
                activity_id = myObj.getInt("id");

            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        Then("^we get the \"([^\"]*)\" from wfAPI\\.$", (String arg1) -> {
            String activity_query =  String.format("PREFIX kaisa: <http://helsinki.fi/library/onto#> " +
                    "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "ASK{ GRAPH <http://localhost:3030/ds/data/provenance>{" +
                    "  kaisa:activity%d rdf:type ?object" +
                    "	} " +
                    "}", activity_id);
            String workflow_query =  String.format("PREFIX kaisa: <http://helsinki.fi/library/onto#> " +
                    "PREFIX prov: <http://www.w3.org/ns/prov#> " +
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                    "ASK{ GRAPH <http://localhost:3030/ds/data/provenance>{" +
                    "  kaisa:workflow%d rdf:type ?object" +
                    "	} " +
                    "}", workflow_id);

            Model m = ModelFactory.createDefaultModel();;
            m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
            m.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
            m.setNsPrefix("kaisa", "http://helsinki.fi/library/onto#");
            m.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
            m.setNsPrefix("pwo", "http://purl.org/spar/pwo/");
            m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            m.setNsPrefix("schema", "http://schema.org/");
            m.setNsPrefix("sd", "http://www.w3.org/ns/sparql-service-description#");
            m.setNsPrefix("xml", "http://www.w3.org/XML/1998/namespace");
            m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

            try {
                String the_query = (arg1 == "activity") ? activity_query : workflow_query;
                String URL = String.format("http://localhost:4301/v0.1/%s", arg1);
                GetRequest get = Unirest.get(URL);
                HttpResponse<String> response1 = get.asString();
                Dataset dataset = DatasetFactory.create(m);

                RDFDataMgr.read(dataset, response1.getRawBody(), TURTLE);

                Query query = QueryFactory.create(the_query);
                QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
                Boolean result = qexec.execAsk();
                System.out.println(result);
//                assertTrue(result);
                qexec.close() ;
            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });


    }
    
}
