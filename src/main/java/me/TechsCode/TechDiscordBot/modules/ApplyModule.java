package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.actions.buttons.ApplyButton;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlApplication;
import me.techscode.techdiscordbot.model.enums.Application;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ApplyModule {

	private static final String APPLY_CHANNEL_ID = Settings.Modules.Apply.channel;

	public static void embed() {

		if (!Settings.Modules.Apply.enabled) return;

		// The Verification text channel
		final TextChannel channel = Objects.requireNonNull(TechDiscordBot.getJDA().getGuildById(SimpleSettings.Bot.MainGuild())).getTextChannelById(APPLY_CHANNEL_ID);


		// Create the verification embed
		final SimpleEmbedBuilder embed = new SimpleEmbedBuilder("Applications | How it works")
				.text(
						"Welcome to the applications channel in here you can create your application to be a staff member on the server.",
						"",
						"**Open positions**",
						"- Supporter - *Help people with their problems*",
						"- Developer - *Help our development team to develop our plugins*",
						"- Marketing - *Be creative and discuss new marketing ideas make drawings and much more*",
						"- Community Helper - *Help us maintain our Translations and Wiki pages*",
						"",
						"**How to create a application?**",
						"Click on the button below to create a application. After that there will be a application created for you."
				)
				.color(new Color(81, 153, 226));

		// Debug message to see what the application channel is
		assert channel != null;
		Debugger.debug("Applications", "channel name == " + channel.getName() + " channel id == " + channel.getId());

		// Send the embed with the buttons to the verification channel
		channel.sendMessageEmbeds(embed.build())
				.setActionRow(
						new ApplyButton.Button().build()
				).queue();
	}

	public static CompletableFuture<TextChannel> applicationCreate(@NotNull Member member, Guild guild) {

		// Permissions list for the applications channel
		final List<Permission> allowedPermissions = new ArrayList<>(
				Arrays.asList(
						Permission.VIEW_CHANNEL,
						Permission.MESSAGE_SEND,
						Permission.MESSAGE_HISTORY,
						Permission.MESSAGE_ATTACH_FILES,
						Permission.MESSAGE_EMBED_LINKS,
						Permission.MESSAGE_ADD_REACTION,
						Permission.MANAGE_EMOJIS_AND_STICKERS
				)
		);

		// Get the members name if the members name can't be gathered it will use the user's ID
		final String memberName;
		if (Objects.equals(member.getNickname(), "") || member.getNickname() == null) {
			if (member.getEffectiveName().equals("")) {
				memberName = member.getUser().getId();
			} else {
				member.getEffectiveName();
				memberName = member.getEffectiveName();
			}
		} else {
			memberName = member.getNickname();
		}

		// Get some settings from the settings file
		final Category category = guild.getCategoryById(Settings.Modules.Apply.category);
		final long supportRole = Settings.Roles.support;
		final long developmentRole = Settings.Roles.development;
		final long leadershipRole = Settings.Roles.leadership;

		CompletableFuture<TextChannel> future = new CompletableFuture<>();

		// Create the application channel for the member
		guild.createTextChannel("application-" + memberName, category)
				.addPermissionOverride(guild.getPublicRole(), null, allowedPermissions)
				// Add staff permissions
				.addRolePermissionOverride(supportRole, allowedPermissions, null)
				.addRolePermissionOverride(developmentRole, allowedPermissions, null)
				.addRolePermissionOverride(leadershipRole, allowedPermissions, null)
				// Add member permissions
				.addMemberPermissionOverride(member.getIdLong(), allowedPermissions, null)
				.queue(
						(channel) -> {
							channel.sendMessageEmbeds(new SimpleEmbedBuilder("Application creation (1/3)")
									.text(
											"**Hi there " + member.getAsMention() + ",**",
											"",
											"Thank you for your interest in one of our open roles. To complete your application, you will be asked a few questions. This is divided into two parts: general questions in Part 1 and questions in Part 2 based on the direction you want to take.",
											"",
											"The questions we'd like answered are listed below. The button displays input fields where you can answer the questions. You can close the field at any time, and all progress is saved.",
											"***NOTE:** After 15 minutes, the input field will no longer function.*"
									)
									.field("What is your name and age?", "Please enter your name and age.", false)
									.field("What is your timezone?", "Please enter your timezone.", false)
									.field("Available time per week?", "Please enter your available time per week.", false)
									.field("Why do you want to join the team?", "Please enter your reason for wanting to join the team.", false)
									.field("What are your pro's and con's?", "Please enter your pro's and con's.", false)
									.build()
							).addActionRow(new ApplyButton.GeneralQuestionButton(member).build()).queue();
							new SqlApplication(Database.MEMBERSTable.getFromDiscordId(member.getIdLong()).get(0).getId(), channel.getIdLong(), channel.getTimeCreated().toEpochSecond(), null).save();

							future.complete(channel);
						},

						(error) -> {
							guild.createTextChannel("application-" + member.getId(), category)
									.addPermissionOverride(guild.getPublicRole(), null, allowedPermissions)
									// Add staff permissions
									.addRolePermissionOverride(supportRole, allowedPermissions, null)
									.addRolePermissionOverride(developmentRole, allowedPermissions, null)
									.addRolePermissionOverride(leadershipRole, allowedPermissions, null)
									// Add member permissions
									.addMemberPermissionOverride(member.getIdLong(), allowedPermissions, null)
									.queue(channel -> {
										channel.sendMessageEmbeds(new SimpleEmbedBuilder("Application creation (1/3)")
												.text(
														"**Hi there " + member.getAsMention() + ",**",
														"",
														"Thank you for your interest in one of our open roles. To complete your application, you will be asked a few questions. This is divided into two parts: general questions in Part 1 and questions in Part 2 based on the direction you want to take.",
														"",
														"The questions we'd like answered are listed below. The button displays input fields where you can answer the questions. You can close the field at any time, and all progress is saved.",
														"***NOTE:** After 15 minutes, the input field will no longer function.*"
												)
												.field("What is your name and age?", "Please enter your name and age.", false)
												.field("What is your timezone?", "Please enter your timezone.", false)
												.field("Available time per week?", "Please enter your available time per week.", false)
												.field("Why do you want to join the team?", "Please enter your reason for wanting to join the team.", false)
												.field("What are your pro's and con's?", "Please enter your pro's and con's.", false)
												.build()
										).addActionRow(new ApplyButton.GeneralQuestionButton(member).build()).queue();
										new SqlApplication(Database.MEMBERSTable.getFromDiscordId(member.getIdLong()).get(0).getId(), channel.getIdLong(), channel.getTimeCreated().toEpochSecond(), null).save();

										future.complete(channel);
									});
							});

		return future;
	}

	public static void finishApplicationCreation(@NotNull TextChannel channel, Member member) {
		SqlApplication sqlApplication = Database.APPLICATIONSTable.get(channel.getIdLong()).get(0);

		Application.Position position = sqlApplication.getCategory();

		channel.sendMessageEmbeds(new SimpleEmbedBuilder("Apply - " + member.getUser().getAsTag())
				.text(
						"Thank you for creating a Application.",
						"We will discuss your application and get back to you as soon as possible."
				)
				.field("Position", position.getEmoji().getFormatted() + " " + position.getName(), true)
				.success().build()).queue();

		channel.getManager().setTopic( "This Application has been created by " + member.getAsMention() + "\n" +
				" **- Created at:** <t:" + sqlApplication.getTime() + ":R>\n" +
				" **- Position:** " + position.getEmoji().getFormatted() + " " + position.getName() + "\n" ).queue();
	}

	public static void applicationClose(@NotNull TextChannel applicationChannel, long unixTime) {
		applicationChannel.sendMessageEmbeds(new SimpleEmbedBuilder("Apply - " + applicationChannel.getName())
				.text("This Application will be closed in 30 seconds.",
						"**Time closed:** <t:" + unixTime + ":R>")
				.error().build()).queue();

		// TODO: Add transcript system here

		new Thread(() -> {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			applicationChannel.delete().queue();
			Database.APPLICATIONSTable.remove(applicationChannel.getIdLong());
		}).start();
	}

	public static void applicationClose(@NotNull TextChannel applicationChannel, long unixTime, String reason) {
		applicationChannel.sendMessageEmbeds(new SimpleEmbedBuilder("Apply - " + applicationChannel.getName())
				.text("This Application will be closed in 30 seconds.",
						"**Time closed:** <t:" + unixTime + ":R>",
						"**Reason:** " + reason)
				.error().build()).queue();

		// TODO: Add transcript system here

		new Thread(() -> {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			applicationChannel.delete().queue();
			Database.APPLICATIONSTable.remove(applicationChannel.getIdLong());
		}).start();
	}
}
