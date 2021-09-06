package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.UUID;

public class CodeCommand extends CommandModule {

    public CodeCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "code";
    }

    @Override
    public String getDescription() {
        return "Get a random code for verification";
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
        return 2;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String code = UUID.randomUUID().toString().split("-")[0];

        e.replyEmbeds(new TechEmbedBuilder("Manual Verification Code")
                .text("```TechManualVerification."+code+"```")
                .build()).queue();
    }

}
