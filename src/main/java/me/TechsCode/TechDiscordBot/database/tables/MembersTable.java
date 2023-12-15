package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlMember;
import org.jooq.Select;

import javax.annotation.Nullable;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.*;

public class MembersTable {

	public static final String TABLE_NAME = "members";

	public MembersTable() {
		createTable();
	}

	public void createTable() {
		try {
			Database.sql.createTableIfNotExists(TABLE_NAME)
					.column("id", BIGINT.identity(true)).primaryKey("id")
					.column("discordId", BIGINT)
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to create table " + TABLE_NAME);
		}
	}

	public void add(final SqlMember member) {
		try {
			Database.sql.insertInto(table(TABLE_NAME))
					.set(field("discordId"), member.getDiscordId())
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to add member to table " + TABLE_NAME);
		}
	}

	@Nullable
	public List<SqlMember> getFromId(final int id) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("id").eq(id));

			if (select.fetch().isEmpty()) return null;
			return select.fetch().into(SqlMember.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get member from table " + TABLE_NAME);
			return null;
		}
	}

	@Nullable
	public List<SqlMember> getFromDiscordId(final long discordId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("discordId").eq(discordId));
			if (select.fetch().isEmpty()) return null;
			return select.fetch().into(SqlMember.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get member from table " + TABLE_NAME);
			return null;
		}
	}

	@Nullable
	public List<SqlMember> getFromDiscordId(final String discordId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("discordId").eq(discordId));
			if (select.fetch().isEmpty()) return null;
			return select.fetch().into(SqlMember.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get member from table " + TABLE_NAME);
			return null;
		}
	}
}
