package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

/**
 * TODO: Fix all the commands and finish this file!
 */
public class PluginCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public PluginCommand() {
		super("plugin");
		description("Get info/links about a plugin");

		mainGuildOnly();

		options(new OptionData(OptionType.STRING, "plugin", "Select a plugin", true)
				.addChoice("Vault", "Vault")
				.addChoice("PlaceholderAPI", "PlaceholderAPI")
				.addChoice("ProtocolLib", "ProtocolLib")
				.addChoice("TAB", "TAB")
				.addChoice("NametagEdit", "NametagEdit"));
	}

	// TODO: Make better embeds

	/**
	 * The main code of the timeout command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		final String topic = Objects.requireNonNull(event.getOption("plugin")).getAsString();

		if (topic.equalsIgnoreCase("Vault")) {
			event.reply("https://www.spigotmc.org/resources/vault.34315/").queue();
		}
		if (topic.equalsIgnoreCase("PlaceholderAPI")) {
			event.reply("https://www.spigotmc.org/resources/placeholderapi.6245/").queue();
		}
		if (topic.equalsIgnoreCase("ProtocolLib")) {
			event.reply("https://www.spigotmc.org/resources/protocollib.1997/").queue();
		}
		if (topic.equalsIgnoreCase("TAB")) {
			event.reply("https://github.com/NEZNAMY/TAB").queue();
		}
		if (topic.equalsIgnoreCase("NametagEdit")) {
			event.reply("https://github.com/NEZNAMY/TAB").queue();
		}
	}
}
