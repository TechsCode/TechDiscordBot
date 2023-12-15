package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.model.enums.Plugin;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class WikiCommand extends SimpleSlashCommand {

        public WikiCommand() {
            super("wiki");
            description("Get a wiki link for a plugin");
        }

        @Override
        protected void onCommand(SlashCommandInteractionEvent event) {

            Channel channel = event.getChannel();

            SimpleEmbedBuilder embedBuilder = new SimpleEmbedBuilder("📖 | Wiki");


            switch (channel.getId()) {
                // Permissions
                case "330053303050436608" -> event.replyEmbeds(embedBuilder.text("Here is the Ultra Permissions wiki",
                                Plugin.ULTRA_PERMISSIONS.getEmojiRaw() + " https://ultrapermissions.com/wiki")
                        .color(Plugin.ULTRA_PERMISSIONS.getRole().getColor())
                        .build()).queue();

                // Scoreboards
                case "858052621574078474" -> event.replyEmbeds(embedBuilder.text("Here is the Ultra Scoreboards wiki",
                                Plugin.ULTRA_SCOREBOARDS.getEmojiRaw() + " https://ultrascoreboards.com/wiki")
                        .color(Plugin.ULTRA_SCOREBOARDS.getRole().getColor())
                        .build()).queue();

                // Punishments
                case "531251918291599401" -> event.replyEmbeds(embedBuilder.text("Here is the Ultra Punishments wiki",
                                Plugin.ULTRA_PUNISHMENTS.getEmojiRaw() + " https://ultrapunishments.com/wiki")
                        .color(Plugin.ULTRA_PUNISHMENTS.getRole().getColor())
                        .build()).queue();

                // Customizer
                case "380133603683860480" -> event.replyEmbeds(embedBuilder.text("Here is the Ultra Customizer wiki",
                                Plugin.ULTRA_CUSTOMIZER.getEmojiRaw() + " https://ultracustomizer.com/wiki")
                        .color(Plugin.ULTRA_CUSTOMIZER.getRole().getColor())
                        .build()).queue();

                // Economy
                case "737773631198986240" -> event.replyEmbeds(embedBuilder.text("Here is the Ultra Economy wiki",
                                Plugin.ULTRA_ECONOMY.getEmojiRaw() + " https://ultraeconomy.com/wiki")
                        .color(Plugin.ULTRA_ECONOMY.getRole().getColor())
                        .build()).queue();

                // Regions
                case "465975795433734155" -> event.replyEmbeds(embedBuilder.text("Here is the Ultra Regions wiki",
                                Plugin.ULTRA_REGIONS.getEmojiRaw() + " https://ultraregions.com/wiki")
                        .color(Plugin.ULTRA_REGIONS.getRole().getColor())
                        .build()).queue();

                // MOTD
                case "931264562995540038" -> event.replyEmbeds(embedBuilder.text("Here is the Ultra MOTD wiki",
                                Plugin.ULTRA_MOTD.getEmojiRaw() + " https://ultramotd.com/wiki")
                        .color(Plugin.ULTRA_MOTD.getRole().getColor())
                        .build()).queue();

                // Shops
                case "576813543698202624" -> event.replyEmbeds(embedBuilder.text("Here is the Insane Shops wiki",
                                Plugin.INSANE_SHOPS.getEmojiRaw() + " https://insaneshops.com/wiki")
                        .color(Plugin.INSANE_SHOPS.getRole().getColor())
                        .build()).queue();

                // Vaults
                case "1058612057576054864" -> event.replyEmbeds(embedBuilder.text("Here is the Insane Vaults wiki",
                                Plugin.INSANE_VAULTS.getEmojiRaw() + " https://insanevaults.com/wiki")
                        .color(Plugin.INSANE_VAULTS.getRole().getColor())
                        .build()).queue();
                default -> event.replyEmbeds(new SimpleEmbedBuilder("📚 | Wiki")
                        .text("Showing all wiki's")
                        .field(
                                "Wiki's",
                                Plugin.ULTRA_PERMISSIONS.getEmojiRaw() + " https://ultrapermissions.com/wiki\n" +
                                        Plugin.ULTRA_CUSTOMIZER.getEmojiRaw() + " https://ultracustomizer.com/wiki\n" +
                                        Plugin.ULTRA_ECONOMY.getEmojiRaw() + " https://ultraeconomy.com/wiki\n" +
                                        Plugin.ULTRA_REGIONS.getEmojiRaw() + " https://ultraregions.com/wiki\n" +
                                        Plugin.ULTRA_PUNISHMENTS.getEmojiRaw() + " https://ultrapunishments.com/wiki\n" +
                                        Plugin.ULTRA_SCOREBOARDS.getEmojiRaw() + " https://ultrascoreboards.com/wiki\n" +
                                        Plugin.ULTRA_MOTD.getEmojiRaw() + " https://ultramotd.com/wiki\n" +
                                        Plugin.INSANE_VAULTS.getEmojiRaw() + " https://insanevaults.com/wiki\n" +
                                        Plugin.INSANE_SHOPS.getEmojiRaw() + " https://insaneshops.com/wiki\n" +
                                        Plugin.INSANE_ANNOUNCER.getEmojiRaw() + " https://insaneannouncer.com/wiki\n",
                                false
                        )
                        .build()
                ).queue();
            }
        }
}
