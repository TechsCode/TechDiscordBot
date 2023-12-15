package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlApplication;
import me.techscode.techdiscordbot.model.enums.Application;
import org.jooq.Query;
import org.jooq.Select;
import org.jooq.impl.DSL;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.VARCHAR;

public class ApplicationsTable {

	public static final String TABLE_NAME = "applications";

	public ApplicationsTable() {
		createTable();
	}

	public void createTable() {
		try {
			Database.sql.createTableIfNotExists(TABLE_NAME)
					.column("id", BIGINT.identity(true)).primaryKey("id")
					.column("memberId", BIGINT)
					.column("channelId", BIGINT)
					.column("time", BIGINT)
					.column("position", VARCHAR.nullable(true))
					.constraint(
							DSL.foreignKey("memberId").references(MembersTable.TABLE_NAME, "id")
					)
					.execute();
			Debugger.debug("Database", "Created table " + TABLE_NAME);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to create table " + TABLE_NAME);
		}
	}

	public void add(final SqlApplication application) {
		try {
			Database.sql.insertInto(table(TABLE_NAME))
					.set(field("memberId"), Database.MEMBERSTable.getFromDiscordId(application.getMember().get(0).getDiscordId()).get(0).getId())
					.set(field("channelId"), application.getChannelId())
					.set(field("time"), application.getTime())
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to add a new application to the database",
					"Member ID: " + application.getMember().get(0).getDiscordId(),
					"Channel ID: " + application.getChannelId(),
					"Time: " + application.getTime()
			);
		}
	}

	public void setPosition(final long channelId, final Application.Position position) {
		try {
			Database.sql.update(table(TABLE_NAME))
					.set(field("position"), position.getId())
					.where(field("channelId").eq(channelId))
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to update application category in table " + TABLE_NAME);
		}
	}

	public List<SqlApplication> get(final int id) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("id").eq(id));
			return select.fetch().into(SqlApplication.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get application from table " + TABLE_NAME);
			return null;
		}
	}

	public List<SqlApplication> get(final long channelId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("channelId").eq(channelId));
			return select.fetch().into(SqlApplication.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get application from table " + TABLE_NAME);
			return null;
		}
	}

	public List<Object> getMember(final int memberId) {
		try {
			final Query query = Database.sql.select(field(TABLE_NAME + ".*"), field("members.*"))
					.from(table("applications")
							.join(table("members"))
							.on(field("applications.memberId").eq(field("members.id")))
							.where(field("applications.id").eq(memberId))
					);
			return query.getBindValues();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get application from table " + TABLE_NAME);
			return null;
		}
	}

	public void remove(final long channelId) {
		try {
			Database.sql.deleteFrom(table(TABLE_NAME)).where(field("channelId").eq(channelId)).execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to remove application from table " + TABLE_NAME);
		}
	}
}
