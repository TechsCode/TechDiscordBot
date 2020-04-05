package me.TechsCode.TechDiscordBot.client;

public abstract class APIClient extends Thread {

    private String token;

    public APIClient(String token) {
        this.token = token;
        start();
    }

    public String getToken() {
        return token;
    }
}