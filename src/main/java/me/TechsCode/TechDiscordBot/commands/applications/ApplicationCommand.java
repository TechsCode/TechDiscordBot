package me.techscode.techdiscordbot.commands.applications;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlApplication;
import me.techscode.techdiscordbot.modules.ApplyModule;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static me.techscode.techdiscordbot.modules.ApplyModule.applicationCreate;

public class ApplicationCommand extends SimpleSlashCommand {

    public ApplicationCommand() {
        super("application");

        mainGuildOnly();

        subCommands(
                new SubcommandData("create", "Create a ticket"),
                new SubcommandData("close", "Close a ticket")
        );
    }

    @Override
    protected void onCommand(@NotNull SlashCommandInteractionEvent event) {

        String subCommand = event.getSubcommandName();

        assert subCommand != null;
        if(subCommand.equals("create")) {
            // Create the ticket channel
            CompletableFuture<TextChannel> future = applicationCreate(getMember(), getGuild());
            future.thenAccept(channel -> {
                // Send a message to the user
                event.replyEmbeds(new SimpleEmbedBuilder("Application Created")
                        .text(
                                "A new application has been created.",
                                "Please follow the steps in " + channel.getAsMention()
                        )
                        .success().build()).setEphemeral(true).queue();
            });

        } else if(subCommand.equals("close")) {
            long channelId = event.getChannel().getIdLong();
            List<SqlApplication> sqlApplications = Database.APPLICATIONSTable.get(channelId);

            if (sqlApplications.size() == 0) {
                event.replyEmbeds(new SimpleEmbedBuilder("Ticket Not Found")
                        .text("This channel is not a application. Please use this command in a application channel.",
                                "If you think this is a mistake, please contact a staff member.")
                        .error().build()).setEphemeral(true).queue();
                return;
            }

            for (SqlApplication sqlApplication : sqlApplications) {
                if (sqlApplication.getChannelId() != channelId) continue;
                if (sqlApplication.getMember().get(0).getDiscordId() == event.getMember().getIdLong() || SimpleRoles.hasRole(event.getMember(), Settings.Roles.staff)) {
                    // Close the ticket
                    ApplyModule.applicationClose(sqlApplication.getChannel(), event.getTimeCreated().toEpochSecond());
                    event.reply("Application closed").setEphemeral(true).queue(message -> message.deleteOriginal().queue());
                } else {
                    event.replyEmbeds(new SimpleEmbedBuilder("Not a owner or Staff member")
                            .text("You are not the owner of this Application or a staff member. There for you can't close it.")
                            .error().build()).setEphemeral(true).queue();
                }
                return;
            }
        }
    }
}
