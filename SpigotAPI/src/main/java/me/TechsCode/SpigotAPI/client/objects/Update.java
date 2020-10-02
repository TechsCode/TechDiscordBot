package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import org.json.simple.JSONObject;

public class Update extends APIObject {

    public Update(SpigotAPIClient client, JSONObject jsonObject) {
        super(client, jsonObject);
    }

    public Resource getResource(){
        return client.getResources().id(getResourceId()).get();
    }

    public String getId() {
        return getStringProperty("id");
    }

    public String getResourceId() {
        return getStringProperty("resourceId");
    }

    public String getResourceName() {
        return getStringProperty("resourceName");
    }

    public String getTitle() {
        return getStringProperty("title");
    }

    public String[] getImages(){
        return getStringArrayProperty("images");
    }

    public String getDescription() {
        return getStringProperty("description");
    }

    public Time getTime() {
        return new Time(getStringProperty("humanTime"), getLongProperty("unixTime"));
    }
}