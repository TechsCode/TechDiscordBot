package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.TranscriptDatabase;
import me.techscode.techdiscordbot.database.entities.SqlPreorder;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class PreorderCommand extends SimpleSlashCommand {

	public PreorderCommand() {
		super("preorder");
		description("Check if you have pre-ordered Insane Vaults.");

		mainGuildOnly();

		options(
				new OptionData(OptionType.STRING, "type", "What do you need?", true).addChoice("Check", "check").addChoice("Link", "link"),
				new OptionData(OptionType.USER, "user", "The user to check the pre-order of.", false)
		);
	}

	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
/*
		final String type = event.getOption("type").getAsString();

		if (type.equals("check")) {
			final User user = Objects.requireNonNull(event.getOption("user")).getAsUser();

			if (!SimpleRoles.hasRole(getMember(), Settings.Roles.staff)) {
				final List<SqlPreorder> sqLPreorder = TranscriptDatabase.PREORDERS.get(getMember().getIdLong());

				for (final SqlPreorder sqlPreorder : sqLPreorder) {
					if (sqlPreorder.getDiscordId() == getMember().getIdLong() && !sqlPreorder.getTransactionId().equalsIgnoreCase("none")) {
						event.replyEmbeds(new SimpleEmbedBuilder("Insane Vaults Preorder")
								.text(
										"**" + user.getAsMention() + " You have preorderd Insane Vaults!**",
										"",
										"You can access <#1058612057576054864> for your support questions.",
										"To download the latest build of Insane Vaults go to the preorder page **[HERE](https://preorder.insanevaults.com/)**"
								)
								.color(Color.getColor("#e74c3c"))
								.thumbnail("https://cloud.techscode.com/s/J6SaqeQRrErQwft/preview")
								.build()
						).setEphemeral(true).queue();
					} else {
						event.replyEmbeds(new SimpleEmbedBuilder("Insane Vaults Preorder")
								.text(
										"**" + user.getAsMention() + " You haven't yet preorderd Insane Vaults!**",
										"",
										"You can preorder Insane Vaults **[HERE](https://preorder.insanevaults.com/)**"
								)
								.color(Color.getColor("#e74c3c"))
								.thumbnail("https://cloud.techscode.com/s/J6SaqeQRrErQwft/preview")
								.build()
						).setEphemeral(true).queue();
					}
				}
			} else {
				final List<SqlPreorder> sqLPreorder = TranscriptDatabase.PREORDERS.get(user.getIdLong());

				for (final SqlPreorder sqlPreorder : sqLPreorder) {
					if (sqlPreorder.getDiscordId() == user.getIdLong() && !sqlPreorder.getTransactionId().equalsIgnoreCase("none")) {
						event.replyEmbeds(new SimpleEmbedBuilder("Insane Vaults Preorder")
								.text(
										TechDiscordBot.getJDA().getUserById(sqlPreorder.getDiscordId()).getAsMention() + " has pre-ordered Insane Vaults!",
										"",
										"Transaction ID; " + sqlPreorder.getTransactionId()
								)
								.color(Color.getColor("#e74c3c"))
								.thumbnail("https://cloud.techscode.com/s/J6SaqeQRrErQwft/preview")
								.build()
						).setEphemeral(true).queue();
					} else {
						event.replyEmbeds(new SimpleEmbedBuilder("Insane Vaults Preorder")
								.text(
										TechDiscordBot.getJDA().getUserById(sqlPreorder.getDiscordId()).getAsMention() + " has not pre-ordered Insane Vaults!",
										"",
										"Transaction ID; " + sqlPreorder.getTransactionId()
								)
								.color(Color.getColor("#e74c3c"))
								.thumbnail("https://cloud.techscode.com/s/J6SaqeQRrErQwft/preview")
								.build()
						).setEphemeral(true).queue();
					}
				}
			}
		}
		if (type.equals("link")) {
			event.replyEmbeds(new SimpleEmbedBuilder("Preorder link")
					.text("Pre-order here: https://preorder.insanevaults.com/")
					.success().build()
			).setEphemeral(true).queue();
		}*/
	}
}
