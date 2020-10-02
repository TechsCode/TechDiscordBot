package me.TechsCode.SpigotAPI.server.data;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Entry {

    private Map<String, Object> data;

    public Entry() {
        data = new HashMap<>();
    }

    public Entry set(String key, Object value){
        data.put(key, value);
        return this;
    }

    public void setCost(String costString){
        if(costString == null) return;

        double value = Double.parseDouble(costString.split(" ")[0]);
        String currency = costString.split(" ")[1];

        set("costValue", value);
        set("costCurrency", currency);
    }

    public boolean has(String key){
        return data.containsKey(key);
    }

    public Object get(String key) { return data.get(key); }

    public String getString(String key){
        return (String) get(key);
    }

    public Map<String, Object> getAll() {
        return data;
    }

    public JSONObject toJSONObject() {
        return new JSONObject(data);
    }
}
