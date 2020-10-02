package me.TechsCode.SpigotAPI.server.data;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Data {

    private long recordTime;
    private HashMap<String, List<Entry>> map;

    public Data(long retrievedTime) {
        this.recordTime = retrievedTime;
        map = new HashMap<>();
    }

    private Data(long retrievedTime, HashMap<String, List<Entry>> map) {
        this.recordTime = retrievedTime;
        this.map = map;
    }

    public void set(String key, List<Entry> data){
        this.map.put(key, data);
    }

    public List<Entry> get(String key){
        return map.get(key);
    }

    public long getRecordTime() {
        return recordTime;
    }

    public HashMap<String, List<Entry>> getAll() {
        return map;
    }

    public Data filter(String key, Object value){
        HashMap<String, List<Entry>> copyOfMap = new HashMap<>();

        map.forEach((dataType, entryList) -> {
            copyOfMap.put(dataType, entryList.stream().filter(entry -> entry.has(key) && (entry.get(key)+"").equalsIgnoreCase(value+"")).collect(Collectors.toList()));
        });

        return new Data(recordTime, copyOfMap);
    }

    public JSONObject toJSONObject(){
        JSONObject jsonObject = new JSONObject();

        map.forEach((dataType, entryList) -> {
            JSONArray jsonArray = new JSONArray();
            entryList.forEach(entry -> jsonArray.add(entry.toJSONObject()));
            jsonObject.put(dataType, jsonArray);
        });

        return jsonObject;
    }
}
