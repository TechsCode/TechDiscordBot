package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import me.techscode.techdiscordbot.database.TranscriptDatabase;
import me.techscode.techdiscordbot.database.entities.SqlPreorder;
import org.jooq.Select;

import java.util.List;

import static org.jooq.impl.DSL.field;

public class Preorders {

	public static final String TABLE_NAME = "Preorders";

	public List<SqlPreorder> get(final long discordId) {
		try {
			final Select<?> select = TranscriptDatabase.sql.select().from(TABLE_NAME).where(field("discordId").eq(discordId));
			return select.fetch().into(SqlPreorder.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get preorder from table " + TABLE_NAME);
			return null;
		}
	}

	public List<SqlPreorder> getAll() {
		try {
			final Select<?> select = TranscriptDatabase.sql.select().from(TABLE_NAME);
			return select.fetch().into(SqlPreorder.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get all preorders from table " + TABLE_NAME);
			return null;
		}
	}
}
