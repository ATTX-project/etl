package org.uh.hulib.attx.wc.uv.dpu.uploader;

/**
 * Configuration class for uv-dpu-l-attx-uploader.
 *
 * @author ATTX
 */
public class UploaderConfig_V1 {

    private String endpointURL = "http://localhost:3030/ds/";

    private String rdfFileFormat = "Auto";

    private String targetGraphName = "";

    private boolean clearDestinationGraph = false;

    public String getEndpointURL() {
        return endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }

    public String getRdfFileFormat() {
        return rdfFileFormat;
    }

    public void setRdfFileFormat(String rdfFileFormat) {
        this.rdfFileFormat = rdfFileFormat;
    }

    public String getTargetGraphName() {
        return targetGraphName;
    }

    public void setTargetGraphName(String targetGraphName) {
        this.targetGraphName = targetGraphName;
    }

    public boolean isClearDestinationGraph() {
        return clearDestinationGraph;
    }

    public void setClearDestinationGraph(boolean clearDestinationGraph) {
        this.clearDestinationGraph = clearDestinationGraph;
    }    

}
