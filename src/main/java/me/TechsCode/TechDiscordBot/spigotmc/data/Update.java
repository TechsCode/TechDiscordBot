package me.TechsCode.TechDiscordBot.spigotmc.data;

import java.util.Objects;

public class Update {

    private String id;
    private String resourceId;
    private String title;
    private String[] images;
    private String description;
    private Time time;

    public Update(String id, String resourceId, String title, String[] images, String description, Time time) {
        this.id = id;
        this.resourceId = resourceId;
        this.title = title;
        this.images = images;
        this.description = description;
        this.title = title;
        this.time = time;
    }

    public Resource getResource(){
        //return dataset.getSpigotResource().id(resourceId).orElse(null);
        return null;
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