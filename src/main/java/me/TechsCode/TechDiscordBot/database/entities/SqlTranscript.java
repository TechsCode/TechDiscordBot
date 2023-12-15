package me.techscode.techdiscordbot.database.entities;

import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.TranscriptDatabase;

public class SqlTranscript {
	private String id;
	private final String value;

	public SqlTranscript(final String id, final String value) {
		this.id = id;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public String getMessages() {
		return value;
	}

	public void save() {
		TranscriptDatabase.TRANSCRIPT.add(this);
	}

}

