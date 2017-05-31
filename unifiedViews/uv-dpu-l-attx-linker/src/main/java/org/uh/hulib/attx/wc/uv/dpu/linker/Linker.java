package org.uh.hulib.attx.wc.uv.dpu.linker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Main data processing unit class.
 *
 * @author ATTX
 */
@DPU.AsLoader
public class Linker extends AbstractDpu<LinkerConfig_V1> {
        
    private static final long START_DELAY = 2000;
    private static final long POLLING_INTERVAL = 5000;

    private static final Logger log = LoggerFactory.getLogger(Linker.class);

    public Linker() {
        super(LinkerVaadinDialog.class, ConfigHistory.noHistory(LinkerConfig_V1.class));
    }

    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "Linker.message");
        
        Timer timer = new Timer();
        final CountDownLatch latch = new CountDownLatch(1);
        int workID = -1;
        try {

            JSONObject payload = new JSONObject();
            JSONObject strategy = new JSONObject();
            strategy.put("uri", config.getLinkStrategy());
            strategy.put("output", config.getOutputGraph());
            
            JSONObject graphStore = new JSONObject();
            JSONObject endpoint = new JSONObject();
            
            String fusekiEndpoint = config.getFusekiEndpoint();
            String host = fusekiEndpoint.substring(0, fusekiEndpoint.indexOf(":"));
            String ds = fusekiEndpoint.substring(fusekiEndpoint.lastIndexOf("/") +1);
            int port = 3030;
            try {
                port = Integer.parseInt(fusekiEndpoint.substring(fusekiEndpoint.indexOf(":") + 1, fusekiEndpoint.lastIndexOf("/")));
            }catch(Exception ex) {
                // do nothing, going with the default value;
            }
            endpoint.put("host", host);
            endpoint.put("port", port);
            endpoint.put("dataset", ds);
            
            
            graphStore.put("endpoint", endpoint);
            JSONArray graphs = new JSONArray();
            for(OptionValue inputGraph : config.getInputGraphs())
                graphs.put(inputGraph.getValue());
                
            if(graphs.length() > 0) {
                graphStore.put("graphs", graphs);
            }
            
            payload.put("strategy", strategy);
            payload.put("graphStore", graphStore);
            
            
            ContextUtils.sendShortInfo(ctx, "Calling link endpoint with payload: ",   payload.toString());
            
            
            HttpResponse<JsonNode> r = Unirest.post("http://" + config.getGmapiEndpoint() + "/link")
                    .header("accept", "application/json")
                    .body(payload)
                    .asJson();
            
            if(r.getStatus() != 202) {
                ContextUtils.sendError(ctx, "Post to link failed with 404", r.getBody().toString());
                return;
            }
                        
            ContextUtils.sendShortInfo(ctx, "Request successful. Starting polling.");                
            final int ID = r.getBody().getObject().getInt("id");
            // start polling - waiting for status = "Done"
            workID = ID;

            timer.schedule(new TimerTask() {
                @Override
                public void run()  {
                    HttpResponse<JsonNode> resp = null;
                    try {
                        ContextUtils.sendShortInfo(ctx, "Polling for " + ID);
                        resp = Unirest.get("http://" + config.getGmapiEndpoint() + "/link/" + ID)
                                .header("accept", "application/json")
                                .asJson();

                        String status = resp.getBody().getObject().getString("status");
                        if (status.equalsIgnoreCase("done")) {
                            ContextUtils.sendShortInfo(ctx, "Polling done");
                            
                            latch.countDown();
                        } else if ("error".equalsIgnoreCase(status)) {
                            ContextUtils.sendError(ctx, "Received error.", resp.getBody().toString());                            
                            latch.countDown();
                            cancel();
                            
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (resp != null) {
                            ContextUtils.sendError(ctx, "Polling failed", resp.getBody().toString());
                        } else {
                            ContextUtils.sendError(ctx, "Polling failed", ex.getMessage());
                        }
                            latch.countDown();
                            cancel();

                    }
                }
            }, START_DELAY, POLLING_INTERVAL);

            latch.await();

        } catch (Exception ex) {
            throw ContextUtils.dpuException(ctx, ex, "Api call failed");
        } finally {

            timer.cancel();
            
            if (workID > 0) {
                try {
                    HttpResponse<String> resp = Unirest.delete("http://" + config.getGmapiEndpoint() + "/link/" + workID).asString();
                    if (resp.getStatus() != 200) {
                        ContextUtils.sendError(ctx, "Delete mapping failed", resp.getBody().toString());
                    }
                } catch (Exception ex) {
                    ContextUtils.sendError(ctx, "Delete mapping failed", ex.getMessage(), ex);
                }

            }            
        }

    }

}
