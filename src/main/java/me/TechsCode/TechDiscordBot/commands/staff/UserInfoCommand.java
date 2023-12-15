package me.techscode.techdiscordbot.commands.staff;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The mute command to mute an player
 */
public class UserInfoCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public UserInfoCommand() {
		super("info");
		description("Get information about a specific user.");

		mainGuildOnly();

		options(new OptionData(OptionType.USER, "member", "The member that needs a timeout", true));
	}

	/**
	 * The main code of the timeout command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		final Member member = event.getOption("member") == null ? event.getMember() : Objects.requireNonNull(event.getOption("member")).getAsMember();
		assert member != null;
		final User user = member.getUser();

		event.replyEmbeds(
				new SimpleEmbedBuilder(user.getName() + "#" + user.getDiscriminator())
						.field("ID", user.getId(), true)
						.field("Status", member.getOnlineStatus().getKey().substring(0, 1).toUpperCase() + member.getOnlineStatus().getKey().substring(1), true)
						.field("Times", "Created: " + user.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ", Joined: " + member.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), true)
						.field("Flags", user.getFlags().clone().stream().map(User.UserFlag::getName).collect(Collectors.joining(", ")), true)
						.field("Roles", member.getRoles().stream().map(Role::getAsMention).collect(Collectors.joining(", ")), true)
						.thumbnail(user.getAvatarUrl())
						.build()
		).queue();
	}
}
