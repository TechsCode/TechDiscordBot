package me.TechsCode.TechDiscordBot.tickets;

public class ChatMessage {

    private final String from, content, timestamp, avatarURL;

    public ChatMessage(String from, String content, String timestamp, String avatarURL) {
        this.from = from;
        this.content = content;
        this.timestamp = timestamp;
        this.avatarURL = avatarURL;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAvatarURL() {
        return avatarURL;
    }
}
