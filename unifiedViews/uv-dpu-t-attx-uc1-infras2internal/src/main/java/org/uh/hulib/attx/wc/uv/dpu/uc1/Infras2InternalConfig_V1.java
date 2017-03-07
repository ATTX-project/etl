package org.uh.hulib.attx.wc.uv.dpu.uc1;

/**
 * Configuration class for t-attx-metadata.
 *
 * @author ATTX
 */
public class Infras2InternalConfig_V1 {

    private String baseName = "http://data.hulib.helsinki.fi/attx/";
    
    public Infras2InternalConfig_V1() {

    }
    
    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }
    
    public String getBaseName() {
        return this.baseName;
    }

}
