package me.techscode.techdiscordbot.commands.staff.punishment;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.model.Logs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.TimeUnit;

/**
 * The mute command to mute an player
 */
public class TimeoutCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public TimeoutCommand() {
		super("timeout");
		description("Timeout a user");

		mainGuildOnly();

		// Set the options for the command
		options(
				new OptionData(OptionType.USER, "user", "The user to timeout", true),
				new OptionData(OptionType.STRING, "duration", "The duration", true),
				new OptionData(OptionType.STRING, "timeunit", "The time unit (s, h, d, w, m) of the timeout", true)
						.addChoice("seconds", "s")
						.addChoice("minutes", "m")
						.addChoice("hours", "h")
						.addChoice("days", "d")
						.addChoice("weeks", "w")
						.addChoice("months", "M"),
				new OptionData(OptionType.STRING, "reason", "The reason for the timeout", true)
		);
	}

	/**
	 * The main code of the timeout command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		// Get the command sender
		final User executor = event.getUser();

		// Get the guild from the event
		final Guild guild = event.getGuild();
		assert guild != null;

		final User target = event.getOption("user").getAsUser();
		final String duration = event.getOption("duration").getAsString();
		final String timeunit = event.getOption("timeunit").getAsString();
		final String reason = event.getOption("reason").getAsString();

		// Get the member from the guild
		final Member guildMember = guild.getMember(target);

		// Check if the target isn't null
		if (guildMember == null) {
			event.reply("The user is not in this guild.").setEphemeral(true).queue();
			return;
		}

		// Check if the target is a staff member
		if (guildMember.getRoles().contains(SimpleRoles.getRoleByName(getGuild(), "staff")) || guildMember.getRoles().contains(SimpleRoles.getRoleByName(getGuild(), "bot"))) {
			event.reply("You are not allowed to timeout a staff member or a BOT").setEphemeral(true).queue();
			return;
		}

		// TODO: Fix this part to make it simpler

		// Retrieve the time option of the command
		long time = event.getOption("time").getAsLong();
		final String timeUnitString = event.getOption("timeunit").getAsString();
		TimeUnit timeUnit = null;

		// Check the timeUnitsString to determine the duration
		switch (timeUnitString) {
			case "s":
				timeUnit = TimeUnit.SECONDS;
				break;
			case "m":
				timeUnit = TimeUnit.MINUTES;
				break;
			case "h":
				timeUnit = TimeUnit.HOURS;
				break;
			case "d":
				timeUnit = TimeUnit.DAYS;
				break;
			case "w":
				timeUnit = TimeUnit.DAYS;
				time = time * 7;
				break;
			case "M":
				timeUnit = TimeUnit.DAYS;
				time = time * 30;
				break;
			default:
				event.replyEmbeds(new SimpleEmbedBuilder("timeout | ERROR")
						.text("You did not fil in the right time unit")
						.error().build()).setEphemeral(true).queue();
				break;
		}

		// Assert the option to check if the values aren't null
		assert timeUnit != null;

		// Give the member a timeout
		guild.getMember(target).timeoutFor(time, timeUnit).queue();

		// Send success message the executor of the command
		event.replyEmbeds(new SimpleEmbedBuilder("TimeOut | SUCCESS")
				.text("You have given " + target.getAsMention() + " a timeout;",
						"",
						"**Reason:** " + reason,
						"**Duration:** " + time + " " + timeUnit.toString().toLowerCase())
				.build()).setEphemeral(true).queue();

		// Send dm message to the member that got a timeout
		target.openPrivateChannel().complete().sendMessageEmbeds(new SimpleEmbedBuilder("Timeout | RECEIVED")
				.text("You have been given an timeout!",
						"",
						"**Reason:** " + reason,
						"**Duration:** " + time + " " + timeUnit.toString().toLowerCase())
				.build()).queue();

		Logs.PunishLogs.log(new SimpleEmbedBuilder("Timeout | SUCCESS")
				.text(target.getAsMention() + " has been given a timeout",
						" ",
						"**Reason:** " + reason,
						"**Duration:** " + time + " " + timeUnit.toString().toLowerCase()));
	}
}
