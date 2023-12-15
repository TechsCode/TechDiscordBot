package me.techscode.techdiscordbot.database;

import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.tables.Transcripts;
import org.jooq.DSLContext;

public class TranscriptDatabase {

	public static DSLContext sql = TechDiscordBot.getTranscriptSqlManager().getDslContext();

	public static Transcripts TRANSCRIPT = new Transcripts();
}
