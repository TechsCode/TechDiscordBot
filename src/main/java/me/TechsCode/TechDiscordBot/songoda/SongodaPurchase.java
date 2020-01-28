package me.TechsCode.TechDiscordBot.songoda;

public class SongodaPurchase {

    private String name;
    private String discord;

    public SongodaPurchase(String name, String discord) {
        this.name = name;
        this.discord = discord;
    }

    public String getName() { return name; }

    public String getDiscord() { return discord; }
}
