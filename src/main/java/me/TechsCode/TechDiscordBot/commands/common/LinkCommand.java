package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.model.enums.Plugin;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.util.Objects;

public class LinkCommand extends SimpleSlashCommand {

    public LinkCommand() {
        super("link");
        description("Will return a link to one of our websites");

        subCommands(new SubcommandData("addon", "Returns the addon website"),
                new SubcommandData("guides", "Returns the UC Guide website"),
                new SubcommandData("insaneeditor", "Return the InsaneEditor website"),
                new SubcommandData("polymart", "Returns the Polymart website"),
                new SubcommandData("songoda", "Returns the Songoda website"),
                new SubcommandData("spigot", "Returns the Spigot website"),
                new SubcommandData("translations", "Returns the translations website")
        );
    }

    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {
        String subcommandData = event.getSubcommandName();

        switch (Objects.requireNonNull(subcommandData)) {
            case "addon" -> event.replyEmbeds(new SimpleEmbedBuilder("Addon Website")
                    .text("All our addon's are stored and manged thru our addons website.",
                            "https://ultraadditions.com/",
                            "",
                            "Welcome to the page where you can find, download, and upload addons to TechsCode's Plugins. Enhancing the capabilities of UltraCustomizer and UltraRegions.")
                    .color(new Color(192, 38, 211))
                    .build()).queue();
            case "guides" -> event.replyEmbeds(new SimpleEmbedBuilder("Guides Website")
                    .text("Guides give more and a detailed example of how to setup or create a system for Ultra Customizer.",
                            "https://guides.techscode.com",
                            "",
                            "The guides website provides a simple website on how to use Ultra Customizer en on how to create a system. (This page might be integrated in to the main website in the future)")
                    .color(new Color(174, 84, 206))
                    .image("https://i.imgur.com/KBw2szW.png")
                    .build()).queue();
            case "insaneeditor" -> event.replyEmbeds(new SimpleEmbedBuilder("Insane Editor")
                    .text("Use a web interface to edit in game settings and values of some of our plugins",
                            "**Under maintenance**",
                            "",
                            "This website allows you to edit permissions and settings for our plugins. Currently it is offline and being updated to a newer and faster system.")
                    .color(new Color(113, 225, 207))
                    .image("https://i.imgur.com/4C08sQD.jpg")
                    .build()).queue();
            case "polymart" -> event.replyEmbeds(new SimpleEmbedBuilder("Polymart")
                    .text("Polymart is a marketplace where we sell our resources",
                            "https://polymart.org/user/techscode.5485",
                            "",
                            "Buy our resources from our marketplace and verify your purchase here: <#907349490556616745>")
                    .color(new Color(3, 160, 146))
                    .image("https://i.imgur.com/1PXzrRg.png")
                    .build()).queue();
            case "songoda" -> event.replyEmbeds(new SimpleEmbedBuilder("Songoda")
                    .text("Songoda is a marketplace where we sell our resources",
                            "https://songoda.com/profile/techscode",
                            "",
                            "Buy our resources from our marketplace and verify your purchase here: <#907349490556616745>")
                    .color(new Color(252, 73, 74))
                    .image("https://i.imgur.com/oKeIpv4.png")
                    .build()).queue();
            case "spigot" -> event.replyEmbeds(new SimpleEmbedBuilder("SpigotMC")
                    .text("SpigotMC is a marketplace where we sell our resources",
                            "https://www.spigotmc.org/members/techscode.29620/",
                            "",
                            "Buy our resources from our marketplace and verify your purchase here: <#907349490556616745>")
                    .color(new Color(238, 135, 19))
                    .image("https://i.imgur.com/wlbnhwI.png")
                    .build()).queue();
            case "translations" -> event.replyEmbeds(new SimpleEmbedBuilder("Translations repository")
                    .text("All our translations can be found on our translation repository.",
                            "https://github.com/TechsCode-Team/PluginTranslations",
                            "",
                            "GitHub - TechsCode-Team/PluginTranslations: A repository holding the translations for our plugins.")
                    .color(new Color(67, 178, 255))
                    .image("https://imgur.com/M2bHbUx.png")
                    .build()).queue();
        }
    }
}
