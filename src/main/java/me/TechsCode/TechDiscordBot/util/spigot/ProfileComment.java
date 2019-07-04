package me.TechsCode.TechDiscordBot.util.spigot;

public class ProfileComment {

    private String text;
    private String userId;

    public ProfileComment(String text, String userId) {
        this.text = text;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public String getUserId() {
        return userId;
    }
}
