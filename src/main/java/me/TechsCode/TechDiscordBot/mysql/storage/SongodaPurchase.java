package me.TechsCode.TechDiscordBot.mysql.storage;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.api.entities.Member;

public class SongodaPurchase {

    private String name;
    private String discord;

    public SongodaPurchase(String name, String discord) {
        this.name = name;
        this.discord = discord;
    }

    public String getName() {
        return name;
    }

    public String getDiscord() {
        return discord;
    }

    public Member getMember() {
        if(getDiscord() == null) return null;

        String name = getDiscord().split("#")[0];
        String discrim = getDiscord().split("#")[1];

        if(name == null || discrim == null || name.isEmpty() || discrim.isEmpty()) return null;

        return TechDiscordBot.getGuild().getMembers().stream().filter(member -> member.getUser().getName().equals(name) && member.getUser().getDiscriminator().equals(discrim)).findFirst().orElse(null);
    }
}
