package me.TechsCode.TechDiscordBot.songoda;

import com.google.gson.JsonObject;
import me.TechsCode.SpigotAPI.data.Cost;
import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.SpigotAPI.data.Time;
import me.TechsCode.SpigotAPI.data.User;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.api.entities.Member;

public class SongodaPurchase extends Purchase {

    private final String discord;

    public SongodaPurchase(String resourceId, User user, Time time, Cost cost, String discord) {
        super(resourceId, user, time, cost);

        this.discord = discord;
    }

    public SongodaPurchase(JsonObject state, String discord) {
        super(state);

        this.discord = discord;
    }

    public String getDiscord() {
        return discord;
    }

    public Member getMember() {
        if (getDiscord() == null)
            return null;

        String name = this.discord.split("#")[0];
        String discrim = this.discord.split("#")[1];

        if (name == null || discrim == null || name.isEmpty() || discrim.isEmpty())
            return null;

        return TechDiscordBot.getGuild().getMembers().stream().filter(member -> member.getUser().getName().equals(name) && member.getUser().getDiscriminator().equals(discrim)).findFirst().orElse(null);
    }
}
