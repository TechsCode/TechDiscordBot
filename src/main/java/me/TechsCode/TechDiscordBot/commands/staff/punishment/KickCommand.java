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

public class KickCommand extends SimpleSlashCommand {

	/**
	 * Create the kick command with its specific settings
	 */
	public KickCommand() {
		super("kick");
		description("Kick a user from the guild");

		// Set the specific options that can be used for the command
		options(
				new OptionData(OptionType.USER, "user", "The user to kick", true),
				new OptionData(OptionType.STRING, "reason", "The reason for the kicking", true)
		);
	}

	/**
	 * The main code of the kick command
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
		if (guildMember.getRoles().contains(SimpleRoles.getRoleByName(getGuild(), "staff")) || guildMember.getRoles().contains(SimpleRoles.getRoleByName(getGuild(), "bot"))) {
			event.reply("You are not allowed to kick a staff member or a BOT").setEphemeral(true).queue();
			return;
		}

		// Kick the user from the guild
		guild.kick(target, reason).queue();

		if (guild == TechDiscordBot.getMainGuild()) {
			Logs.PunishLogs.log(
					new SimpleEmbedBuilder("Kick | " + target.getName())
							.text("Kicked a user from the GUILD;")
							.field("Target;", target.getAsMention(), true)
							.field("Staff member;", executor.getAsMention(), true)
							.field("Reason;", reason, false)
			);
		}
	}
}
