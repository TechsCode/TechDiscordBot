package me.TechsCode.TechDiscordBot.module;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.Cooldown;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CommandModule {

    private final HashMap<String, Cooldown> cooldowns = new HashMap<>();

    protected TechDiscordBot bot;
    private boolean enabled;

    public CommandModule(TechDiscordBot bot) {
        this.bot = bot;
    }

    public void enable() {
        Set<Requirement> failedRequirements = Arrays.stream(getRequirements())
                .filter(requirement -> !requirement.check())
                .collect(Collectors.toSet());

        if(failedRequirements.isEmpty()) {
            onEnable();
            enabled = true;
        } else {
            TechDiscordBot.log(ConsoleColor.YELLOW + "Failed Enabling Module " + ConsoleColor.YELLOW_BOLD_BRIGHT+getName()+ConsoleColor.YELLOW + " because:");
            failedRequirements.forEach(requirement -> TechDiscordBot.log(ConsoleColor.WHITE + "- " + requirement.getUnmatchMessage()));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public void onEnable() {}

    public void onDisable() {}

    public String getName() {
        return "[" + getCommand() + "] Command";
    }

    public Requirement[] getRequirements() {
        Set<Requirement> requirements = new HashSet<>();

        if(getRestrictedRoles() != null)
            requirements.add(new Requirement(getRestrictedRoles(), 1, "No Roles found which are suitable for running this Command (Missing Restricted Roles)"));
        if(getRestrictedChannels() != null)
            requirements.add(new Requirement(getRestrictedChannels(), 1, "No Channels found which are suitable for running this Command (Missing Restricted Channels)"));

        return requirements.toArray(new Requirement[0]);
    }

    public abstract String getCommand();

    public abstract String[] getAliases();

    public abstract DefinedQuery<Role> getRestrictedRoles();

    public abstract DefinedQuery<TextChannel> getRestrictedChannels();

    public abstract CommandCategory getCategory();

    public abstract void onCommand(TextChannel channel, Message message, Member member, String[] args);

    public abstract int getCooldown();

    public HashMap<String, Cooldown> getCooldowns() {
        return cooldowns;
    }
}