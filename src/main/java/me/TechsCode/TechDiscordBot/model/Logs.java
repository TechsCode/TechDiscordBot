package me.techscode.techdiscordbot.model;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.util.Objects;

public class Logs {

    public static class ChatLogs {

        private static final long CHANNEL_ID = Settings.Modules.Logs.chat;

        public static void log(Message message) {
            MessageCreateData messageCreateData = new MessageCreateBuilder()
                    .setContent(message.getContentRaw())
                    .build();
            TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID).sendMessage(messageCreateData).complete();
        }


        public static boolean log(String msg) {
            return sendChannel("Log", msg, null);
        }

        public static boolean log(String title, String msg) {
            return sendChannel(title, msg, null);
        }

        public static boolean log(SimpleEmbedBuilder embed) {
            return sendChannel(embed);
        }

        public static boolean error(Exception ex) {
            return sendChannel("Error", "```" + ex.getMessage() + "```", new Color(178,34,34));
        }

        public static boolean error(String error) {
            return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
        }

        private static boolean sendChannel(SimpleEmbedBuilder embed) {
            try {
                TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID).sendMessageEmbeds(embed.build()).queue();

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        private static boolean sendChannel(String title, String msg, Color color) {
            try {
                new SimpleEmbedBuilder(title)
                        .text(msg)
                        .color(color)
                        .queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static class ServerLogs {

        private static final long CHANNEL_ID = Settings.Modules.Logs.server;

        public  boolean log(String msg) {
            return sendChannel("Log", msg, null);
        }

        public static boolean log(String title, String msg) {
            return sendChannel(title, msg, null);
        }

        public static boolean log(SimpleEmbedBuilder embed) {
            return sendChannel(embed);
        }

        public static boolean error(Exception ex) {
            return sendChannel("Error", "```" + ex.getMessage() + "```", new Color(178,34,34));
        }

        public static boolean error(String error) {
            return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
        }

        private static boolean sendChannel(SimpleEmbedBuilder embed) {
            try {
                TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID).sendMessageEmbeds(embed.build()).queue();

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        private static boolean sendChannel(String title, String msg, Color color) {
            try {
                new SimpleEmbedBuilder(title)
                        .text(msg)
                        .color(color)
                        .queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static class VerificationLogs {

        private static final long CHANNEL_ID = Settings.Modules.Logs.verification;

        public static boolean log(String msg) {
            return sendChannel("Log", msg, null);
        }

        public static boolean log(String title, String msg) {
            return sendChannel(title, msg, null);
        }

        public static boolean log(SimpleEmbedBuilder embed) {
            return sendChannel(embed);
        }

        public static boolean error(Exception ex) {
            return sendChannel("Error", "```" + ex.getMessage() + "```", new Color(178,34,34));
        }

        public static boolean error(String error) {
            return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
        }

        private static boolean sendChannel(SimpleEmbedBuilder embed) {
            try {
                embed.queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        private static boolean sendChannel(String title, String msg, Color color) {
            try {
                new SimpleEmbedBuilder(title)
                        .text(msg)
                        .color(color)
                        .queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static class PunishLogs {

        private static final long CHANNEL_ID = Settings.Modules.Logs.punish;

        public static boolean log(String msg) {
            return sendChannel("Log", msg, null);
        }

        public static boolean log(String title, String msg) {
            return sendChannel(title, msg, null);
        }

        public static boolean log(SimpleEmbedBuilder embed) {
            return sendChannel(embed);
        }

        public static boolean error(Exception ex) {
            return sendChannel("Error", "```" + ex.getMessage() + "```", new Color(178,34,34));
        }

        public static boolean error(String error) {
            return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
        }

        private static boolean sendChannel(SimpleEmbedBuilder embed) {
            try {
                embed.queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        private static boolean sendChannel(String title, String msg, Color color) {
            try {
                new SimpleEmbedBuilder(title)
                        .text(msg)
                        .color(color)
                        .queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static class TicketLogs {

        private static final long CHANNEL_ID = Settings.Modules.Logs.tickets;

        public static void log(Message message) {
            MessageCreateData messageCreateData = new MessageCreateBuilder()
                    .setContent(message.getContentRaw())
                    .build();
            TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID).sendMessage(messageCreateData).complete();
        }


        public static boolean log(String msg) {
            return sendChannel("Log", msg, null);
        }

        public static boolean log(String title, String msg) {
            return sendChannel(title, msg, null);
        }

        public static boolean log(SimpleEmbedBuilder embed) {
            return sendChannel(embed);
        }

        public static boolean error(Exception ex) {
            return sendChannel("Error", "```" + ex.getMessage() + "```", new Color(178,34,34));
        }

        public static boolean error(String error) {
            return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
        }

        private static boolean sendChannel(SimpleEmbedBuilder embed) {
            try {
                TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID).sendMessageEmbeds(embed.build()).queue();

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        private static boolean sendChannel(String title, String msg, Color color) {
            try {
                new SimpleEmbedBuilder(title)
                        .text(msg)
                        .color(color)
                        .queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    public static class RoleLogs {

        private static final long CHANNEL_ID = Settings.Modules.Logs.roles;

        public static void log(Message message) {
            MessageCreateData messageCreateData = new MessageCreateBuilder()
                    .setContent(message.getContentRaw())
                    .build();
            TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID).sendMessage(messageCreateData).complete();
        }


        public static boolean log(String msg) {
            return sendChannel("Log", msg, null);
        }

        public static boolean log(String title, String msg) {
            return sendChannel(title, msg, null);
        }

        public static boolean log(SimpleEmbedBuilder embed) {
            return sendChannel(embed);
        }

        public static boolean error(Exception ex) {
            return sendChannel("Error", "```" + ex.getMessage() + "```", new Color(178,34,34));
        }

        public static boolean error(String error) {
            return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
        }

        private static boolean sendChannel(SimpleEmbedBuilder embed) {
            try {
                TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID).sendMessageEmbeds(embed.build()).queue();

                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        private static boolean sendChannel(String title, String msg, Color color) {
            try {
                new SimpleEmbedBuilder(title)
                        .text(msg)
                        .color(color)
                        .queue(Objects.requireNonNull(SimpleBot.getJDA().getTextChannelById(CHANNEL_ID)));

                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

}
