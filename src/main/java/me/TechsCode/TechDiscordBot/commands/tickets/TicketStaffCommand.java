package me.techscode.techdiscordbot.commands.tickets;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.TranscriptDatabase;
import me.techscode.techdiscordbot.database.entities.SqlTicket;
import me.techscode.techdiscordbot.database.entities.SqlTranscript;
import me.techscode.techdiscordbot.model.Logs;
import me.techscode.techdiscordbot.modules.TicketModule;
import me.techscode.techdiscordbot.settings.Settings;
import me.techscode.techdiscordbot.transcripts.TicketTranscript;
import me.techscode.techdiscordbot.transcripts.TicketTranscriptOptions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TicketStaffCommand extends SimpleSlashCommand {

	public TicketStaffCommand() {
		super("ticketstaff");

		description("Manage tickets");

		mainGuildOnly();

		subCommands(
				new SubcommandData("create", "Create a ticket for a user").addOption(OptionType.USER, "user", "The user to create a ticket for", true),
				new SubcommandData("add", "Add a user to a ticket").addOption(OptionType.USER, "user", "The user that needs to be added to the ticket", true),
				new SubcommandData("remove", "Remove a user from a ticket").addOption(OptionType.USER, "user", "The user that needs to be removed to the ticket", true),
				new SubcommandData("transcript", "Get a transcript of a ticket"),
				new SubcommandData("list", "List all tickets"),
				new SubcommandData("info", "Get information about a ticket"),
				new SubcommandData("rename", "Rename a ticket"),
				new SubcommandData("transfer", "Transfer a ticket to another category").addOptions(new OptionData(OptionType.STRING, "category", "The category to transfer the ticket to", true).addChoice("support", "support").addChoice("development", "development").addChoice("management", "management").addChoice("greazi", "greazi").addChoice("timo", "timo").addChoice("lucifer", "lucifer").addChoice("ghost", "ghost").addChoice("fabian", "fabian").addChoice("peng", "peng").addChoice("das", "das").addChoice("opti", "opti")),
				new SubcommandData("archive", "Archive a ticket"),
				new SubcommandData("unarchive", "Unarchive a ticket"),
				new SubcommandData("delete", "Delete a ticket")
		);
	}

	@Override
	protected void onCommand(@NotNull final SlashCommandInteractionEvent event) {
		final String subCommand = event.getSubcommandName();

        assert subCommand != null;
        if (subCommand.equals("create")) {
			// Create the ticket channel
			Member createMember = event.getOption("user").getAsMember();
			CompletableFuture<TextChannel> future = TicketModule.ticketCreate(event.getMember(), event.getGuild(), createMember);
			future.thenAccept(channel -> {
				if (channel == null) {
					event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Creation Failed")
							.text(
									"Your ticket could not be created.",
									"You have reached the maximum amount of tickets you can create."
							)
							.error().build()).setEphemeral(true).queue();
					return;
				}

				// Send a message to the user
				event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Created")
						.text(
								"A new ticket has been created for another user.",
								"The other user will be able to follow the steps inside " + channel.getAsMention()
						)
						.success().build()).setEphemeral(true).queue();
				event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Ticket Created")
						.text(
								createMember.getAsMention() + " there has been created a ticket for you.",
								"Please follow the steps inside " + channel.getAsMention()
						)
						.success().build()).queue();
			});
			return;
		}

		TextChannel textChannel = event.getChannel().asTextChannel();

		List<SqlTicket> sqlTickets = Database.TICKETS.get(textChannel.getIdLong());

		if (sqlTickets.isEmpty()) {
			event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Not Found")
					.text(
							"This channel is not a ticket and can not be managed with this command.",
							"If you think this is a mistake, please contact my developer."
					)
					.error().build()).setEphemeral(true).queue();
			return;
		}

		SqlTicket sqlTicket = null;
		for (SqlTicket sqlTicket1 : sqlTickets) {
			if (sqlTicket1.getChannelId() == textChannel.getIdLong()) sqlTicket = sqlTicket1;
		}

        switch (Objects.requireNonNull(subCommand)) {
            case "add" -> {
                if (event.getMember().getIdLong() == event.getOption("user").getAsUser().getIdLong()) {
                    event.replyEmbeds(new SimpleEmbedBuilder("Error")
                            .text("You cannot add yourself to a ticket.")
                            .error().build()).setEphemeral(true).queue();
                    return;
                }
                for (Member member : textChannel.getMembers()) {
                    if (member.getIdLong() == event.getOption("user").getAsUser().getIdLong()) {
                        event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                .text("This user is already inside the ticket.")
                                .error().build()).setEphemeral(true).queue();
                        return;
                    }
                }
                Member addMember = event.getOption("user").getAsMember();
                final List<Permission> permissionsAdd = new ArrayList<>(
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
                textChannel.getManager().putMemberPermissionOverride(addMember.getIdLong(), permissionsAdd, null).queue();
                event.replyEmbeds(new SimpleEmbedBuilder("Success")
                        .text("Successfully added " + addMember.getAsMention() + " to the ticket.")
                        .success().build()).setEphemeral(true).queue();
                Logs.TicketLogs.log(new SimpleEmbedBuilder("Ticket Member Added")
                        .text(
                                "**Member:** " + addMember.getAsMention(),
                                "**Ticket:** " + textChannel.getName() + " (" + textChannel.getId() + ")"
                        )
                );
                return;
            }
            case "remove" -> {
                if (event.getMember().getIdLong() == event.getOption("user").getAsUser().getIdLong()) {
                    event.replyEmbeds(new SimpleEmbedBuilder("Error")
                            .text("You cannot remove yourself of a ticket.")
                            .error().build()).setEphemeral(true).queue();
                    return;
                }
                if (sqlTicket.getMember().getDiscordId() == event.getOption("user").getAsUser().getIdLong()) {
                    event.replyEmbeds(new SimpleEmbedBuilder("Error")
                            .text("You cannot remove the ticket owner from there own ticket.")
                            .error().build()).setEphemeral(true).queue();
                    return;
                }
                for (Member member : textChannel.getMembers()) {
                    if (member.getIdLong() == event.getOption("user").getAsUser().getIdLong()) {
                        Member removeMember = event.getOption("user").getAsMember();

                        final List<Permission> permissionsRemove = new ArrayList<>(
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
                        textChannel.getManager().putMemberPermissionOverride(removeMember.getIdLong(), null, permissionsRemove).queue();
                        event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                .text("Successfully removed " + removeMember.getAsMention() + " from the ticket.")
                                .success().build()).setEphemeral(true).queue();

                        Logs.TicketLogs.log(new SimpleEmbedBuilder("Ticket Member Removed")
                                .text(
                                        "**Member:** " + removeMember.getAsMention(),
                                        "**Ticket:** " + textChannel.getName() + " (" + textChannel.getId() + ")"
                                )
                        );
                        return;
                    }
                }
                event.replyEmbeds(new SimpleEmbedBuilder("Error")
                        .text("This user is already removed from the ticket.")
                        .error().build()).setEphemeral(true).queue();
                return;
            }
            case "transcript" -> {
                TicketTranscript transcript = TicketTranscript.buildTranscript(event.getChannel().asTextChannel(), TicketTranscriptOptions.DEFAULT);
                SqlTicket finalSqlTicket = sqlTicket;
                transcript.build(object -> {
                    assert finalSqlTicket != null;
                    if (finalSqlTicket.getMember().getDiscordMember() != null) {
                        Logs.TicketLogs.log(
                                new SimpleEmbedBuilder("Ticket Transcript (Command)")
                                        .text("Transcript of " + event.getChannel().getAsMention() + ": " + transcript.getUrl())
                                        .color(Color.ORANGE)
                        );
                    }

                    event.replyEmbeds(
                            new SimpleEmbedBuilder("Ticket Transcript")
                                    .text("Transcript of " + event.getChannel().getAsMention() + ": " + transcript.getUrl())
                                    .color(Color.ORANGE)
                                    .build()
                    ).queue();

                    TranscriptDatabase.TRANSCRIPT.add(new SqlTranscript(object.get("id").getAsString(), object.toString()));
                });
            }
            case "list" -> {
            }
            case "info" -> {
            }
            case "rename" -> {
            }
            case "transfer" -> {
                OptionMapping optionMap = event.getOption("category");
                if (optionMap == null) {
                    event.replyEmbeds(new SimpleEmbedBuilder("Error")
                            .text("You need to provide a category.")
                            .error().build()).setEphemeral(true).queue();
                    return;
                }
                switch (optionMap.getAsString()) {
                    case "support" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.support) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the support category.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.support)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the support category.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "development" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.development) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the development category.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.development)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the development category.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "management" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.management) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the development category.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.management)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the development category.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "greazi" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.greazi) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Greazi.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.greazi)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Greazi.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "timo" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.timo) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Timo.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.timo)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Timo.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "lucifer" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.lucifer) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Lucifer.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.lucifer)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Lucifer.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "ghost" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.ghost) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Ghost.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.ghost)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Ghost.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "fabian" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.fabian) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Fabian.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.fabian)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Fabian.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "peng" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.peng) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Peng.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.peng)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Peng.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "das" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.das) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Das.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.das)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Das.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                    case "opti" -> {
                        if (textChannel.getParentCategoryIdLong() == Settings.Modules.Ticket.Category.opti) {
                            event.replyEmbeds(new SimpleEmbedBuilder("Error")
                                    .text("This ticket is already in the category for Opti.")
                                    .error().build()).setEphemeral(true).queue();
                            return;
                        } else {
                            textChannel.getManager().setParent(event.getJDA().getCategoryById(Settings.Modules.Ticket.Category.opti)).queue();
                            event.replyEmbeds(new SimpleEmbedBuilder("Success")
                                    .text("Successfully transferred the ticket to the category for Opti.")
                                    .success().build()).setEphemeral(true).queue();
                        }
                    }
                }
            }
            case "archive" -> {
            }
            case "unarchive" -> {
            }
            case "delete" -> {
            }
        }
	}
}
