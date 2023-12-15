package me.techscode.techdiscordbot.database.entities;

import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.Database;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SqlTicket {

	private int id;
	private final long memberId;
	private final long channelId;

	private final long time;
	private final String category;
	private final String type;
	private final String priority;

	public SqlTicket(final long memberId, final long channelId, final long time, final String category, final String type, final String priority) {
		this.memberId = memberId;
		this.channelId = channelId;
		this.time = time;
		this.category = category;
		this.type = type;
		this.priority = priority;
	}

	public SqlTicket(final int id, final long memberId, final long channelId, final long time, final String category, final String type, final String priority) {
		this.id = id;
		this.memberId = memberId;
		this.channelId = channelId;
		this.time = time;
		this.category = category;
		this.type = type;
		this.priority = priority;
	}

	public int getId() {
		return id;
	}

	public long getMemberId() {
		return memberId;
	}

	public SqlMember getMember() {
		return Database.MEMBERSTable.getFromId(Integer.parseInt(memberId + "")).get(0);
	}

	public long getChannelId() {
		return channelId;
	}

	public TextChannel getChannel() {
		return TechDiscordBot.getMainGuild().getTextChannelById(channelId);
	}

	public Long getTime() {
		return time;
	}

	public String getCategory() {
		return category;
	}

	public String getType() {
		return type;
	}

	public String getPriority() {
		return priority;
	}

	public void save() {
		Database.TICKETS.add(this);
	}

	public void delete() {
		Database.TICKETS.remove(this.getChannelId());
	}
}
