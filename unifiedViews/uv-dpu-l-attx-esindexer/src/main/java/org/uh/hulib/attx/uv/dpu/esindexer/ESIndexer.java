package org.uh.hulib.attx.uv.dpu.esindexer;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;
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
public class ESIndexer extends AbstractDpu<ESIndexerConfig_V1> {

    private static final long startDelay = 0;
    private static final long pollingInterval = 5000;
    
    private static final Logger log = LoggerFactory.getLogger(ESIndexer.class);

	public ESIndexer() {
		super(ESIndexerVaadinDialog.class, ConfigHistory.noHistory(ESIndexerConfig_V1.class));
	}
		
    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "ESIndexer.message");
        int mapID = 0;
        Timer timer = new Timer();
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            
            // post the new mapping            
            JSONObject payload = new JSONObject();
            payload.put("plugin", config.getPlugin());
            payload.put("targetEndpoint", config.getTargetEndpoint());
            payload.put("mapping", new JSONObject(config.getMapping()));
            payload.put("sourceGraphs", config.getSourceGraphs());
            payload.put("format", "application/json");
            
            
            ContextUtils.sendShortInfo(ctx, "Posting to" + config.getGraphManagerEndpoint()+ "/map");
            HttpResponse<JsonNode> resp =  Unirest.post(config.getGraphManagerEndpoint() + "/map")
                    .header("accept", "application/json")
                    .body(payload)
                    .asJson();
            
            
            if(resp.getStatus() == 202) {
                ContextUtils.sendShortInfo(ctx, "Post successful");
                // mapping started successfully. 
                mapID = resp.getBody().getObject().getInt("id");
                final int mapID2 = mapID;
                // start polling
                
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        HttpResponse<JsonNode> resp = null;
                        try {
                            ContextUtils.sendShortInfo(ctx, "Polling for " + mapID2);
                            resp = Unirest.get(config.getGraphManagerEndpoint() + "/map/" + mapID2)
                                .header("accept", "application/json")
                                .asJson();
                            
                            String status = resp.getBody().getObject().getString("status");
                            if("DONE".equals(status)) {
                                ContextUtils.sendShortInfo(ctx, "Polling done");
                                ContextUtils.sendShortInfo(ctx, "Mapping done successfully");
                                latch.countDown();
                            }
                            else if("ERROR".equals(status)) {
                                ContextUtils.sendError(ctx, "Mapping failed", resp.getBody().toString());
                                latch.countDown();
                            }
                            
                        }catch(Exception ex) {
                            if(resp != null) {
                                ContextUtils.sendError(ctx, "Polling failed", resp.getBody().toString());
                            }
                            else {
                                ContextUtils.sendError(ctx, "Polling failed", "Unknown error");
                            }
                            latch.countDown();
                            cancel();
                            
                            
                        }
                    }
                }, startDelay, pollingInterval);
                
            }
            else {
                ContextUtils.sendError(ctx, "Starting mapping failed",resp.getBody().toString());
            }
            
            latch.await();
        
        
        }catch(Exception ex) {
            throw ContextUtils.dpuException(ctx, ex, "Mapping failed");
        }finally {
            
            timer.cancel();
            // try to delete the mapID
            if (mapID > 0) {
                try {
                    HttpResponse<String> resp = Unirest.delete(config.getGraphManagerEndpoint() + "/map/" + mapID).asString();
                    if(resp.getStatus() != 200) {
                        ContextUtils.sendError(ctx, "Delete mapping failed",resp.getBody().toString());
                    }
                }catch(Exception ex) {
                    ContextUtils.sendError(ctx, "Delete mapping failed",ex.getMessage(), ex);
                }
                
            }
        }
        
        
    }
	
}
