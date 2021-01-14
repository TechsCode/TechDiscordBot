package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class PatreonCommand extends CommandModule {

    public PatreonCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!patreon"; }

    @Override
    public String[] getAliases() { return null; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return null; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.INFO; }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        boolean isPatron = member.getRoles().stream().anyMatch(r -> r.getName().equals("Patreon"));

        channel.sendMessage((isPatron ? "Thank you for being a Patron!\n\n" : "") + "https://patreon.com/TechsCode").queue();
    }
}
