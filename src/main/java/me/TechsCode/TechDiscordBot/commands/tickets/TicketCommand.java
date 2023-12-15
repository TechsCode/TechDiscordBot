package me.techscode.techdiscordbot.commands.tickets;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlTicket;
import me.techscode.techdiscordbot.modules.TicketModule;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TicketCommand extends SimpleSlashCommand {

    public TicketCommand() {
        super("ticket");
        description("Create a ticket");

        mainGuildOnly();

        subCommands(
                new SubcommandData("create", "Create a ticket"),
                new SubcommandData("close", "Close a ticket").addOption(OptionType.STRING, "reason", "The reason for closing the ticket", true)
        );
    }

    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {

        String subCommand = event.getSubcommandName();

        assert subCommand != null;
        if(subCommand.equals("create")) {
            // Create the ticket channel
            CompletableFuture<TextChannel> future = TicketModule.ticketCreate(getMember(), getGuild(), getMember());

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
                                "Your ticket has been created.",
                                "To complete the ticket creation process, please follow the steps in " + channel.getAsMention()
                        )
                        .success().build()).setEphemeral(true).queue();
            });

        } else if(subCommand.equals("close")) {
            long channelId = event.getChannel().getIdLong();
            List<SqlTicket> sqlTickets = Database.TICKETS.get(channelId);

            if (sqlTickets.size() == 0) {
                event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Not Found")
                        .text(
                                "This ticket does not exist in the database.",
                                "You will need to delete this ticket manually."
                        )
                        .error().build()).setEphemeral(true).queue();
                return;
            }


            String reason = event.getOption("reason").getAsString();
            int length = reason.split(" ").length;

            if (length < 3) {
                event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Not Closed")
                        .text(
                                "You have filled in a reason that is too short.",
                                "Please provide a reason that is longer than 3 words.",
                                "",
                                "Example reason: 'Fixed Ultra Regions FLY flag not working correctly'"
                        )
                        .error().build()).setEphemeral(true).queue();
                return;
            }

            for (SqlTicket sqlTicket : sqlTickets) {
                if (sqlTicket.getChannelId() != channelId) continue;

                if (sqlTicket.getMember().getDiscordId() == event.getMember().getIdLong() || SimpleRoles.hasRole(event.getMember(), Settings.Roles.staff)) {
                    TicketModule.ticketClose(event.getMember(), sqlTicket, event.getTimeCreated().toEpochSecond(), reason, 30000);
                    event.reply("Ticket closed").setEphemeral(true).queue(message -> message.deleteOriginal().queue());
                } else {
                    event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Not Closed")
                            .text(
                                    "It seems that you are not the owner of this ticket.",
                                    "Only the ticket owner can close the ticket.",
                                    "",
                                    "*If you think this is a mistake, please contact a staff member.*"
                            )
                            .error().build()).setEphemeral(true).queue();
                }
                return;
            }
        }
    }
}
