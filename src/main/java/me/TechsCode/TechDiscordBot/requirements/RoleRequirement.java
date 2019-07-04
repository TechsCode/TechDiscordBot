package me.TechsCode.TechDiscordBot.requirements;

import me.TechsCode.TechDiscordBot.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

public class RoleRequirement extends Requirement {

    private String roleName;

    public RoleRequirement(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public boolean check(TechDiscordBot bot) {
        return bot.getGuild().getRolesByName(roleName, true).size() > 0;
    }

    @Override
    public String getFailReason() {
        return "Missing Role \""+roleName+"\"";
    }
}
