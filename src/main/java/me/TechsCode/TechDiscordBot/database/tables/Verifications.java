package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.verification.SqlVerification;
import me.techscode.techdiscordbot.model.enums.Marketplace;
import org.jooq.Select;

import java.util.List;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.INTEGER;

public class Verifications {
	public static final String TABLE_NAME = "verifications";

	public Verifications() {
		createTable();
	}

	public void createTable() {
		try {
			Database.sql.createTableIfNotExists(TABLE_NAME)
					.column("id", BIGINT.identity(true)).primaryKey("id")
					.column("memberId", BIGINT)
					.column("marketplace", INTEGER)
					.column("marketplaceUserId", INTEGER)
					.constraint(
							foreignKey("memberId")
									.references("members", "id")
					)
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to create table " + TABLE_NAME);
		}
	}

	public boolean add(final SqlVerification verification) {
		try {
			Database.sql.insertInto(table(TABLE_NAME))
					.set(field("memberId"), verification.getSqlMember().getId())
					.set(field("marketplace"), verification.getMarketplace())
					.set(field("marketplaceUserId"), verification.getMarketplaceUserId())
					.execute();
			return true;
		} catch (final Exception e) {
			Common.throwError(e, "Failed to add verification to table " + TABLE_NAME + " for member " + verification.getSqlMember().getDiscordMember().getNickname());
			return false;
		}
	}

	public boolean delete(final SqlVerification verification) {
		try {
			Database.sql.deleteFrom(table(TABLE_NAME))
					.where(field("id").eq(verification.getId()))
					.execute();
			return true;
		} catch (final Exception e) {
			Common.throwError(e, "Failed to delete verification from table " + TABLE_NAME + " for member " + verification.getSqlMember().getDiscordMember().getNickname());
			return false;
		}
	}

	public List<SqlVerification> getById(final int id) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("id").eq(id));
			return select.fetch().into(SqlVerification.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get verification from table " + TABLE_NAME + " for id " + id);
			return null;
		}
	}

	public List<SqlVerification> getByMarketplaceUserId(final Marketplace marketplace, final int marketplaceUserId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("marketplaceUserId").eq(marketplaceUserId)).and(field("marketplace").eq(marketplace.getId()));
			return select.fetch().into(SqlVerification.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get verification from table " + TABLE_NAME + " for marketplace user id " + marketplaceUserId);
			return null;
		}
	}

	public List<SqlVerification> getByDiscordId(final long memberId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("memberId").eq(Database.MEMBERSTable.getFromDiscordId(memberId).get(0).getId()));
			return select.fetch().into(SqlVerification.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get verification from table " + TABLE_NAME + " for discord id " + memberId);
			return null;
		}
	}

	public List<SqlVerification> getAll() {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME);
			return select.fetch().into(SqlVerification.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get all verifications from table " + TABLE_NAME);
			return null;
		}
	}

	public List<SqlVerification> getAllFromMarketplace(Marketplace marketplace) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("marketplace").eq(marketplace.getId()));
			return select.fetch().into(SqlVerification.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get all verifications from table " + TABLE_NAME);
			return null;
		}
	}
}
