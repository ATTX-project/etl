package org.uh.hulib.attx.uv.dpu.apicaller;

/**
 * Configuration class for ESIndexer.
 *
 * @author ATTX
 */
public class APICallerConfig_V1 {

    private String apiEndpoint;
    private String method = "GET";
    private String payload;
    private boolean polling;

    public boolean isPolling() {
        return polling;
    }

    public void setPolling(boolean polling) {
        this.polling = polling;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }



    
    
    
}
