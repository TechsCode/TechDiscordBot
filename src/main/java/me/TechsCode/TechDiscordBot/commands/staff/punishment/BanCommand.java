package me.techscode.techdiscordbot.commands.staff.punishment;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.model.Logs;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class BanCommand extends SimpleSlashCommand {

	/**
	 * Create the ban command with its specific settings
	 */
	public BanCommand() {
		super("ban");
		description("Ban a person from the guild");

		// Se the specific options that can be used for the command
		options(
				new OptionData(OptionType.USER, "user", "The user to ban", true),
				new OptionData(OptionType.STRING, "reason", "The reason for the banning", true)
		);
	}

	/**
	 * The main code of the ban command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		// Get the command sender
		final User executor = event.getUser();

		// Get the guild where the command was executed
		final Guild guild = event.getGuild();
		assert guild != null;

		// Get the options from the command
		final User target = event.getOption("user").getAsUser();
		final String reason = event.getOption("reason").getAsString();

		// Get the member from the guild
		final Member guildMember = guild.getMember(target);

		// Check if the target isn't null
		if (guildMember == null) {
			event.reply("The user is not in this guild.").setEphemeral(true).queue();
			return;
		}

		// Check if the target is a staff member
		if (SimpleRoles.hasRole(guildMember, "staff") || SimpleRoles.hasRole(guildMember, "Bot")) {
			event.reply("You are not allowed to ban a staff member or a BOT").setEphemeral(true).queue();
			return;
		}

		// Add a new ban to the guild's ban list
		guild.ban(target, 1, TimeUnit.DAYS)
				.reason(reason)
				.queue();

		SimpleEmbedBuilder embed = new SimpleEmbedBuilder("Ban | " + target.getName())
				.text("Banned a user from the GUILD;")
				.field("Target;", target.getAsMention(), true)
				.field("Staff Member;", executor.getAsMention(), true)
				.field("Reason;", reason, false)
				.error();

		// An extra check to see if the guild is really the main guild
		if (guild == TechDiscordBot.getMainGuild()) {
			Logs.PunishLogs.log(
					embed
			);
		}

		event.replyEmbeds(embed.build()).setEphemeral(true).queue();
	}
}