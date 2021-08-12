package me.TechsCode.TechDiscordBot.spigotmc.data;

import com.google.gson.JsonObject;

public class User {

    private final String userId, username, avatar;

    public User(JsonObject jsonObject){
        this.userId = jsonObject.get("userId").getAsString();
        this.username = jsonObject.get("username").getAsString();
        this.avatar = jsonObject.get("avatar").getAsString();
    }

    public User(String userId, String username, String avatar) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("avatar", avatar);
        return jsonObject;
    }
}
