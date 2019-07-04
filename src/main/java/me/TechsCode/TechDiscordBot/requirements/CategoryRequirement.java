package me.TechsCode.TechDiscordBot.requirements;

import me.TechsCode.TechDiscordBot.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

public class CategoryRequirement extends Requirement {

    private String categoryName;

    public CategoryRequirement(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean check(TechDiscordBot bot) {
        return bot.getGuild().getCategoriesByName(categoryName, true).size() > 0;
    }

    @Override
    public String getFailReason() {
        return "Missing Category \""+categoryName+"\"";
    }
}
