package me.TechsCode.TechDiscordBot.requirements;

import me.TechsCode.TechDiscordBot.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

public class ChannelRequirement extends Requirement {

    private String channelName;

    public ChannelRequirement(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public boolean check(TechDiscordBot bot) {
        return bot.getGuild().getTextChannelsByName(channelName, true).size() > 0;
    }

    @Override
    public String getFailReason() {
        return "Missing Text Channel \""+channelName+"\"";
    }
}
