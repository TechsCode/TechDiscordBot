package me.TechsCode.TechDiscordBot.mysql.storage;

public class Transcript {

    private String channelId;
    private String html;
    private String password;

    public Transcript(String channelId, String html, String password) {
        this.channelId = channelId;
        this.html = html;
        this.password = password;
    }

    public String getChannelId() { return channelId; }

    public String getHtml() { return html; }

    public String getPassword() { return password; }

}