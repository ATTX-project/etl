/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.hulib.attx.wc.uv.dpu.linker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 *
 * @author jkesanie
 */
public class ATTXClient {
    
    public String gmapiEndpoint;
    public String fusekiEndpoint;
    
    public ATTXClient(String gmapiEndpoint, String fusekiEndpoint) {
        this.gmapiEndpoint = gmapiEndpoint;
        this.fusekiEndpoint = fusekiEndpoint;
                
    }
    
    public List<OptionValue> getLinkingStrategies() {
        List<OptionValue> values = new ArrayList<OptionValue>();
        try {
            HttpResponse<JsonNode> r = Unirest.get("http://" + gmapiEndpoint + "/linkstrategy?graphStore=http://" + fusekiEndpoint).header("accept", "application/json").asJson();
            if(r.getStatus() == 404) {
                return values;
            }
            JSONArray a = r.getBody().getArray();
            for(int i = 0; i < a.length(); i++) {
                String uri = a.getJSONObject(i).getString("uri");
                values.add(new OptionValue(uri, uri));

            }
            return values;
        }catch(Exception ex) {
            ex.printStackTrace();
            values.add(new OptionValue("error", ex.getMessage()));
            return values;
        }
    }
    
    public List<OptionValue> getWorkingGraphs() {
        
        List<OptionValue> values = new ArrayList<OptionValue>();
        try {
            String query = "SELECT DISTINCT ?g ?title\n" +
"WHERE {\n" +
"	GRAPH ?g { ?s ?p ?o }\n" +
"		FILTER(strStarts(str(?g), 'http://data.hulib.helsinki.fi/attx/work'))           \n" +
"	GRAPH <http://data.hulib.helsinki.fi/attx/prov> {\n" +
"	    ?g a <http://data.hulib.helsinki.fi/attx/onto#Dataset> .\n" +
"		?g <http://purl.org/dc/elements/1.1/title> ?title .\n" +
"	}\n" +
"}";
            HttpResponse<JsonNode> r = Unirest.post("http://" + fusekiEndpoint + "/query")
                        .header("Content-Type", "application/sparql-query")
                        .header("Accept", "application/sparql-results+json")
                        .body(query)
                        .asJson();
            if(r.getStatus() == 404) {
                return values;
            }
            JSONArray a = r.getBody().getObject().getJSONObject("results").getJSONArray("bindings");
            for(int i = 0; i < a.length(); i++) {
                String uri = a.getJSONObject(i).getJSONObject("g").getString("value");
                String label = a.getJSONObject(i).getJSONObject("title").getString("value");
                values.add(new OptionValue(uri, label));

            }
            return values;
        }catch(Exception ex) {
            ex.printStackTrace();
            values.add(new OptionValue("error", ex.getMessage()));
            return values;
        }        

    }
}
