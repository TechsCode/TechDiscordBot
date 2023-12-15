package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.model.reminders.Reminder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * TODO: Fix all the commands and finish this file!
 */
public class RemindCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public RemindCommand() {
		super("remindme");
		description("Get reminded about something.");

		mainGuildOnly();

		options(
				new OptionData(OptionType.STRING, "time", "How long FROM NOW to be reminded.", true),
				new OptionData(OptionType.STRING, "reminder", "What to be reminded about.", true),
				new OptionData(OptionType.BOOLEAN, "private", "Whether to send the reminder in a dm or not.", false)
		);
	}

	/**
	 * The main code of the timeout command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		final String time = event.getOption("time").getAsString();
		final String message = event.getOption("reminder").getAsString();
		final Boolean isPrivate = event.getOption("private") != null && event.getOption("private").getAsBoolean();

		final Reminder reminder = TechDiscordBot.getRemindersManager().createReminder(event.getUser(), time, message, event.getChannel().asTextChannel(), isPrivate);

		if (reminder == null) {
			event.replyEmbeds(
					new SimpleEmbedBuilder("Reminder - Error")
							.text("An error has occurred. Did you specify a time and a reason?\n\n**Here are some examples!**:\n`/remind 1 day Fix x thing.`\n`/remind 30 hours I need help.`\n`/remind 30 hours I need help. dm` (makes it a dm)")
							.error()
							.build()
			).setEphemeral(true).queue();
		} else {
			event.replyEmbeds(
					new SimpleEmbedBuilder("Reminder Set!")
							.text("I will remind you <t:" + (reminder.getTime() / 1000) + ":R> for **" + reminder.getReminder() + "**!")
							.success()
							.build()
			).setEphemeral(true).queue();
		}
	}
}
