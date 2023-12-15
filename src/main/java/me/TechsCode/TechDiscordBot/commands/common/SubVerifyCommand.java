package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SubVerifyCommand extends SimpleSlashCommand {

    public SubVerifyCommand() {
        super("subverify");
        description("Verify another person under your account.");

        options(new OptionData(OptionType.USER, "user", "The user to add to your account.", true));
    }

    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {

    }
}
