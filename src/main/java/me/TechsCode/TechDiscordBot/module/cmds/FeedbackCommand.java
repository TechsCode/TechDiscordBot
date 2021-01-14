package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class FeedbackCommand extends CommandModule {

    public FeedbackCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!feedback"; }

    @Override
    public String[] getAliases() { return new String[]{ "!suggestions", "!suggest", "!suggestion" }; }

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
        channel.sendMessage("https://feedback.techscode.de").queue();
    }
}
