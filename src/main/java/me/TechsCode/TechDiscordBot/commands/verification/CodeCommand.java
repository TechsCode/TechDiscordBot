package me.techscode.techdiscordbot.commands.verification;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.UUID;

public class CodeCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public CodeCommand() {
		super("code");
		description("Get a random code for verification");

		mainGuildOnly();
	}

	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		final String code = UUID.randomUUID().toString().split("-")[0];

		final SimpleEmbedBuilder builder = new SimpleEmbedBuilder("Manual Verification Code");
		builder.text(
				"Your custom verification code is:",
				"```TechManualVerification." + code + "```"
		);

		event.replyEmbeds(builder.build()).queue();
	}
}
