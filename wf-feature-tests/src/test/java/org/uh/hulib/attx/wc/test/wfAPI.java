package org.uh.hulib.attx.wc.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

import java.net.URL;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;
import org.uh.hulib.attx.dev.TestUtils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * @author stefanne
 */
public class wfAPI {

    @Test
    public void testWFHealthAvailable() {
        try {

            TestUtils.testWfHealth();

            String workflowURL = String.format(TestUtils.getWfapi() + "/0.1/workflow");
            HttpResponse<String> postResponseWrf = Unirest.post(workflowURL)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .asString();

            HttpResponse<String> responseWorkflow = postResponseWrf;
            int notallowed = responseWorkflow.getStatus();
            assertEquals(405, notallowed);

            GetRequest getWorkflow = Unirest.get(workflowURL);
            HttpResponse<String> responseWrf = getWorkflow.asString();
            int resultWrf = responseWrf.getStatus();
            assertTrue(resultWrf >= 200);


            String activityURL = String.format(TestUtils.getWfapi() + "/0.1/activity");
            HttpResponse<String> postResponseAct = Unirest.post(activityURL)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .asString();

            HttpResponse<String> responseActivity = postResponseAct;
            notallowed = responseActivity.getStatus();
            assertEquals(405, notallowed);

            GetRequest getActivity = Unirest.get(activityURL);
            HttpResponse<String> responseAct = getActivity.asString();
            int resultAct = responseAct.getStatus();
            assertTrue(resultAct >= 200);

        } catch (Exception ex) {
            Logger.getLogger(wfAPI.class.getName()).log(Level.SEVERE, null, ex);
            TestCase.fail(ex.getMessage());
        }
    }

    @Test
    public void endpointsData() {
        try {
            URL resource = wfAPI.class.getResource("/testPipeline.zip");
            int pipelineID = TestUtils.importPipeline(resource);

            await().atMost(20, TimeUnit.SECONDS).until(TestUtils.pollForWorkflowStart(pipelineID), equalTo(200));
            await().atMost(20, TimeUnit.SECONDS).until(TestUtils.pollForWorkflowExecution(pipelineID), equalTo("FINISHED_SUCCESS"));

            String ActURL = String.format(TestUtils.getWfapi() + "/0.1/activity");

            Model m1 = RDFDataMgr.loadModel(ActURL);

            assertTrue(!m1.isEmpty());

            String WrfURL = String.format(TestUtils.getWfapi() + "/0.1/workflow");

            Model m2 = RDFDataMgr.loadModel(WrfURL);

            assertTrue(!m2.isEmpty());

        } catch (Exception ex) {
            Logger.getLogger(wfAPI.class.getName()).log(Level.SEVERE, null, ex);
            TestCase.fail(ex.getMessage());
        }
    }


}
