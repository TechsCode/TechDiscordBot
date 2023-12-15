package me.techscode.techdiscordbot.commands.staff;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.handlers.selectmenu.string.SimpleStringSelectMenu;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.actions.menus.RolesMenu;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

/**
 * TODO: Fix all the commands and finish this file!
 */
public class RoleCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public RoleCommand() {
		super("role");
		description("add roles to a user");

		mainGuildOnly();

		subCommands(
				new SubcommandData("add", "add roles to a user").addOption(OptionType.USER, "user", "The user to add roles", true),
				new SubcommandData("remove", "remove roles from a user").addOption(OptionType.USER, "user", "The user to remove roles", true)

		);
		options(new OptionData(OptionType.USER, "user", "The user to add roles", true));
	}

	/**
	 * The main code of the timeout command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		final User target = event.getOption("user").getAsUser();

		// Get the guild where the command was executed
		final Guild guild = event.getGuild();
		assert guild != null;

		// Get the member from the guild
		final Member guildMember = guild.getMember(target);

		if (guildMember == null) {
			event.reply("The user is not in this guild.").setEphemeral(true).queue();
			return;
		}

		switch (event.getSubcommandName()) {
			case "add" -> event.replyEmbeds(
							new SimpleEmbedBuilder("Add Roles")
									.text("Select the roles you want to add to member " + guildMember.getAsMention() + ".")
									.build()
					)
					.addActionRow(
							new RolesMenu.Add(guildMember).build()
					).setEphemeral(true).queue();
			case "remove" -> event.replyEmbeds(
							new SimpleEmbedBuilder("Remove Roles")
									.text("Select the roles you want to remove from member " + guildMember.getAsMention() + ".")
									.build()
					)
					.addActionRow(
							new RolesMenu.Remove(guildMember).build()
					).setEphemeral(true).queue();
		}


	}
}
