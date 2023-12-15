package me.techscode.techdiscordbot.commands.console;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.handlers.console.SimpleConsoleCommand;
import me.techscode.techdiscordbot.TechDiscordBot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class SayConsoleCommand extends SimpleConsoleCommand {
    public SayConsoleCommand() {
        super("say");
        description("Says something from the console to an specific channel");
        usage("say <channelId> <message...>");
    }

    @Override
    public void onConsoleCommand(List<String> args) {
        if (args.isEmpty() || args.size() < 2) {
            sendUsage();
            return;
        }

        String channelId = args.get(0);
        String message = String.join(" ", args.subList(1, args.size()));

        TextChannel textChannel = TechDiscordBot.getMainGuild().getTextChannelById(channelId);

        if (textChannel == null) {
            Common.error("Channel not found!");
            sendUsage();
            return;
        }

        textChannel.sendMessage(message).queue();
        Common.log("Message sent to " + textChannel.getName() + ": " + message);

    }
}
