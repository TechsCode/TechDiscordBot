package me.techscode.techdiscordbot.commands.verification;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import me.techscode.techdiscordbot.model.enums.Marketplace;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;


public class CheckAccountCommand extends SimpleSlashCommand {

	/**
	 * Create the check-account command with its specific settings
	 */
	public CheckAccountCommand() {
		// Set the command
		super("check-account");
		// Set the description of the command
		description("Check a marketplace ID for a verified user");
		// Set to only the main guild
		mainGuildOnly();
		// Set the options that can be used
		options(new OptionData(OptionType.STRING, "marketplace", "The marketplace where you want to check ", true)
						.addChoice("Spigot", Marketplace.SPIGOT.getId())
						.addChoice("MC-Market", Marketplace.BUILTBYBIT.getId())
						.addChoice("Polymart", Marketplace.POLYMART.getId()),
				new OptionData(OptionType.INTEGER, "ID", "The member ID of that marketplace", true));
	}

	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {

	}
}
