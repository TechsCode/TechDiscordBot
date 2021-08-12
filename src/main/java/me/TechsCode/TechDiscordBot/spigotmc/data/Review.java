package me.TechsCode.TechDiscordBot.spigotmc.data;

import me.TechsCode.TechDiscordBot.TechDiscordBot;

import java.util.Objects;

public class Review {

    private final String id, text, resourceId;
    private final User user;
    private final int rating;
    private final Time time;

    public Review(String id, String resourceId, User user, String text, int rating, Time time) {
        this.id = id;
        this.resourceId = resourceId;
        this.user = user;
        this.text = text;
        this.rating = rating;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public Resource getResource() {
        return TechDiscordBot.getSpigotAPI().getSpigotResources().id(resourceId).orElse(null);
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public Time getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;
        return id.equals(review.id) &&
                resourceId.equals(review.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }
}