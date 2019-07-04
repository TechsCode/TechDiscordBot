package me.TechsCode.TechDiscordBot.requirements;

import me.TechsCode.TechDiscordBot.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

public class EmoteRequirement extends Requirement {

    private String emoteName;

    public EmoteRequirement(String emoteName) {
        this.emoteName = emoteName;
    }

    @Override
    public boolean check(TechDiscordBot bot) {
        return bot.getGuild().getEmotesByName(emoteName, true).size() > 0;
    }

    @Override
    public String getFailReason() {
        return "Missing Emote \""+emoteName+"\"";
    }
}
