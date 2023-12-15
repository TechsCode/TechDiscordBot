package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlPatreon;
import me.techscode.techdiscordbot.model.enums.Patreon;
import org.jooq.Select;

import java.util.List;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.INTEGER;

public class PatreonTable {
	public static final String TABLE_NAME = "patreon";

	public PatreonTable() {
		createTable();
	}

	public void createTable() {
		try {
			Database.sql.createTableIfNotExists(TABLE_NAME)
					.column("id", BIGINT.identity(true)).primaryKey("id")
					.column("memberId", BIGINT)
					.column("join", BIGINT)
					.column("left", BIGINT.nullable(true))
					.column("tier", INTEGER)
					.constraint(
							foreignKey("memberId")
									.references("members", "id")
					)
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to create table " + TABLE_NAME);
		}
	}

	public boolean add(final SqlPatreon sqlPatreon) {
		try {
			Database.sql.insertInto(table(TABLE_NAME))
					.set(field("memberId"), sqlPatreon.getSqlMember().getId())
					.set(field("join"), sqlPatreon.getJoin())
					.execute();
			return true;
		} catch (final Exception e) {
			Common.throwError(e, "Failed to add sqlPatreon to table " + TABLE_NAME + " for member " + sqlPatreon.getSqlMember().getDiscordMember().getNickname());
			return false;
		}
	}

	public boolean updateTier(final int sqlMemberId, final Patreon patreon) {
		try {
			Database.sql.update(table(TABLE_NAME))
					.set(field("tier"), patreon.getId())
					.where(field("id").eq(sqlMemberId))
					.execute();
			return true;
		} catch (final Exception e) {
			Common.throwError(e, "Failed to update sqlPatreon in table " + TABLE_NAME + " for member " + sqlMemberId);
			return false;
		}
	}

	public boolean setLeft(final SqlPatreon sqlPatreon) {
		try {
			Database.sql.update(table(TABLE_NAME))
					.set(field("left"), sqlPatreon.getLeft())
					.where(field("id").eq(sqlPatreon.getId()))
					.execute();
			return true;
		} catch (final Exception e) {
			Common.throwError(e, "Failed to update sqlPatreon in table " + TABLE_NAME + " for member " + sqlPatreon.getSqlMember().getDiscordMember().getNickname());
			return false;
		}
	}

	public boolean delete(final SqlPatreon sqlPatreon) {
		try {
			Database.sql.deleteFrom(table(TABLE_NAME))
					.where(field("id").eq(sqlPatreon.getId()))
					.execute();
			return true;
		} catch (final Exception e) {
			Common.throwError(e, "Failed to delete sqlPatreon from table " + TABLE_NAME + " for member " + sqlPatreon.getSqlMember().getDiscordMember().getNickname());
			return false;
		}
	}

	public List<SqlPatreon> getById(final int id) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("id").eq(id));
			return select.fetch().into(SqlPatreon.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get verification from table " + TABLE_NAME + " for id " + id);
			return null;
		}
	}

	public List<SqlPatreon> getByDiscordId(final long memberId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("memberId").eq(Database.MEMBERSTable.getFromDiscordId(memberId).get(0).getId()));
			return select.fetch().into(SqlPatreon.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get verification from table " + TABLE_NAME + " for discord id " + memberId);
			return null;
		}
	}

	public List<SqlPatreon> getAll() {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME);
			return select.fetch().into(SqlPatreon.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get all verifications from table " + TABLE_NAME);
			return null;
		}
	}
}
