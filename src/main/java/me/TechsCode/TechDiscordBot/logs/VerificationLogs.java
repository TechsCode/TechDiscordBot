package me.TechsCode.TechDiscordBot.logs;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;

import java.awt.*;
import java.util.Objects;

public class VerificationLogs {

    private static final long CHANNEL_ID = 761382066633572373L;

    public static boolean log(String msg) {
        return sendChannel("Log", msg, null);
    }

    public static boolean log(String title, String msg) {
        return sendChannel(title, msg, null);
    }

    public static boolean log(TechEmbedBuilder embed) {
        return sendChannel(embed);
    }

    public static boolean error(Exception ex) {
        return sendChannel("Error", "```" + ex.getMessage() + "```", new Color(178,34,34));
    }

    public static boolean error(String error) {
        return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
    }

    private static boolean sendChannel(TechEmbedBuilder embed) {
        try {
            embed.queue(Objects.requireNonNull(TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID)));

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean sendChannel(String title, String msg, Color color) {
        try {
            new TechEmbedBuilder(title)
                .text(msg)
                .color(color)
                .queue(Objects.requireNonNull(TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID)));

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
