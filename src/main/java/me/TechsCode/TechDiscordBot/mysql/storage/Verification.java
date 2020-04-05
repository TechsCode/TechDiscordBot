package me.TechsCode.TechDiscordBot.mysql.storage;

public class Verification {

    private Storage storage;
    private String userId, discordId;

    public Verification(Storage storage, String userId, String discordId) {
        this.storage = storage;
        this.userId = userId;
        this.discordId = discordId;
    }

    public String getUserId() { return userId; }

    public String getDiscordId() { return discordId; }

    public void delete() { storage.removeVerification(this); }
}
