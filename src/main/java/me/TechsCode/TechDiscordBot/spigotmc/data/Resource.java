package me.TechsCode.TechDiscordBot.spigotmc.data;

import me.TechsCode.TechDiscordBot.spigotmc.data.lists.PurchasesList;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.ReviewsList;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.UpdatesList;

import java.util.Objects;
import java.util.Optional;

public class Resource {

    private String id, name, tagLine, category, version;
    private Cost cost;
    private Time time;

    public Resource(String id, String name, String tagLine, String category, String version, Cost cost, Time time) {
        this.id = id;
        this.name = name;
        this.tagLine = tagLine;
        this.category = category;
        this.version = version;
        this.cost = cost;
        this.time = time;
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

    public UpdatesList getSpigotUpdates() {
        //return dataset.getSpigotUpdates().resource(id);
        return null;
    }

    public ReviewsList getSpigotReviews() {
        //return dataset.getSpigotReviews().resource(id);
        return null;
    }

    public PurchasesList getSpigotPurchases() {
        //return dataset.getSpigotPurchases().resource(id);
        return null;
    }
}
