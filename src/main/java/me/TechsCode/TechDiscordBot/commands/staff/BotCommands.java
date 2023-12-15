package me.techscode.techdiscordbot.commands.staff;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.actions.buttons.BotActionButtons;
import me.techscode.techdiscordbot.modules.ApplyModule;
import me.techscode.techdiscordbot.modules.TicketModule;
import me.techscode.techdiscordbot.modules.VerificationModule;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BotCommands extends SimpleSlashCommand {

    public BotCommands() {
        super("manage");
        description("manage some things in the bot");

        subCommands(
                new SubcommandData("restart", "restart the bot"),
                /*new SubcommandData("shutdown", "shutdown the bot"),*/
                new SubcommandData("embeds", "manage embeds")
                        .addOptions(
                                new OptionData(OptionType.STRING, "type", "the type of embed", true)
                                        .addChoice("overview", "overview")
                                        .addChoice("ticket", "ticket")
                                        .addChoice("application", "application")
                                        .addChoice("verification", "verification"),
                                new OptionData(OptionType.STRING, "action", "the action to perform", true)
                                        .addChoice("send", "send")
                                        .addChoice("resend", "resend")
                                        .addChoice("remove", "remove")
                        )
        );


        mainGuildOnly();
    }

    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName();

        switch (Objects.requireNonNull(subCommand)) {
            case "restart" -> {
                event.replyEmbeds(new SimpleEmbedBuilder("Restart")
                        .text("To restart the bot you need to to confirm you want to restart the bot. To do this, press the Confirm button below.")
                        .error().build()).addActionRow(new BotActionButtons.shutdown().build()).setEphemeral(true).queue();
                TechDiscordBot.getInstance().stop();
            }
            case "shutdown" -> event.replyEmbeds(new SimpleEmbedBuilder("Shutdown")
                    .text("To shutdown the bot you need to to confirm you want to stop the bot. To do this, press the Confirm button below.")
                    .error().build()).addActionRow(new BotActionButtons.shutdown().build()).setEphemeral(true).queue();
            case "embeds" -> {
                String type = Objects.requireNonNull(event.getOption("type")).getAsString();
                String action = Objects.requireNonNull(event.getOption("action")).getAsString();


                SimpleEmbedBuilder embed = new SimpleEmbedBuilder("Embeds").success();
                TextChannel channel;
                switch (type) {
                    case "overview":
                        break;
                    case "ticket":
                        channel = event.getJDA().getTextChannelById(Settings.Modules.Ticket.channel);
                        assert channel != null;
                        switch (action) {
                            case "send" -> {
                                TicketModule.embed();
                                embed.text("The embed has been sent.",
                                        channel.getAsMention());
                            }
                            case "resend" -> {
                                channel.getIterableHistory().takeAsync(10).thenAccept(channel::purgeMessages);
                                TicketModule.embed();
                                embed.text("The embed has been resent.",
                                        channel.getAsMention());
                            }
                            case "remove" -> {
                                channel.getIterableHistory().takeAsync(10).thenAccept(channel::purgeMessages);
                                embed.text("The embed has been removed.",
                                        channel.getAsMention());
                            }
                        }
                        break;
                    case "application":
                        channel = event.getJDA().getTextChannelById(Settings.Modules.Apply.channel);
                        assert channel != null;
                        switch (action) {
                            case "send" -> {
                                ApplyModule.embed();
                                embed.text("The embed has been sent.",
                                        channel.getAsMention());
                            }
                            case "resend" -> {
                                channel.getIterableHistory().takeAsync(10).thenAccept(channel::purgeMessages);
                                ApplyModule.embed();
                                embed.text("The embed has been resent.",
                                        channel.getAsMention());
                            }
                            case "remove" -> {
                                channel.getIterableHistory().takeAsync(10).thenAccept(channel::purgeMessages);
                                embed.text("The embed has been removed.",
                                        channel.getAsMention());
                            }
                        }
                        break;
                    case "verification":
                        channel = event.getJDA().getTextChannelById(Settings.Modules.Verification.channel);
                        assert channel != null;
                        switch (action) {
                            case "send" -> {
                                VerificationModule.embed();
                                embed.text("The embed has been sent.",
                                        channel.getAsMention());
                            }
                            case "resend" -> {
                                channel.getIterableHistory().takeAsync(10).thenAccept(channel::purgeMessages);
                                VerificationModule.embed();
                                embed.text("The embed has been resent.",
                                        channel.getAsMention());
                            }
                            case "remove" -> {
                                channel.getIterableHistory().takeAsync(10).thenAccept(channel::purgeMessages);
                                embed.text("The embed has been removed.",
                                        channel.getAsMention());
                            }
                        }
                        break;
                }
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
            }
        }
    }
}
