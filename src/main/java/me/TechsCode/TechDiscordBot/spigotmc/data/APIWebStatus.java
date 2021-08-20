package me.TechsCode.TechDiscordBot.spigotmc.data;

public class APIWebStatus {

    private final String lastSpigotFetchDate, lastMarketFetchDate;
    private final long lastSpigotFetch, lastMarketFetch;
    private final boolean spigotFetching, marketFetching;

    public APIWebStatus(boolean spigotFetching, boolean marketFetching, long lastSpigotFetch, long lastMarketFetch, String lastSpigotFetchDate, String lastMarketFetchDate){
        this.spigotFetching = spigotFetching;
        this.marketFetching = marketFetching;
        this.lastSpigotFetch = lastSpigotFetch;
        this.lastMarketFetch = lastMarketFetch;
        this.lastSpigotFetchDate = lastSpigotFetchDate;
        this.lastMarketFetchDate = lastMarketFetchDate;
    }

    public boolean isMarketFetching() {
        return marketFetching;
    }

    public boolean isSpigotFetching() {
        return spigotFetching;
    }

    public long getLastSpigotFetch() {
        return lastSpigotFetch;
    }

    public String getLastSpigotFetchDate() {
        return lastSpigotFetchDate;
    }

    public long getLastMarketFetch() {
        return lastMarketFetch;
    }

    public String getLastMarketFetchDate() {
        return lastMarketFetchDate;
    }

}
