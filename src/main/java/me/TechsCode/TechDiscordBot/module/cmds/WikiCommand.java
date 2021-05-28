package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.List;
import java.util.stream.Collectors;

public class WikiCommand extends CommandModule {

    public WikiCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "wiki";
    }

    @Override
    public String getDescription() {
        return "Returns the wiki website!";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.BOOLEAN, "all", "Show all plugins"),
                new OptionData(OptionType.BOOLEAN, "mine", "Show your plugins"),
        };
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 2;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, InteractionHook hook, SlashCommandEvent e) {
        boolean all = e.getOption("all") != null && e.getOption("all").getAsBoolean();
        boolean mine = e.getOption("mine") != null && e.getOption("mine").getAsBoolean();

        if(Plugin.isPluginChannel(channel)) {
            if(!all && !mine) {
                showCurrentChannel(e, channel);
            } else {
                if(all) {
                    showAll(e);
                } else if (mine) {
                    showYourPlugins(e, m);
                }
            }
        } else {
            if(!all && !mine) {
                showYourPlugins(e, m);
            } else if(all) {
                showAll(e);
            }
        }
    }

    public void showCurrentChannel(SlashCommandEvent e, TextChannel channel) {
        if(Plugin.isPluginChannel(channel)) {
            Plugin plugin = Plugin.byChannel(channel);
            if(!plugin.hasWiki()) {
                new TechEmbedBuilder("Wikis").error().setText(plugin.getEmoji().getAsMention() + " " + plugin.getRoleName() + " unfortunately does not have a wiki!").sendTemporary(channel, 10);
                return;
            }

            e.replyEmbeds(
                    new TechEmbedBuilder("Wikis")
                        .setText("*Showing the wiki of the support channel you're in.*\n\n" + plugin.getEmoji().getAsMention() + " " + plugin.getWiki() + "\n\nFor more info please execute the command `wiki help`.")
                        .build()
            ).queue();
        }
    }

    public void showYourPlugins(SlashCommandEvent e, Member member) {
        boolean apiIsUsable = TechDiscordBot.getBot().getStatus().isUsable();

        List<Plugin> plugins = Plugin.allWithWiki();
        if(apiIsUsable) plugins = Plugin.fromUser(member).stream().filter(Plugin::hasWiki).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        if(!apiIsUsable)
            sb.append(TechDiscordBot.getBot().getEmotes("offline").first().getAsMention()).append(" **The API is not online, showing all plugins with a wiki.**\n\n");
        if(apiIsUsable)
            sb.append("*Showing all wikis of the plugins you own!*\n\n");
        if(plugins.isEmpty())
            sb.append("**You do not own of any of Tech's plugins, showing all wikis!**\n\n");
        if(plugins.isEmpty())
            plugins = Plugin.allWithWiki();
        plugins.forEach(p -> sb.append(p.getEmoji().getAsMention()).append(" ").append(p.getWiki()).append("\n"));

        e.replyEmbeds(
            new TechEmbedBuilder("Wikis")
                .setText(sb.substring(0, sb.toString().length() - 1))
                .build()
        ).queue();
    }

    public void showAll(SlashCommandEvent e) {
        List<Plugin> plugins = Plugin.allWithWiki();
        StringBuilder sb = new StringBuilder();

        sb.append("*Showing all wikis!*\n\n");
        plugins.forEach(p -> sb.append(p.getEmoji().getAsMention()).append(" ").append(p.getWiki()).append("\n"));

        e.replyEmbeds(
            new TechEmbedBuilder("Wikis")
                .setText(sb.substring(0, sb.toString().length() - 1))
                .build()
        ).queue();
    }
}
