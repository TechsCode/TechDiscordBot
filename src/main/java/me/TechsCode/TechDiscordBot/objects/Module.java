package me.TechsCode.TechDiscordBot.objects;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Module {

    protected TechDiscordBot bot;

    private boolean enabled;

    public Module(TechDiscordBot bot) { this.bot = bot; }

    public void enable() {
        Set<Requirement> failedRequirements = Arrays.stream(getRequirements()).filter(requirement -> !requirement.check()).collect(Collectors.toSet());
        if(failedRequirements.isEmpty()) {
            bot.log("Enabling Module " + getName() + "..");
            onEnable();
            enabled = true;
        } else {
            bot.log(ConsoleColor.YELLOW_BRIGHT + "Failed Enabling Module " + ConsoleColor.YELLOW_BOLD_BRIGHT+getName()+ConsoleColor.YELLOW_BRIGHT + " because:");
            failedRequirements.forEach(requirement -> bot.log(ConsoleColor.WHITE + "- " + requirement.getUnmatchMessage()));
        }
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public boolean isEnabled() { return enabled; }

    public abstract String getName();

    public abstract Requirement[] getRequirements();

}
