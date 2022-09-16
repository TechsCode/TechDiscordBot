package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

public class FeedbackCommand extends CommandModule {

    public FeedbackCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() { return "feedback"; }

    @Override
    public String getDescription() {
        return "Returns the feedback website!";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        e.replyEmbeds(new TechEmbedBuilder("Feedback")
                .success()
                .text("For suggestions, <#1020188847461629972>.\n" + "For Bug Reports, <#1020188935953076244>.\n" + "For Enhancements, <#1020189010406150204>.")
                .build()
        ).queue();
    }
}