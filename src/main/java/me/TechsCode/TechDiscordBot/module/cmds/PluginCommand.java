package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Arrays;

public class PluginCommand extends CommandModule {

    public PluginCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "plugin";
    }

    @Override
    public String getDescription() {
        return "Get info/links about a plugin.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "plugin", "The plugin you want info about.", true)
                        .addChoices(Arrays.stream(Plugin.values()).map(p -> new Command.Choice(p.getRoleName(), p.getRoleName())).toArray(Command.Choice[]::new))
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        Plugin plugin = e.getOption("plugin") == null ? null : Plugin.byRoleName(e.getOption("plugin").getAsString());

        if(plugin == null) {
            e.reply("Could not find the plugin, this probably shouldn't be happening.").setEphemeral(true).queue();
            return;
        }

        e.replyEmbeds(
                new TechEmbedBuilder(plugin.getRoleName())
                    .text(plugin.getDescription() + ".")
                    .field("Download Links", plugin.getPluginMarketplace().toString(), true)
                    .field("Wiki", plugin.getWiki(), true)
                    .color(plugin.getColor())
                    .thumbnail(plugin.getResourceLogo())
                    .build()
        ).queue();
    }
}
