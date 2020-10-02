package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class APIObject {

    protected SpigotAPIClient client;
    private JSONObject jsonObject;

    public APIObject(SpigotAPIClient client, JSONObject jsonObject) {
        this.client = client;
        this.jsonObject = jsonObject;
    }

    protected Object getProperty(String property){
        return jsonObject.get(property);
    }

    protected String getStringProperty(String property){
        return (String) jsonObject.get(property);
    }

    protected String[] getStringArrayProperty(String property){
        return jsonArrayToStringArray((JSONArray)jsonObject.get(property));
    }

    protected long getLongProperty(String property){
        return (long) jsonObject.get(property);
    }

    protected long getIntProperty(String property){
        return (int)(long) jsonObject.get(property);
    }

    protected double getDoubleProperty(String property){
        if(jsonObject.get(property) == null) return 0d;
        return (double) jsonObject.get(property);
    }

    private String[] jsonArrayToStringArray(JSONArray jsonArray) {
        int arraySize = jsonArray.size();
        String[] stringArray = new String[arraySize];

        for(int i=0; i<arraySize; i++) stringArray[i] = (String) jsonArray.get(i);

        return stringArray;
    }
}
