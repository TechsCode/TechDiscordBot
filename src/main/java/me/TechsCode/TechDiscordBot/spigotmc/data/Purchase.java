package me.TechsCode.TechDiscordBot.spigotmc.data;

import com.google.gson.JsonObject;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

import java.util.Objects;
import java.util.Optional;

public class Purchase {

    private String resourceId;
    private User user;
    private Time time;
    private Cost cost;

    public Purchase(String resourceId, User user, Time time, Cost cost) {
        this.resourceId = resourceId;
        this.user = user;
        this.time = time;
        this.cost = cost;
    }

    public Purchase(JsonObject state) {
    }

    public Resource getResource(){
        return TechDiscordBot.getSpigotAPI().getSpigotResources().id(resourceId).orElse(null);
    }

    public User getUser() {
        return user;
    }

    public Time getTime() {
        return time;
    }

    public Optional<Cost> getCost() {
        return Optional.ofNullable(cost);
    }

    public boolean isPurchased(){
        return cost != null;
    }

    public boolean isGifted(){
        return cost == null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;

        return resourceId.equals(purchase.resourceId) &&
                user.getUserId().equals(purchase.user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, user);
    }
}