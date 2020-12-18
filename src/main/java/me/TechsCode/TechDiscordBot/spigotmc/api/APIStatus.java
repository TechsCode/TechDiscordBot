package me.TechsCode.TechDiscordBot.spigotmc.api;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

import java.util.concurrent.TimeUnit;

public enum APIStatus {

    ONLINE("Online", "The api is online and running!", "low_priority"),
    NOT_FETCHING("Not Fetching", "The api is online, but is not fetching new info!", "medium_priority"),
    OFFLINE("Offline", "The api has no information. Something bad must've happened.", "high_priority");

    private final String status, description, emojiName;

    APIStatus(String status, String description, String emojiName) {
        this.status = status;
        this.description = description;
        this.emojiName = emojiName;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUsable() {
        return this == ONLINE || this == NOT_FETCHING;
    }

    public String getEmoji() {
        return TechDiscordBot.getGuild().getEmotesByName(emojiName, true).get(0).getAsMention();
    }

    public static APIStatus getStatus(SpigotAPIClient client) {
        APIStatus status;

        if(client.getData().isPresent() && client.getRefreshTime() != 0L) {
            if(client.getRefreshTime() + TimeUnit.HOURS.toMillis(1) < System.currentTimeMillis()) {
                status = NOT_FETCHING;
            } else {
                status = ONLINE;
            }
        } else {
            status = OFFLINE;
        }

        return status;
    }
}
