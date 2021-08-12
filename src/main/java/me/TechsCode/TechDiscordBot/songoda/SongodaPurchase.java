package me.TechsCode.TechDiscordBot.songoda;

import com.google.gson.JsonObject;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.spigotmc.data.Cost;
import me.TechsCode.TechDiscordBot.spigotmc.data.Purchase;
import me.TechsCode.TechDiscordBot.spigotmc.data.Time;
import me.TechsCode.TechDiscordBot.spigotmc.data.User;
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
        if(!getDiscord().contains("#"))
            return TechDiscordBot.getGuild().getMemberById(getDiscord());

        String name = this.discord.split("#")[0];
        String discrim = this.discord.split("#")[1];

        if (name == null || discrim == null || name.isEmpty() || discrim.isEmpty())
            return null;

        return TechDiscordBot.getGuild().getMembers().stream().filter(member -> member.getUser().getName().equals(name) && member.getUser().getDiscriminator().equals(discrim)).findFirst().orElse(null);
    }
}
