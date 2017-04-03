package org.uh.hulib.attx.wc.uv.dpu.linker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Configuration class for ESIndexer.
 *
 * @author ATTX
 */
public class LinkerConfig_V1 {

    private List<OptionValue> inputGraphs = new ArrayList<OptionValue>();
    private String linkStrategy;
    private String outputGraph;
    private String outputGraphLabel;
    private String gmapiEndpoint = "gmapi:4302/0.1";
    private String fusekiEndpoint= "fuseki:3030/ds";

    public String getGmapiEndpoint() {
        return gmapiEndpoint;
    }

    public void setGmapiEndpoint(String gmapiEndpoint) {
        this.gmapiEndpoint = gmapiEndpoint;
    }

    public String getFusekiEndpoint() {
        return fusekiEndpoint;
    }

    public void setFusekiEndpoint(String fusekiEndpoint) {
        this.fusekiEndpoint = fusekiEndpoint;
    }
    
    
    public String getOutputGraph() {
        return outputGraph;
    }

    public void setOutputGraph(String outputGraph) {
        this.outputGraph = outputGraph;
    }

    public String getOutputGraphLabel() {
        return outputGraphLabel;
    }

    public void setOutputGraphLabel(String outputGraphLabel) {
        this.outputGraphLabel = outputGraphLabel;
    }
    
    
    public List<OptionValue> getInputGraphs() {
        return inputGraphs;
    }

    public void setInputGraphs(List<OptionValue> inputGraphs) {
        this.inputGraphs = inputGraphs;
    }

    public String getLinkStrategy() {
        return linkStrategy;
    }

    public void setLinkStrategy(String linkStrategy) {
        this.linkStrategy = linkStrategy;
    }

    
    
}
