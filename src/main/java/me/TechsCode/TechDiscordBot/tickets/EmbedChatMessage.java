package me.TechsCode.TechDiscordBot.tickets;

public class EmbedChatMessage {

    private final String from, title, content, color, avatarURL, timestamp;

    public EmbedChatMessage(String from, String title, String content, String color, String avatarURL, String timestamp) {
        this.from = from;
        this.title = title;
        this.content = content;
        this.color = color;
        this.avatarURL = avatarURL;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getColor() {
        return color;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
