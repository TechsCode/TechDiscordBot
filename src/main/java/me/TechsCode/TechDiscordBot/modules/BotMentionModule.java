package me.techscode.techdiscordbot.modules;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/**
 * A module that listens for mentions of the bot.
 */
public class BotMentionModule extends ListenerAdapter {

    /**
     * Listens for mentions of the bot.
     * @param event message received event
     */
    @SubscribeEvent
    public void onBotMention(MessageReceivedEvent event) {

        // Check if the message contains a mention of the bot
        if (event.getMessage().getContentRaw().startsWith("<@" + event.getJDA().getSelfUser().getId() + ">")) {

            // Send a hello message back to the user
            event.getChannel().sendMessage("Hello " + event.getMember().getAsMention() + ", I'm TechDiscordBot!").queue();

            // Add an emoji to the message
            event.getMessage().addReaction(Emoji.fromUnicode("👀")).queue();
        }
    }
}
