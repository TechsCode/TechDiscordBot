package me.TechsCode.TechDiscordBot.logs;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.*;

public class ServerLogs {

    private static final long CHANNEL_ID = 761382066633572373L;

    public static boolean log(String msg) {
        return sendChannel("Log", msg, null);
    }

    public static boolean error(Exception ex) {
        return sendChannel("Error", "```" + ExceptionUtils.getMessage(ex) + "```", new Color(178,34,34));
    }

    public static boolean error(String error) {
        return sendChannel("Error", "```" + error + "```", new Color(178,34,34));
    }

    private static boolean sendChannel(String title, String msg, Color color) {
        try {
            new TechEmbedBuilder(title)
                .setText(msg)
                .setColor(color)
                .send(TechDiscordBot.getJDA().getTextChannelById(CHANNEL_ID));

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
