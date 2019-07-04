package me.TechsCode.TechDiscordBot;

import me.TechsCode.TechDiscordBot.util.ConsoleColor;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Module {

    protected TechDiscordBot bot;

    private boolean enabled;

    public Module(TechDiscordBot bot) {
        this.bot = bot;

        Set<Requirement> failedRequirements = Arrays.stream(getRequirements())
                .filter(requirement -> !requirement.check(bot))
                .collect(Collectors.toSet());

        if(failedRequirements.isEmpty()){
            bot.log("Enabling Module "+getName()+"..");
            onEnable();

            enabled = true;
        } else {
            bot.log(ConsoleColor.YELLOW+"Failed Enabling Module "+ConsoleColor.YELLOW_BOLD_BRIGHT+getName()+ConsoleColor.YELLOW+" because:");

            failedRequirements.forEach(requirement -> bot.log(ConsoleColor.WHITE+"- "+requirement.getFailReason()));
        }
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public boolean isEnabled() {
        return enabled;
    }

    public abstract String getName();

    public abstract Requirement[] getRequirements();

}
