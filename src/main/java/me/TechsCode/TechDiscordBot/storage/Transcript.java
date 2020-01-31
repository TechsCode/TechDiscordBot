package me.TechsCode.TechDiscordBot.storage;

public class Transcript {

    private String id;
    private String html;
    private String password;

    public Transcript(String id, String html, String password) {
        this.id = id;
        this.html = html;
        this.password = password;
    }

    public String getId() { return id; }

    public String getHtml() { return html; }

    public String getPassword() { return password; }

}
