package me.TechsCode.TechDiscordBot.spigotmc.data;

import me.TechsCode.TechDiscordBot.TechDiscordBot;

import java.util.Objects;

public class Update {

    private final String id;
    private final String resourceId;
    private final String title;
    private final String[] images;
    private final String description;
    private final Time time;

    public Update(String id, String resourceId, String title, String[] images, String description, Time time) {
        this.id = id;
        this.resourceId = resourceId;
        this.title = title;
        this.images = images;
        this.description = description;
        this.time = time;
    }

    public Resource getResource(){
        return TechDiscordBot.getSpigotAPI().getSpigotResources().id(resourceId).orElse(null);
    }

    public String getId() {
        return id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }

    public String[] getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public Time getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Update update = (Update) o;
        return id.equals(update.id) &&
                resourceId.equals(update.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, resourceId);
    }
}