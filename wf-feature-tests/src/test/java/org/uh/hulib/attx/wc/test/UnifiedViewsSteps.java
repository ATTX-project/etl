/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.hulib.attx.wc.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import cucumber.api.java8.En;

import java.io.File;
import java.io.StringReader;
import java.net.URL;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import static org.junit.Assert.*;


/**
 * @author stefanne
 */
public class UnifiedViewsSteps implements En {
    PlatformServices s = new PlatformServices();

    private static int pipeline_id = -1;
    private static int execution_id = -1;

    private final String API_USERNAME = "master";
    private final String API_PASSWORD = "commander";
    private final String ACTIVITY = "{ \"debugging\" : \"false\", \"userExternalId\" : \"admin\" }";

    public UnifiedViewsSteps() {


        Given("^the UnifiedViews is running$", () -> {
            try {
                GetRequest get = Unirest.get(s.getUV() + "/master/api/1/pipelines/visible?userExternalId=organization_ext_id").basicAuth(API_USERNAME, API_PASSWORD).header("accept", "application/json");
                HttpResponse<JsonNode> response = get.asJson();
                int result = response.getStatus();
                assertEquals(200, result);

            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        When("^we add a new pipeline", () -> {
            try {

                URL resource = UnifiedViewsSteps.class.getResource("/testPipeline1.zip");
                HttpResponse<JsonNode> postResponse = Unirest.post(s.getUV() + "/master/api/1/pipelines/import")
                        .header("accept", "application/json")
                        .basicAuth(API_USERNAME, API_PASSWORD)
                        .field("importUserData", false)
                        .field("importSchedule", false)
                        .field("file", new File(resource.toURI()))

                        .asJson();
                /*
                {
  "id": 1,
  "name": "test",
  "description": "test",
  "userExternalId": "http://www.johnadmin.cz",
  "userActorExternalId": null
}
                */

                JSONObject myObj = postResponse.getBody().getObject();
                pipeline_id = myObj.getInt("id");
                assertTrue(pipeline_id > 0);


            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        When("^we run a pipeline", () -> {
            try {

                System.out.println("PipelineID: " + this.pipeline_id);
                String URL = String.format(s.getUV() + "/master/api/1/pipelines/%s/executions", this.pipeline_id);
                HttpResponse<JsonNode> postResponse = Unirest.post(URL)
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .basicAuth(API_USERNAME, API_PASSWORD)
                        .body(ACTIVITY)
                        .asJson();

                /*
                {
  "id": 1,
  "status": "QUEUED",
  "orderNumber": 1,
  "start": null,
  "end": null,
  "schedule": null,
  "stop": false,
  "lastChange": "2017-02-28T10:39:01.705+0000",
  "userExternalId": "http://www.johnadmin.cz",
  "userActorExternalId": null,
  "debugging": false
}
                */
                JSONObject myObj = postResponse.getBody().getObject();
                execution_id = myObj.getInt("id");
                assertTrue(execution_id > 0);


            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

        Then("^we get the \"([^\"]*)\" from wfAPI\\.$", (String arg1) -> {
            try {
                // wait for success
                Thread.sleep(2000);

                String URL = s.getWfapi() + "/0.1/workflow";
                HttpResponse<String> response = Unirest.get(URL)
                        .asString();
                String rdf = response.getBody();
                Model model = ModelFactory.createDefaultModel();
                model.read(new StringReader(rdf), "http://data.hulib.helsinki.fi/attx/", "TURTLE");
                Property prop = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                Resource obs = ResourceFactory.createResource("http://data.hulib.helsinki.fi/attx/onto#Workflow");

                assertTrue(!model.isEmpty());


            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });


    }


}
