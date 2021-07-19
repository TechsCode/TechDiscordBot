package me.TechsCode.TechDiscordBot.util;

public class PluginMarketplace {

    private final String spigotRId, songodaRId, mcMarketRId, polymartRId;

    private PluginMarketplace(String spigotRId, String songodaRId, String mcMarketRId, String polymartRId) {
        this.spigotRId = spigotRId;
        this.songodaRId = songodaRId;
        this.mcMarketRId = mcMarketRId;
        this.polymartRId = polymartRId;
    }

    public static PluginMarketplace of(String spigotRId, String songodaRId, String mcMarketRId, String polymartRId) {
        return new PluginMarketplace(spigotRId, songodaRId, mcMarketRId, polymartRId);
    }

    public String getSpigotResourceId() {
        return spigotRId;
    }

    public String getSpigotResourceUrl() {
        return "https://www.spigotmc.org/resources/" + spigotRId;
    }

    public String getSongodaResourceId() {
        return songodaRId;
    }

    public String getSongodaResourceUrl() {
        return "https://songoda.com/marketplace/product/" + songodaRId;
    }

    public String getMcMarketResourceId() {
        return mcMarketRId;
    }

    public String getMcMarketResourceUrl() {
        return "https://www.mc-market.org/resources/" + mcMarketRId;
    }

    public String getPolymartResourceId() {
        return polymartRId;
    }

    public String getPolymartResourceUrl() {
        return "https://polymart.org/resource/" + polymartRId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(spigotRId != null)
            sb.append("[SpigotMC](").append(getSpigotResourceUrl()).append(")");

        if(songodaRId != null) {
            appendNotEmpty(sb);
            sb.append("[Songoda](").append(getSongodaResourceUrl()).append(")");
        }

        if(mcMarketRId != null) {
            appendNotEmpty(sb);
            sb.append("[MC-Market](").append(getMcMarketResourceUrl()).append(")");
        }

        if(polymartRId != null) {
            appendNotEmpty(sb);
            sb.append("[Polymart](").append(getPolymartResourceUrl()).append(")");
        }

        return sb.toString();
    }

    private void appendNotEmpty(StringBuilder sb) {
        if(!sb.toString().isEmpty())
            sb.append(" **-** ");
    }
}
