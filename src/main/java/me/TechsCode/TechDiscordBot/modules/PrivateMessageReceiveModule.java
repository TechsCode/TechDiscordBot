package me.techscode.techdiscordbot.modules;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/**
 * A temporary way adding event listeners
 * this will be finished / updated later
 */
public class PrivateMessageReceiveModule extends ListenerAdapter {

    @SubscribeEvent
    public void onPrivateMessageReceive(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getChannelType() != ChannelType.PRIVATE) return;
        if (event.getAuthor().isBot()) return;

        message.addReaction(Emoji.fromUnicode("👀")).queue();
    }
}
