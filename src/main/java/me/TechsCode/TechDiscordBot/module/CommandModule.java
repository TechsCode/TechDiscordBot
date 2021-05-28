package me.TechsCode.TechDiscordBot.module;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.Cooldown;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.HashMap;
import java.util.List;

public abstract class CommandModule {

    private final HashMap<String, Cooldown> cooldowns = new HashMap<>();
    protected TechDiscordBot bot;

    public CommandModule(TechDiscordBot bot) {
        this.bot = bot;
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract CommandPrivilege[] getCommandPrivileges();

    public abstract OptionData[] getOptions();

    public abstract boolean isEphemeral();

    public abstract int getCooldown();

    public boolean isHook() {
        return false;
    }

    public HashMap<String, Cooldown> getCooldowns() {
        return cooldowns;
    }

    public abstract void onCommand(TextChannel channel, Member m, InteractionHook hook, SlashCommandEvent e);

}