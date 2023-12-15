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


/**
 * TODO: Fix all the commands and finish this file!
 */
public class WarnCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public WarnCommand() {
		super("warn");
		description("Warn a user");

		mainGuildOnly();

		options(
				new OptionData(OptionType.USER, "user", "The user to warn", true),
				new OptionData(OptionType.STRING, "reason", "The reason for the warn", true)
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
			event.reply("You are not allowed to warn a staff member or a BOT").setEphemeral(true).queue();
			return;
		}

		event.reply("You have warned; " + target.getAsMention() + " for; `" + reason + "`").queue();

		event.getGuildChannel().sendMessageEmbeds(
				new SimpleEmbedBuilder("Warning")
						.text("You have been warned by; " + executor.getAsMention())
						.field("Reason;", reason, false)
						.build()
		).queue();

		if (guild == TechDiscordBot.getMainGuild()) {
			Logs.PunishLogs.log(
					new SimpleEmbedBuilder("Warn | " + target.getName())
							.text("Warned a user in the guild;")
							.field("Target;", target.getAsMention(), true)
							.field("Staff member;", executor.getAsMention(), true)
							.field("Reason;", reason, false)
			);
		}
	}
}
