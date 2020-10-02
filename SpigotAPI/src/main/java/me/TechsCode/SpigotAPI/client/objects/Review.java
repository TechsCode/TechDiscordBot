package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import org.json.simple.JSONObject;

public class Review extends APIObject {

    public Review(SpigotAPIClient client, JSONObject jsonObject) {
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

    public String getText() {
        return getStringProperty("text");
    }

    public long getRating() {
        return getLongProperty("rating");
    }

    public Time getTime() {
        return new Time(getStringProperty("humanTime"), getLongProperty("unixTime"));
    }
}