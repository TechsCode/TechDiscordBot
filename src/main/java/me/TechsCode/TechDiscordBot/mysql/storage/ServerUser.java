package me.TechsCode.TechDiscordBot.mysql.storage;

import com.stanjg.ptero4j.entities.panel.admin.User;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

public class ServerUser {

    private Storage storage;
    private int userId;
    private String discordId;

    public ServerUser(Storage storage, int userId, String discordId) {
        this.storage = storage;
        this.userId = userId;
        this.discordId = discordId;
    }

    public int getUserId() {
        return userId;
    }

    public String getDiscordId() {
        return discordId;
    }

    public User getPteroUser() {
        return TechDiscordBot.getPteroAdminAPI().getUsersController().getUser(getUserId());
    }

    public void delete() {
        storage.removeServerUser(this);
    }
}
