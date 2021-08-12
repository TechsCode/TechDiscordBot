package me.TechsCode.TechDiscordBot.spigotmc.data;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.PurchasesList;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.ReviewsList;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.UpdatesList;

import java.util.Objects;
import java.util.Optional;

public class Resource {

    private String id, name, tagLine, category, version, market;
    private Cost cost;
    private Time time;

    public Resource(String id, String name, String tagLine, String category, String version, Cost cost, Time time, String market) {
        this.id = id;
        this.name = name;
        this.tagLine = tagLine;
        this.category = category;
        this.version = version;
        this.cost = cost;
        this.time = time;
        this.market = market;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getCategory() {
        return category;
    }

    public String getVersion() {
        return version;
    }

    public Optional<Cost> getCost() {
        return Optional.ofNullable(cost);
    }

    public Time getTime() {
        return time;
    }

    public boolean isPremium(){
        return cost != null;
    }

    public boolean isFree(){
        return cost == null;
    }

    public String getIcon() {
        try {
            int resourceId = Integer.parseInt(id);
            return String.format("https://www.spigotmc.org/data/resource_icons/%d/%d.jpg", (int) Math.floor(resourceId / 1000d), resourceId);
        } catch (NumberFormatException ex) {
            return "https://static.spigotmc.org/styles/spigot/xenresource/resource_icon.png";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;
        return id.equals(resource.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UpdatesList getUpdates() {
        if(market.equals("spigot")){
            return TechDiscordBot.getSpigotAPI().getSpigotUpdates().resource(id);
        }else{
            return TechDiscordBot.getSpigotAPI().getMarketUpdates().resource(id);
        }
    }

    public ReviewsList getReviews() {
        if(market.equals("spigot")){
            return TechDiscordBot.getSpigotAPI().getSpigotReviews().resource(id);
        }else{
            return TechDiscordBot.getSpigotAPI().getMarketReviews().resource(id);
        }
    }

    public PurchasesList getPurchases() {
        if(market.equals("spigot")){
            return TechDiscordBot.getSpigotAPI().getSpigotPurchases().resource(id);
        }else{
            return TechDiscordBot.getSpigotAPI().getMarketPurchases().resource(id);
        }
    }
}
