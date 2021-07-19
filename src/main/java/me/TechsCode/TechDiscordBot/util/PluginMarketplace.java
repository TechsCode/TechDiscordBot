package me.TechsCode.TechDiscordBot.util;

public class PluginMarketplace {

    private final String spigotRId, songodaRId, polymartRId;

    private PluginMarketplace(String spigotRId, String songodaRId, String polymartRId) {
        this.spigotRId = spigotRId;
        this.songodaRId = songodaRId;
        this.polymartRId = polymartRId;
    }

    public static PluginMarketplace of(String spigotRId, String songodaRId, String polymartRId) {
        return new PluginMarketplace(spigotRId, songodaRId, polymartRId);
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
        appendNotEmpty(sb);

        if(songodaRId != null)
            sb.append("[Songoda](").append(getSongodaResourceUrl()).append(")");
        appendNotEmpty(sb);

        if(polymartRId != null)
            sb.append("[Polymart](").append(getPolymartResourceUrl()).append(")");

        return sb.toString();
    }

    private void appendNotEmpty(StringBuilder sb) {
        if(!sb.toString().isEmpty())
            sb.append("**-** ");
    }
}
