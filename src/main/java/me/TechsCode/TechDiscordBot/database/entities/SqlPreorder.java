package me.techscode.techdiscordbot.database.entities;

import java.sql.Timestamp;

public class SqlPreorder {

	private final String email;
	private final long discordId;

	private final String discordName;
	private final String transactionId;
	private final int patreon;

	private final Timestamp lastDownload;

	public SqlPreorder(final String email, final long discordId, final String discordName, final String transactionId, final int patreon, final Timestamp lastDownload) {
		this.email = email;
		this.discordId = discordId;
		this.discordName = discordName;
		this.transactionId = transactionId;
		this.patreon = patreon;
		this.lastDownload = lastDownload;
	}

	public String getEmail() {
		return this.email;
	}

	public long getDiscordId() {
		return this.discordId;
	}

	public String getDiscordName() {
		return this.discordName;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public int getPatreon() {
		return this.patreon;
	}

	public Timestamp getLastDownload() {
		return this.lastDownload;
	}

}
