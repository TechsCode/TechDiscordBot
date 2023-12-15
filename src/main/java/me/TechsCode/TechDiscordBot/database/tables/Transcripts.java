package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.TranscriptDatabase;
import me.techscode.techdiscordbot.database.entities.SqlTranscript;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.*;

public class Transcripts {
	public static final String TABLE_NAME = "Transcripts";

	/*public Transcripts() {
		createTable();
	}*/
	/*public void createTable() {
		try {
			TranscriptDatabase.sql.createTableIfNotExists(TABLE_NAME)
					.column("id", VARCHAR)
					.column("value", LONGNVARCHAR)
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to create table " + TABLE_NAME);
		}
	}*/

	public void add(final SqlTranscript transcript) {
		try {
			TranscriptDatabase.sql.insertInto(table(TABLE_NAME))
					.set(field("id"), transcript.getId())
					.set(field("value"), transcript.getMessages())
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to add member to table " + TABLE_NAME);
		}
	}
}
