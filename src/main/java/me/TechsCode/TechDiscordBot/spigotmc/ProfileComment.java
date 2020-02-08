package me.TechsCode.TechDiscordBot.spigotmc;

public class ProfileComment {

    private String userId;
    private String message;

    public ProfileComment(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return message;
    }
}
