package org.uh.hulib.attx.uv.dpu.apicaller;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import org.json.JSONObject;

/**
 * Main data processing unit class.
 *
 * @author ATTX
 */
@DPU.AsLoader
public class APICaller extends AbstractDpu<APICallerConfig_V1> {

    private static final long startDelay = 0;
    private static final long pollingInterval = 5000;

    private static final Logger log = LoggerFactory.getLogger(APICaller.class);

    public APICaller() {
        super(APICallerVaadinDialog.class, ConfigHistory.noHistory(APICallerConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "APICaller.message");
        
        Timer timer = new Timer();
        final CountDownLatch latch = new CountDownLatch(1);
        try {

            JSONObject payload = null;
            // post the new mapping       
            if(config.getPayload() != null && !"".equals(config.getPayload()))
                payload = new JSONObject(config.getPayload());

            ContextUtils.sendShortInfo(ctx, "Using method " + config.getMethod() + " to request " + config.getApiEndpoint());
            
            HttpResponse r = null;
            String method = config.getMethod();
            if("GET".equals(method)) {
                r = Unirest.get(config.getApiEndpoint()).header("accept", "application/json").asJson();
                    
            }
            else if("DELETE".equals(method)) {
                r = Unirest.delete(config.getApiEndpoint()).header("accept", "application/json").asJson();
            }
            else if("POST".equals(method)) {
                HttpRequestWithBody rq = Unirest.post(config.getApiEndpoint()).header("accept", "application/json");
                if(payload !=null)
                    rq.body(payload);
                r = rq.asJson();
                
            }
            else if("PUT".equals(method)) {
                HttpRequestWithBody rq = Unirest.put(config.getApiEndpoint()).header("accept", "application/json");
                if(payload !=null)
                    rq.body(payload);
                r = rq.asJson();
                
            }    
            else {
                ContextUtils.sendShortInfo(ctx, "No method defined, cancelling request");
                return;
            }
            
            if (r.getStatus() == 202) {
                ContextUtils.sendShortInfo(ctx, "Request successful");
                if(!config.isPolling()) {
                    return;
                }
                // mapping started successfully. 
                if("GET".equals(method) || "DELETE".equals(method)) {
                    return;
                }
                HttpResponse<JsonNode> resp = (HttpResponse<JsonNode>)r;
                
                final int ID = resp.getBody().getObject().getInt("id");
                // start polling - waiting for status = "Done"

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        HttpResponse<JsonNode> resp = null;
                        try {
                            ContextUtils.sendShortInfo(ctx, "Polling for " + ID);
                            resp = Unirest.get(config.getApiEndpoint()+ "/" + ID)
                                    .header("accept", "application/json")
                                    .asJson();

                            String status = resp.getBody().getObject().getString("status");
                            if (status.equalsIgnoreCase("done")) {
                                ContextUtils.sendShortInfo(ctx, "Polling done");
                                latch.countDown();
                            } else if ("ERROR".equals(status)) {
                                ContextUtils.sendError(ctx, "Polling failed", resp.getBody().toString());
                                latch.countDown();
                            }

                        } catch (Exception ex) {
                            if (resp != null) {
                                ContextUtils.sendError(ctx, "Polling failed", resp.getBody().toString());
                            } else {
                                ContextUtils.sendError(ctx, "Polling failed", "Unknown error");
                            }
                            latch.countDown();
                            cancel();

                        }
                    }
                }, startDelay, pollingInterval);

            } else {
                ContextUtils.sendError(ctx, "API call failed", r.getStatusText());
            }

            latch.await();

        } catch (Exception ex) {
            throw ContextUtils.dpuException(ctx, ex, "Api call failed");
        } finally {

            timer.cancel();
        }

    }

}
