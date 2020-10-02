package me.TechsCode.SpigotAPI.server;

import fi.iki.elonen.NanoHTTPD;
import me.TechsCode.SpigotAPI.server.data.Data;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Map;

public class APIEndpoint extends NanoHTTPD {

    private DataCollectingThread dataManager;
    private String apiToken;

    public APIEndpoint(DataCollectingThread dataManager, String apiToken) {
        super(3333);

        this.dataManager = dataManager;
        this.apiToken = apiToken;

        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                APIEndpoint.super.stop();
            }
        });
    }

    public JSONObject serve(Map<String, String> params){
        JSONObject root = new JSONObject();

        root.put("status", "SUCCESS");

        Data data = dataManager.getData();

        if(data == null){
            root.put("status", "WAITING");
            root.put("message", "Data is not available yet and currently being fetched.. Check back later");
            return root;
        }

        if(!params.containsKey("token") || !params.get("token").equals(apiToken)){
            root.put("status", "FAILED");
            root.put("message", "API-Token missing or invalid");
            return root;
        }

        if(params.containsKey("resourceId")) data = data.filter("resourceId", params.get("resourceId"));

        if(params.containsKey("resourceName")) data = data.filter("resourceName", params.get("resourceName"));

        if(params.containsKey("userId")) data = data.filter("userId", params.get("userId"));

        if(params.containsKey("username")) data = data.filter("username", params.get("username"));

        root.put("data", data.toJSONObject());

        return root;
    }

    @Override
    public Response serve(IHTTPSession session) {
        return newFixedLengthResponse(serve(session.getParms()).toJSONString().replace("\\/", "/"));
    }
}
