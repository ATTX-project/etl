/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.hulib.attx.wc.test;

/**
 * @author jkesanie
 */
public class PlatformServices {

    private final String UV = "http://frontend";
    private final int UV_PORT = 8080;

    private final String WFAPI = "http://wfapi";
    private final int WFAPI_PORT = 4301;

    private boolean isLocalhost = false;

    public PlatformServices() {
    }

    public PlatformServices(boolean isLocalhost) {
        this.isLocalhost = isLocalhost;
    }


    public String getUV() {
        return new ServerAddress(System.getProperty("mongo.host"), Integer.parseInt(System.getProperty("mongo.port")));
//        if (isLocalhost) {
//            return "http://localhost:" + UV_PORT;
//        } else {
//            return UV + ":" + UV_PORT;
//        }
    }

    public String getWfapi() {
//        if (isLocalhost) {
//            return "http://localhost:" + WFAPI_PORT;
//        } else {
//            return WFAPI + ":" + WFAPI_PORT;
//        }
    }

}
