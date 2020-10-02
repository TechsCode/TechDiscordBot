package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import org.json.simple.JSONObject;

public class Purchase extends APIObject {

    public Purchase(SpigotAPIClient client, JSONObject jsonObject) {
        super(client, jsonObject);
    }

    public Resource getResource(){
        return client.getResources().id(getResourceId()).get();
    }

    public User getUser(){
        return new User(client, getUserId(), getUsername(), getAvatarUrl());
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

    public String getUserId() {
        return getStringProperty("userId");
    }

    public String getUsername() {
        return getStringProperty("username");
    }

    public String getAvatarUrl() {
        return getStringProperty("avatarUrl");
    }

    public Time getTime() {
        return new Time(getStringProperty("humanTime"), getLongProperty("unixTime"));
    }

    public Cost getCost() {
        return new Cost(getDoubleProperty("costValue"), "EUR");
    }
}