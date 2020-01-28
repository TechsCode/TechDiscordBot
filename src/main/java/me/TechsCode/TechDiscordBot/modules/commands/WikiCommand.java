package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.command.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.Plugin;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class WikiCommand extends CommandModule {

    public WikiCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!wiki"; }

    @Override
    public String[] getAliases() { return new String[]{"w"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return null; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        boolean apiIsUp = bot.getSpigotAPI().isAvailable();
        List<Plugin> plugins = Plugin.allWithWiki();
        if(apiIsUp) plugins = Plugin.fromUser(member);
        StringBuilder sb = new StringBuilder();
        if(!apiIsUp) sb.append(TechDiscordBot.getBot().getEmotes("offline").first().getAsMention()).append(" **The API is not online, showing all plugins with a Wiki.**\n\n");
        if(apiIsUp) sb.append("**Showing all Wikis of the plugins you own!**\n\n");
        if(plugins.isEmpty()) sb.append("*You do not own of any of Tech's plugins, showing all!*\n\n");
        if(plugins.isEmpty()) plugins = Plugin.allWithWiki();
        plugins.forEach(p -> sb.append(p.getEmoji().getAsMention()).append(" ").append(p.getWiki()).append("\n"));
        new CustomEmbedBuilder("Wikis").setText(sb.toString().substring(0, sb.toString().length() - 1)).send(channel);
    }
}
