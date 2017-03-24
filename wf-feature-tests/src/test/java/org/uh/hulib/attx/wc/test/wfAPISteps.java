package org.uh.hulib.attx.wc.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import cucumber.api.java8.En;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import static org.junit.Assert.*;

/**
 * @author stefanne
 */
public class wfAPISteps implements En {
//    PlatformServices s = new PlatformServices(false);
    private final String API_USERNAME = "master";
    private final String API_PASSWORD = "commander";

    private static int notallowed;

    private final String ACTIVITY = "{\n" +
            "    \"debugging\": false,\n" +
            "     \"userExternalId\": \"admin\"\n" +
            "}";

    public wfAPISteps() {
        Given("^the wfAPI is running$", () -> {
            try {
                GetRequest get = Unirest.get(s.getWfapi() + "/0.1/workflow?format=json-ld");
                HttpResponse<JsonNode> response1 = get.asJson();
                int result1 = response1.getStatus();
                assertTrue(result1 >= 200);


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
                assertEquals(405, notallowed);
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
                assertTrue(result1 >= 200);

            } catch (Exception ex) {
                Logger.getLogger(wfAPISteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });

//        This Assumes there is at least one pipeline exists and it has at least one execution and it is public
        Then("^I should get a response with \"([^\"]*)\"\\.$", (String arg1) -> {

            // add pipeline and execution -- might fail, but it doesn' matter
            try {
                URL resource = UnifiedViewsSteps.class.getResource("/testPipeline2.zip");
                HttpResponse<JsonNode> postResponse = Unirest.post(s.getUV() + "/master/api/1/pipelines/import")
                        .header("accept", "application/json")
                        .basicAuth(API_USERNAME, API_PASSWORD)
                        .field("importUserData", false)
                        .field("importSchedule", false)
                        .field("file", new File(resource.toURI()))
                        .asJson();
                JSONObject myObj = postResponse.getBody().getObject();
                int pipeline_id = myObj.getInt("id");
                String exeUrl = String.format(s.getUV() + "/master/api/1/pipelines/%s/executions", pipeline_id);
                HttpResponse<JsonNode> postResponse2 = Unirest.post(exeUrl)
                        .header("accept", "application/json")
                        .header("Content-Type", "application/json")
                        .basicAuth(API_USERNAME, API_PASSWORD)
                        .body(ACTIVITY)
                        .asJson();

                Thread.sleep(2000);

            } catch (Exception ex) {
            }


            try {
                String endpoint = (arg1.equals("activities")) ? "activity" : "workflow";
                String URL = String.format(s.getWfapi() + "/0.1/%s", endpoint);

                Model m = RDFDataMgr.loadModel(URL);

                assertTrue(!m.isEmpty());

            } catch (Exception ex) {
                Logger.getLogger(UnifiedViewsSteps.class.getName()).log(Level.SEVERE, null, ex);
                TestCase.fail(ex.getMessage());
            }
        });


    }
}
