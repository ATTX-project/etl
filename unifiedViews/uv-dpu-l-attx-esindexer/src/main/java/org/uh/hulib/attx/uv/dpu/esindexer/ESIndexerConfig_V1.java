package org.uh.hulib.attx.uv.dpu.esindexer;

/**
 * Configuration class for ESIndexer.
 *
 * @author ATTX
 */
public class ESIndexerConfig_V1 {

    private String graphManagerEndpoint;

    private String targetEndpoint;
    private String plugin;
    private String mapping;
    private String sourceGraphs;

    public String getTargetEndpoint() {
        return targetEndpoint;
    }

    public void setTargetEndpoint(String targetEndpoint) {
        this.targetEndpoint = targetEndpoint;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getSourceGraphs() {
        return sourceGraphs;
    }

    public void setSourceGraphs(String sourceGraphs) {
        this.sourceGraphs = sourceGraphs;
    }

    public String getGraphManagerEndpoint() {
        return graphManagerEndpoint;
    }

    public void setGraphManagerEndpoint(String graphManagerEndpoint) {
        this.graphManagerEndpoint = graphManagerEndpoint;
    }
    
    
    
}
