package me.techscode.techdiscordbot.database.entities;

import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.Database;
import net.dv8tion.jda.api.entities.Member;

import java.time.LocalDateTime;

public class SqlReminder {

	private int id;
	private final int memberId;
	private final long channelId;
	private final long messageId;
	private final LocalDateTime time;
	private final int type;
	private final String reminder;

	public SqlReminder(final int memberId, final long channelId, final long messageId, final LocalDateTime time, final int type, final String reminder) {
		this.memberId = memberId;
		this.channelId = channelId;
		this.messageId = messageId;
		this.time = time;
		this.type = type;
		this.reminder = reminder;
	}

	public SqlReminder(final int id, final int memberId, final long channelId, final long messageId, final LocalDateTime time, final int type, final String reminder) {
		this.id = id;
		this.memberId = memberId;
		this.channelId = channelId;
		this.messageId = messageId;
		this.time = time;
		this.type = type;
		this.reminder = reminder;
	}

	public int getId() {
		return id;
	}

	public Member getMember() {
		for (final SqlMember member : Database.MEMBERSTable.getFromId(memberId)) {
			if (TechDiscordBot.getJDA().getTextChannelById(channelId).getHistory().getMessageById(messageId).getAuthor().getIdLong() == member.getDiscordId()) {
				return member.getDiscordMember();
			}
		}
		return null;
	}

	public long getChannelId() {
		return channelId;
	}

	public long getMessageId() {
		return messageId;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public int getType() {
		return type;
	}

	public String getReminder() {
		return reminder;
	}

	public void save() {
		Database.REMINDERS.add(this);
	}

}
