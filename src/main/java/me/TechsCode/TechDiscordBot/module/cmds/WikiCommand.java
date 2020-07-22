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

import java.util.List;
import java.util.stream.Collectors;

public class WikiCommand extends CommandModule {

    public WikiCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!wiki"; }

    @Override
    public String[] getAliases() { return new String[]{"!w"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return null; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.INFO; }

    @Override
    public int getCooldown() {
        return 2;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length > 0 && args[0].equals("help")) {
            new TechEmbedBuilder("Wiki Help").setText("**Wiki Command Args**\n\n`!wiki` - *If in a plugin support channel, shows that Wiki, otherwise shows your owned plugin's wikis.*\n`!wiki -a` - *Shows all wikis.*\n`wiki -m` - *Shows your wikis if in a plugin support channel.*").send(channel);
            return;
        }
        if(Plugin.isPluginChannel(channel)) {
            if(args.length == 0) {
                showCurrentChannel(channel);
            } else {
                if(args[0].equals("-a")) {
                    showAll(channel);
                } else if (args[0].equals("-m")) {
                    showYourPlugins(member, channel);
                }
            }
        } else {
            if(args.length == 0) {
                showYourPlugins(member, channel);
            } else {
                if(args[0].equals("-a")) {
                    showAll(channel);
                }
            }
        }
    }

    public void showCurrentChannel(TextChannel channel) {
        if(Plugin.isPluginChannel(channel)) {
            Plugin plugin = Plugin.byChannel(channel);
            if(!plugin.hasWiki()) {
                new TechEmbedBuilder("Wikis").error().setText(plugin.getEmoji().getAsMention() + " " + plugin.getRoleName() + " unfortunately does not have a wiki!").sendTemporary(channel, 10);
                return;
            }
            new TechEmbedBuilder("Wikis").setText("*Showing the wiki of the support channel you're in.*\n\n" + plugin.getEmoji().getAsMention() + " " + plugin.getWiki() + "\n\nFor more info please execute the command `wiki help`.").send(channel);
        }
    }

    public void showYourPlugins(Member member, TextChannel channel) {
        boolean apiIsUp = TechDiscordBot.getSpigotAPI().isAvailable();
        List<Plugin> plugins = Plugin.allWithWiki();
        if(apiIsUp) plugins = Plugin.fromUser(member).stream().filter(Plugin::hasWiki).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        if(!apiIsUp) sb.append(TechDiscordBot.getBot().getEmotes("offline").first().getAsMention()).append(" **The API is not online, showing all plugins with a wiki.**\n\n");
        if(apiIsUp) sb.append("*Showing all wikis of the plugins you own!*\n\n");
        if(plugins.isEmpty()) sb.append("**You do not own of any of Tech's plugins, showing all wikis!**\n\n");
        if(plugins.isEmpty()) plugins = Plugin.allWithWiki();
        plugins.forEach(p -> sb.append(p.getEmoji().getAsMention()).append(" ").append(p.getWiki()).append("\n"));
        new TechEmbedBuilder("Wikis").setText(sb.toString().substring(0, sb.toString().length() - 1)).send(channel);
    }

    public void showAll(TextChannel channel) {
        List<Plugin> plugins = Plugin.allWithWiki();
        StringBuilder sb = new StringBuilder();
        sb.append("*Showing all wikis!*\n\n");
        plugins.forEach(p -> sb.append(p.getEmoji().getAsMention()).append(" ").append(p.getWiki()).append("\n"));
        new TechEmbedBuilder("Wikis").setText(sb.toString().substring(0, sb.toString().length() - 1)).send(channel);
    }
}