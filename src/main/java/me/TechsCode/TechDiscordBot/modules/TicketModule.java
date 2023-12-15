package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.actions.buttons.TicketButtons;
import me.techscode.techdiscordbot.actions.menus.TicketMenus;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.TranscriptDatabase;
import me.techscode.techdiscordbot.database.entities.SqlTicket;
import me.techscode.techdiscordbot.database.entities.SqlTranscript;
import me.techscode.techdiscordbot.model.Logs;
import me.techscode.techdiscordbot.model.enums.Plugin;
import me.techscode.techdiscordbot.model.enums.Ticket;
import me.techscode.techdiscordbot.settings.Settings;
import me.techscode.techdiscordbot.transcripts.TicketTranscript;
import me.techscode.techdiscordbot.transcripts.TicketTranscriptOptions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TicketModule {

	private static final String TICKET_CHANNEL_ID = Settings.Modules.Ticket.channel;

	public static void embed() {

		if (!Settings.Modules.Ticket.enabled) return;

		// The Verification text channel
		final TextChannel channel = Objects.requireNonNull(TechDiscordBot.getJDA().getGuildById(SimpleSettings.Bot.MainGuild())).getTextChannelById(TICKET_CHANNEL_ID);

		// Create the verification embed
		final SimpleEmbedBuilder embed = new SimpleEmbedBuilder("📨 Tickets")
				.text(
						"Click the button below to create a ticket",
						"",
						"**NOTE;**",
						"**1.** You can have a maximum of 3 tickets open at a time.",
						"**2.** If you see no one online it doesn't mean you are alone in the ticket. Discord doesn't show offline members. If you are in a ticket and no one is responding, please wait patiently.",
						"",
						"**Data Collection;**",
						"We are currently gathering ticket data and messages contained within tickets to assist in the training of an Auto Response system. When you make a ticket, you understand and agree that your data will be collected."
				)
				.color(new Color(81, 153, 226));

		// Debug message to see what the ticket channel is
		assert channel != null;
		Debugger.debug("Tickets", "channel name == " + channel.getName() + " channel id == " + channel.getId());

		// Send the embed with the buttons to the verification channel
		channel.sendMessageEmbeds(embed.build())
				.setActionRow(
						new TicketButtons.TicketButton().build()
				).queue();
	}

	@NotNull
	public static CompletableFuture<TextChannel> ticketCreate(final Member creator, final Guild guild, Member owner) {

		if (Database.TICKETS.getFromMembedId(owner.getId()).size() >= 3) {
			return CompletableFuture.completedFuture(null);
		}


		// Permissions list for the ticket channel
		final List<Permission> permissions = new ArrayList<>(
				Arrays.asList(
						Permission.VIEW_CHANNEL,
						Permission.MESSAGE_HISTORY
				)
		);

		// Permissions list for the ticket channel
		final List<Permission> staffPermissions = new ArrayList<>(
				Arrays.asList(
						Permission.VIEW_CHANNEL,
						Permission.MESSAGE_HISTORY,
						Permission.MESSAGE_SEND,
						Permission.MESSAGE_ATTACH_FILES,
						Permission.MESSAGE_EMBED_LINKS,
						Permission.MESSAGE_ADD_REACTION,
						Permission.MANAGE_EMOJIS_AND_STICKERS
				)
		);

		// Get the members name if the members name can't be gathered it will use the user's ID
		final String memberName;
		if (Objects.equals(owner.getNickname(), "") || owner.getNickname() == null) {
			if (owner.getEffectiveName().equals("")) {
				memberName = owner.getUser().getId();
			} else {
				owner.getEffectiveName();
				memberName = owner.getEffectiveName();
			}
		} else {
			memberName = owner.getNickname();
		}

		// Get some settings from the settings file
		final Category category = guild.getCategoryById(Settings.Modules.Ticket.Category.support);
		final long staffRole = Settings.Roles.staff;

		CompletableFuture<TextChannel> future = new CompletableFuture<>();

		// Create the application channel for the member
		guild.createTextChannel("ticket-" + memberName, category)
				.addPermissionOverride(guild.getPublicRole(), null, permissions)
				// Add staff permissions
				.addRolePermissionOverride(staffRole, staffPermissions, null)
				// Add member permissions
				.addMemberPermissionOverride(owner.getIdLong(), permissions, Collections.singleton(Permission.MESSAGE_SEND))
				.queue(channel -> {

					channel.sendMessageEmbeds(new SimpleEmbedBuilder("📨 Ticket")
							.text(
									"Please select what kind of ticket you want to create.",
									"",
									"**Available categories:**",
									Ticket.Category.PLUGIN.getEmojiRaw() + " " + Ticket.Category.PLUGIN.getName() + " - " + Ticket.Category.PLUGIN.getDescription(),
									Ticket.Category.PAYMENTS.getEmojiRaw() + " " + Ticket.Category.PAYMENTS.getName() + " - " + Ticket.Category.PAYMENTS.getDescription(),
									Ticket.Category.DEVELOPER.getEmojiRaw() + " " + Ticket.Category.DEVELOPER.getName() + " - " + Ticket.Category.DEVELOPER.getDescription(),
									Ticket.Category.GIVEAWAY.getEmojiRaw() + " " + Ticket.Category.GIVEAWAY.getName() + " - " + Ticket.Category.GIVEAWAY.getDescription(),
									Ticket.Category.PATREON.getEmojiRaw() + " " + Ticket.Category.PATREON.getName() + " - " + Ticket.Category.PATREON.getDescription(),
									Ticket.Category.OTHER.getEmojiRaw() + " " + Ticket.Category.OTHER.getName() + " - " + Ticket.Category.OTHER.getDescription(),
									"",
									"*Please keep in mind that in order to access support for any premium resource, you must first verify your purchase.*"
							)
							.build()
					).addActionRow(new TicketMenus.TicketMenu(creator).build()).queue();

					new SqlTicket(owner.getIdLong(), channel.getIdLong(), channel.getTimeCreated().toEpochSecond(), null, null, null).save();

					future.complete(channel);
				});

		if (owner != creator) {
			Logs.TicketLogs.log(new SimpleEmbedBuilder("Ticket Created")
					.text(
							"Ticket created by " + creator.getAsMention() + " for " + owner.getAsMention(),
							"",
							"**Ticket:** " + future.join().getName() + " (" + future.join().getId() + ")"
					)
			);
		} else {
			Logs.TicketLogs.log(new SimpleEmbedBuilder("Ticket Created")
					.text(
							"Ticket created by " + creator.getAsMention(),
							"",
							"**Ticket:** " + future.join().getName() + " (" + future.join().getId() + ")"
					)
			);
		}

		return future;
	}

	public static void finishTicketCreation(@NotNull final TextChannel channel, final Member member) {
		final SqlTicket sqlTicket = Database.TICKETS.get(channel.getIdLong()).get(0);

		final Ticket.Category category = Ticket.Category.valueOf(sqlTicket.getCategory());
		final Ticket.Priority priority = Ticket.Priority.valueOf(sqlTicket.getPriority());
		final Member sqlTicketMember = sqlTicket.getMember().getDiscordMember();

		if (priority == Ticket.Priority.PATREON) {
			channel.getManager().setName("❗" + channel.getName()).queue();
		}


		if (sqlTicket.getType() == null) {
			channel.sendMessageEmbeds(new SimpleEmbedBuilder("Ticket - " + sqlTicket.getMember().getDiscordMember().getUser().getAsTag())
					.text(
					)
					.field("Category", category.getEmoji().getFormatted() + " " + category.getName(), true)
					.field("Priority", priority.getEmoji().getFormatted() + " " + priority.getName(), true)

					.success().build()).queue(message -> {
						message.pin().queue();
					}
			);

			if (member == sqlTicketMember) {
				channel.getManager().setTopic(
						"This ticket has been created by " + member.getAsMention() + "\n" +
								" **- Created at:** <t:" + sqlTicket.getTime() + ":R>\n" +
								" **- Category:** " + category.getEmoji().getFormatted() + " " + category.getName() + "\n" +
								" **- Priority:** " + priority.getEmoji().getFormatted() + " " + priority.getName()
				).queue();
			} else {
				channel.getManager().setTopic(
						"This ticket has been created by " + member.getAsMention() + " for: " + sqlTicketMember.getAsMention() + "\n" +
								" **- Created at:** <t:" + sqlTicket.getTime() + ":R>\n" +
								" **- Category:** " + category.getEmoji().getFormatted() + " " + category.getName() + "\n" +
								" **- Priority:** " + priority.getEmoji().getFormatted() + " " + priority.getName()
				).queue();
			}
		} else {

			final SimpleEmbedBuilder embedBuilder = new SimpleEmbedBuilder("📨 Ticket - " + sqlTicketMember.getUser().getAsTag())
					.text(
							"Thank you for contacting our support team, a member of our team will be with you shortly.",
							"While you wait for a member of our team to respond, please provide as much information as possible about your issue/question.",
							"",
							"Information about your ticket;"
					)
					.field("Category", category.getEmoji().getFormatted() + " " + category.getName(), true);

			String topic = "This ticket has been created by " + member.getAsMention() + "\n" +
					" **- Created at:** <t:" + sqlTicket.getTime() + ":R>\n" +
					" **- Category:** " + category.getEmoji().getFormatted() + " " + category.getName() + "\n";

			if (sqlTicket.getCategory().equals(Ticket.Category.PLUGIN.getId())) {
				final Plugin type = Plugin.valueOf(sqlTicket.getType());
				embedBuilder.field("Sub Category", type.getEmojiRaw() + " " + type.getName(), true);
				topic = topic + " **- Sub Category:** " + type.getEmoji().getFormatted() + " " + type.getName() + "\n";
			}
			if (sqlTicket.getCategory().equals(Ticket.Category.PAYMENTS.getId())) {
				final Ticket.Payment type = Ticket.Payment.valueOf(sqlTicket.getType());
				embedBuilder.field("Sub Category", type.getEmojiRaw() + " " + type.getName(), true);
				topic = topic + " **- Sub Category:** " + type.getEmojiRaw() + " " + type.getName() + "\n";
			}
			if (sqlTicket.getCategory().equals(Ticket.Category.DEVELOPER.getId())) {
				final Ticket.Developer type = Ticket.Developer.valueOf(sqlTicket.getType());
				embedBuilder.field("Sub Category", type.getEmojiRaw() + " " + type.getName(), true);
				topic = topic + " **- Sub Category:** " + type.getEmojiRaw() + " " + type.getName() + "\n";
			}
			if (sqlTicket.getCategory().equals(Ticket.Category.GIVEAWAY.getId())) {
				final Ticket.Giveaway type = Ticket.Giveaway.valueOf(sqlTicket.getType());
				embedBuilder.field("Sub Category", type.getEmojiRaw() + " " + type.getName(), true);
				topic = topic + " **- Sub Category:** " + type.getEmojiRaw() + " " + type.getName() + "\n";
			}
			if (sqlTicket.getCategory().equals(Ticket.Category.PATREON.getId())) {
				final Ticket.Patreon type = Ticket.Patreon.valueOf(sqlTicket.getType());
				embedBuilder.field("Sub Category", type.getEmojiRaw() + " " + type.getName(), true);
				topic = topic + " **- Sub Category:** " + type.getEmojiRaw() + " " + type.getName() + "\n";
			}

			channel.sendMessageEmbeds(embedBuilder
					.field("Priority", priority.getEmoji().getFormatted() + " " + priority.getName(), true)
					.success().build()).queue(message -> {
						message.pin().queue();
					}
			);

			channel.getManager().setTopic(topic + " **- Priority:** " + priority.getEmoji().getFormatted() + " " + priority.getName()).queue();
		}

		if (category == Ticket.Category.PLUGIN) {
			channel.sendMessageEmbeds(new SimpleEmbedBuilder("Information")
					.text(
							"Please provide the following information:",
							"\u200E "
					)
					.field("Plugin Version", "`/about {Plugin}`", true)
					.field("Server Version & Build", "`/about`", true)
					.field("Plugin List", "`/plugins`", true)
					.field("Error", "The error you are getting. *(If there is one)*", false)
					.field("Additional Information", "Any additional information you think is important.", false)
					.build()
			).queue();
		}

		// Permissions list for the ticket channel
		final List<Permission> permissions = new ArrayList<>(
				Arrays.asList(
						Permission.VIEW_CHANNEL,
						Permission.MESSAGE_HISTORY,
						Permission.MESSAGE_SEND,
						Permission.MESSAGE_ATTACH_FILES,
						Permission.MESSAGE_EMBED_LINKS,
						Permission.MESSAGE_ADD_REACTION,
						Permission.MANAGE_EMOJIS_AND_STICKERS
				)
		);
		channel.getManager().putMemberPermissionOverride(sqlTicket.getMember().getDiscordId(), permissions, null).queue();
	}

	public static void ticketClose(Member member, @NotNull final SqlTicket sqlTicket, final long unixTime, final String reason, int delay) {
		TextChannel textChannel = sqlTicket.getChannel();

		TicketTranscript transcript = TicketTranscript.buildTranscript(textChannel, TicketTranscriptOptions.DEFAULT);

		transcript.build(object -> {
			if (sqlTicket.getMember().getDiscordMember() != null) {
				Logs.TicketLogs.log(
						new SimpleEmbedBuilder("Ticket Transcript (Command)")
								.text("Transcript of " + textChannel.getName() + " (" + textChannel.getId() + "): " + transcript.getUrl())
								.color(Color.ORANGE)
				);
			}

			textChannel.sendMessageEmbeds(
					new SimpleEmbedBuilder("Ticket Transcript")
							.text("Transcript of " + textChannel.getName() + " (" + textChannel.getId() + "): " + transcript.getUrl())
							.color(Color.ORANGE)
							.build()
			).queue();

			TranscriptDatabase.TRANSCRIPT.add(new SqlTranscript(object.get("id").getAsString(), object.toString()));
		});

		ticketRemove(textChannel, delay);

		if (delay >= 3600000) {
			textChannel.sendMessageEmbeds(new SimpleEmbedBuilder("Ticket - " + textChannel.getName())
					.text("This ticket will be closed in " + TimeUnit.MILLISECONDS.toHours(delay) + " hours.",
							"**Time closed:** <t:" + unixTime + ":R>",
							"**Reason:** " + reason)
					.error().build()).queue();
		} else {
			textChannel.sendMessageEmbeds(new SimpleEmbedBuilder("Ticket - " + textChannel.getName())
					.text("This ticket will be closed in " + TimeUnit.MILLISECONDS.toMinutes(delay) + " minutes.",
							"**Time closed:** <t:" + unixTime + ":R>",
							"**Reason:** " + reason)
					.error().build()).queue();
		}

		Common.dm(sqlTicket.getMember().getDiscordUser(), new SimpleEmbedBuilder("📨 Ticket closed")
				.text("Your ticket has been closed by " + member.getAsMention() + ".",
						"**Time closed:** <t:" + unixTime + ":R>",
						"**Reason:** " + reason)
				.error());

		Logs.TicketLogs.log(new SimpleEmbedBuilder("Ticket closed")
				.text("Ticket " + textChannel.getName() + " (" + textChannel.getId() + ") has been closed by " + member.getAsMention() + ".",
						"**Time closed:** <t:" + unixTime + ":R>",
						"**Reason:** " + reason)
				.error());

        /*TicketTranscript transcript = TicketTranscript.buildTranscript(ticketChannel, TicketTranscriptOptions.DEFAULT);
		String channelId = ticketChannel.getId();

		transcript.build(object -> {
			Common.warning("test-3.1");
			new SimpleEmbedBuilder("Transcript")
					.text("You can view your recently closed ticket's transcript here:\n" + transcript.getUrl())
					.color(Color.ORANGE)
					.queue(member);

			Common.warning("test-3.2");
			new SqlTranscript(object.get("id").getAsString(), object.toString()).save();
			Common.warning("test-3.3");
		});*/

	}

	private static void ticketRemove(@NotNull final TextChannel ticketChannel, int delay) {
		final Thread thread = new Thread(() -> {
			try {
				Thread.sleep(delay);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			ticketChannel.delete().queue();
			Database.TICKETS.remove(ticketChannel.getIdLong());
		});
		thread.setName("TicketClose" + ticketChannel.getId());
		thread.start();
	}
}
