package me.TechsCode.TechDiscordBot.spigotmc.data;

public class APIWebStatus {

    private final String spigotStatus, marketStatus, lastFetchDate;
    private final int lastFetch, spigotStatusCode, marketStatusCode;

    public APIWebStatus(String spigotStatus, int spigotStatusCode, String marketStatus, int marketStatusCode, int lastFetch, String lastFetchDate){
        this.spigotStatus = spigotStatus;
        this.spigotStatusCode = spigotStatusCode;
        this.marketStatus = marketStatus;
        this.marketStatusCode = marketStatusCode;
        this.lastFetch = lastFetch;
        this.lastFetchDate = lastFetchDate;
    }

    public int getLastFetch() {
        return lastFetch;
    }

    public String getLastFetchDate() {
        return lastFetchDate;
    }

    public String getSpigotStatus() {
        return spigotStatus;
    }

    public int getSpigotStatusCode() {
        return spigotStatusCode;
    }

    public String getMarketStatus() {
        return marketStatus;
    }

    public int getMarketStatusCode() {
        return marketStatusCode;
    }
}
