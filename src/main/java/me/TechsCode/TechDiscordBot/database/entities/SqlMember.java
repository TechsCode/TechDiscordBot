package me.techscode.techdiscordbot.database.entities;

import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import me.techscode.techdiscordbot.database.Database;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class SqlMember {

	private int id;
	private final long discordId;

	public SqlMember(final long discordId) {
		this.discordId = discordId;
	}

	public SqlMember(final int id, final long discordId) {
		this.id = id;
		this.discordId = discordId;
	}

	public int getId() {
		return this.id;
	}

	public long getDiscordId() {
		return this.discordId;
	}

	public Member getDiscordMember() {
		return Objects.requireNonNull(SimpleBot.getJDA().getGuildById(SimpleSettings.Bot.MainGuild())).getMemberById(this.discordId);
	}

	public User getDiscordUser() {
		return SimpleBot.getJDA().getUserById(this.discordId);
	}

	public void save() {
		Database.MEMBERSTable.add(this);
	}

}
