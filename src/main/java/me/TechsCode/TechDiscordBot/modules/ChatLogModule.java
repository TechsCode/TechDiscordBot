package me.techscode.techdiscordbot.modules;

import me.techscode.techdiscordbot.model.Logs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

/*
 * MessageId | ChannelId | UserId | BLOB | LogDate
 */


public class ChatLogModule extends ListenerAdapter {

	// A HashMap with all messages send when the bot is started
	private final HashMap<String, Message> cachedMessages = new HashMap<>();

	/**
	 * Listens for messages that are deleted
	 *
	 * @param event MessageDeleteEvent
	 */
	@Override
	public void onMessageDelete(final MessageDeleteEvent event) {
		final String messageId = event.getMessageId();
		final Message message = event.getChannel().retrieveMessageById(messageId).complete();

		Logs.ChatLogs.log(message);
	}

	/**
	 * Listens for messages that are edited
	 *
	 * @param event MessageUpdateEvent
	 */
	@Override
	public void onMessageUpdate(final MessageUpdateEvent event) {
		final String messageId = event.getMessageId();
		final Message message = event.getChannel().retrieveMessageById(messageId).complete();
		final Message message2 = event.getMessage();

		Logs.ChatLogs.log(message);
	}
}
