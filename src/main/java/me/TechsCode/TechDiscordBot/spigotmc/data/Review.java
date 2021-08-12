package me.TechsCode.TechDiscordBot.spigotmc.data;

import java.util.Objects;

public class Review {

    private String id, text, resourceId;
    private User user;
    private int rating;
    private Time time;

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
        //return dataset.getSpigotResource().id(resourceId).orElse(null);
        return null;
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