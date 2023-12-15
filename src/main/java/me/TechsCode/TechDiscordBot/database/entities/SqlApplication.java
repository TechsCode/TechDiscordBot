package me.techscode.techdiscordbot.database.entities;

import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.model.enums.Application;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class SqlApplication {

	private int id;
	private final int memberId;
	private final long channelId;

	private final long time;
	private final String category;

	public SqlApplication(final int memberId, final long channelId, final long time, final String category) {
		this.memberId = memberId;
		this.channelId = channelId;
		this.time = time;
		this.category = category;
	}

	public SqlApplication(final int id, final int memberId, final long channelId, final long time, final String category) {
		this.id = id;
		this.memberId = memberId;
		this.channelId = channelId;
		this.time = time;
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public List<SqlMember> getMember() {
		return Database.MEMBERSTable.getFromId(memberId);
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

	public Application.Position getCategory() {
		return Application.Position.getById(category);
	}

	public void save() {
		Database.APPLICATIONSTable.add(this);
	}

	public void delete() {
		Database.APPLICATIONSTable.remove(this.getChannelId());
	}
}
