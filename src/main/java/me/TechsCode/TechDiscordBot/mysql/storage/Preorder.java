package me.TechsCode.TechDiscordBot.mysql.storage;

public class Preorder {

    private final String plugin, email, discordName, transactionId;
    private final long discordId;

    public Preorder(String plugin, String email, long discordId, String discordName, String transactionId) {
        this.plugin = plugin;
        this.email = email;
        this.discordId = discordId;
        this.discordName = discordName;
        this.transactionId = transactionId;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getEmail() {
        return email;
    }

    public String getDiscordName() {
        return discordName;
    }

    public long getDiscordId() {
        return discordId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
