package me.techscode.techdiscordbot.commands.staff;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlMember;
import me.techscode.techdiscordbot.database.entities.SqlTicket;
import me.techscode.techdiscordbot.modules.TicketModule;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.List;
import java.util.Objects;

public class QuestionCommand extends SimpleSlashCommand {

    /**
     * Create the timeout command with its specific settings
     */
    public QuestionCommand() {
        super("q");
        description("give pre made answers and questions");

        mainGuildOnly();

        subcommandGroup(new SubcommandGroupData("ticket", "ticket answers and questions")
                        .addSubcommands(new SubcommandData("unverified", "Unverified premium plugin support ticket").addOption(OptionType.USER, "user", "The user that needs to be pinged", true))
                        .addSubcommands(new SubcommandData("close", "Ask the owner if the ticket can be closed")),
                new SubcommandGroupData("spark", "spark answers and questions")
                        .addSubcommands(new SubcommandData("info", "Give information about spark").addOption(OptionType.USER, "user", "The user that needs this info", false))
                        .addSubcommands(new SubcommandData("report", "Ask for a spark report")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "type", "The type of report you want to ask for", true)
                                                .addChoice("profiler", "profiler")
                                                .addChoice("health", "health")
                                                .addChoice("tickmonitor", "tickmonitor")
                                                .addChoice("garbagecollection", "garbagecollection")
                                                .addChoice("heapsummary", "heapsummary")
                                                .addChoice("heapdump", "heapdump")
                                )
                                .addOption(OptionType.USER, "user", "The user you want to ask for a spark report", false))
        );
    }

    /**
     * The main code of the timeout command
     *
     * @param event SlashCommandInteractionEvent
     */
    @Override
    protected void onCommand(final SlashCommandInteractionEvent event) {


        if (event.getSubcommandGroup().equals("ticket")) {
            switch (Objects.requireNonNull(event.getSubcommandName())) {
                case "unverified" -> {
                    event.reply("success").queue(message -> message.deleteOriginal().queue());
                    event.getMessageChannel().sendMessage(event.getOption("user").getAsUser().getAsMention()).addEmbeds(
                            new SimpleEmbedBuilder("Premium plugin support")
                                    .text(
                                            "Hi there,",
                                            "",
                                            "It seems that you are trying to make a support ticket for a premium plugin, but you are not verified.",
                                            "In order to get verified, you need to verify your purchase in <#907349490556616745>",
                                            "Once you are verified, you will be able to make support tickets for premium plugins.",
                                            "",
                                            "If you have already send a verification request, please wait for a staff member to verify your purchase.",
                                            "",
                                            "If you have a pre sale question go to <#916558825367158794>",
                                            "",
                                            "This ticket will be closed in 1 hour."
                                    ).build()
                    ).queue();
                    TicketModule.ticketClose(event.getOption("user").getAsMember(), Database.TICKETS.get(event.getChannel().getIdLong()).get(0), event.getTimeCreated().toEpochSecond(), "For pre sale question go to <#916558825367158794>, for verifying purchases go to <#907349490556616745>", 3600000);
                }
                case "close" -> {
                    event.reply("success").queue(message -> message.deleteOriginal().queue());
                    long channelId = event.getChannel().getIdLong();
                    List<SqlTicket> sqlTickets = Database.TICKETS.get(channelId);
                    if (sqlTickets.size() == 0) {
                        event.replyEmbeds(new SimpleEmbedBuilder("Further assistance")
                                .text(
                                        "Hi there,",
                                        "",
                                        "We would like to know if we can close this ticket.",
                                        "If you need further assistance, please reply to this message.",
                                        "",
                                        "This ticket will be closed in 24 hours if no reply is given."
                                )
                                .error().build()).setEphemeral(true).queue();
                        return;
                    }
                    SqlMember sqlMember = sqlTickets.get(0).getMember();
                    event.getMessageChannel().sendMessage(sqlMember.getDiscordMember().getAsMention()).addEmbeds(
                            new SimpleEmbedBuilder("Further assistance")
                                    .text(
                                            "Hi there,",
                                            "",
                                            "We would like to know if we can close this ticket.",
                                            "If you need further assistance, please reply to this message.",
                                            "",
                                            "This ticket will be closed in 24 hours if no reply is given."
                                    ).build()
                    ).queue();
                    return;
                }
            }
        }

        if (event.getSubcommandGroup().equals("spark")) {
            SimpleEmbedBuilder embed = new SimpleEmbedBuilder();

            if (Objects.equals(event.getSubcommandName(), "info")) {
                embed.setAuthor("⚡ Spark");
                embed.text("Spark is a plugin that allows you to see what is happening on your server.",
                                "",
                                "You can find more information about Spark on [their website](https://spark.lucko.me).");
                event.replyEmbeds(embed.build()).queue();
                return;
            }

            if (Objects.equals(event.getSubcommandName(), "report")) {
                switch (Objects.requireNonNull(event.getOption("type")).getAsString()) {
                    case "profiler" -> {
                        embed.setAuthor("⚡ Spark Profiler");
                        embed.text("It seems that we would like to receive a Spark report from your server.",
                                        "",
                                        "[Spark](https://spark.lucko.me) is a plugin that allows you to see what is happening on your server.")
                                .field("Create a Spark Profile", "To create a report you need to run `/spark profiler start`\nOnce you are ready to see the results use `/spark profiler stop` to view the end result.", false)
                                .field("How long should the Profile last?", "The longer the profile runs the more accurate the results will be.\nWe recommend running the profile for at least 30 minutes.", false);
                    }
                    case "health" -> {
                        embed.setAuthor("⚡ Spark Health");
                        embed.text("It seems that we would like a Spark Health report from your server.",
                                        "",
                                        "[Spark](https://spark.lucko.me) is a plugin that allows you to see what is happening on your server.")
                                .field("Create a Spark Health report", "To create a report you need to run `/spark health `.\nProvide this report and if needed the one down below.", false)
                                .field("Additional commands", "`/spark health --memory` is needed when there are memory issues.\n`/spark health --network` is needed when there are network issues.", false);
                    }
                    case "tickmonitor" -> {
                        embed.setAuthor("⚡ Spark Tick-monitor");
                        embed.text("We would like to investigate what is happening on your server if you are experiencing lag.",
                                        "The Spark Tickmonitor is a tool that shows the duration of a tick as well as what occurs during a tick.",
                                        "",
                                        "[Spark](https://spark.lucko.me) is a plugin that allows you to see what is happening on your server.")
                                .field("Enable Tick-monitor", "To enable the Tickmonitor you need to run `/spark tickmonitor`. This commands toggles the status of the ticketmonitor.", false)
                                .field("Additional commands", "`/spark tickmonitor --threshold <percent>` only reporting ticks which exceed a percentage increase from the average tick duration.\n`/spark tickmonitor --threshold-tick <milliseconds>` only reporting ticks which exceed the given duration in milliseconds.\n`/spark tickmonitor --without-gc` disable reports about GC activity.", false);
                    }
                    case "garbagecollection" -> {
                        embed.setAuthor("⚡ Spark Garbage Collection");
                        embed.text("It seems that we need to take a deeper look in your memory.",
                                        "There for we are gonna take a look in your GC (Garbage Collection).",
                                        "",
                                        "[Spark](https://spark.lucko.me) is a plugin that allows you to see what is happening on your server.")
                                .field("Print GC", "To print your GC you need to run `/spark gc`", false)
                                .field("Monitor GC", "To activly view your GC you need to run `/spark gcmonitor`", false);
                    }
                    case "heapsummary" -> {
                        embed.setAuthor("⚡ Spark Heap summary");
                        embed.text("It seems that we need a better look at what is stored in your memory.",
                                        "",
                                        "[Spark](https://spark.lucko.me) is a plugin that allows you to see what is happening on your server.")
                                .field("Get Heapsummary", "To get a Heapsummary your need to run `/spark heapsummary` this returns a view with all data stored in its memory", false);
                    }
                    case "heapdump" -> {
                        embed.setAuthor("⚡ Spark Heapdump");
                        embed.text("Spark has an option to generates a new heapdump (.hprof snapshot) file and saves that heapdump to the disk.",
                                        "**NOTE;** This require as much space as your available RAM!",
                                        "",
                                        "[Spark](https://spark.lucko.me) is a plugin that allows you to see what is happening on your server.")
                                .field("Create Heapdump", "To create a Heapdump run `/spark heapdump` this saves a file to your disk.", false)
                                .field("Additional commands", "`/spark heapdump --compress <type>` to specify that the heapdump should be compressed using the given type. The supported types are gzip, xz and lzma.", false);
                    }
                }
            }

            OptionMapping user = event.getOption("user");
            if (user == null) {
                event.replyEmbeds(embed.build()).queue();
            } else {
                event.reply("<@" + user.getAsUser().getId() + ">").addEmbeds(embed.build()).queue();
            }
        }
    }
}
