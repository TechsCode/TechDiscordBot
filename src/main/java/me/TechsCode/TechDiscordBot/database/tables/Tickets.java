package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlTicket;
import me.techscode.techdiscordbot.model.enums.Ticket;
import org.jooq.Query;
import org.jooq.Select;
import org.jooq.impl.DSL;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.*;

public class Tickets {

	public static final String TABLE_NAME = "tickets";

	public Tickets() {
		createTable();
	}

	public void createTable() {
		try {
			Database.sql.createTableIfNotExists(TABLE_NAME)
					.column("id", BIGINT.identity(true)).primaryKey("id")
					.column("memberId", BIGINT)
					.column("channelId", BIGINT)
					.column("time", BIGINT)
					.column("category", VARCHAR.nullable(true))
					.column("type", VARCHAR.nullable(true))
					.column("priority", VARCHAR.nullable(true))
					.constraint(
							DSL.foreignKey("memberId").references(MembersTable.TABLE_NAME, "id")
					)
					.execute();
			Debugger.debug("Database", "Created table " + TABLE_NAME);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to create table " + TABLE_NAME);
		}
	}

	public void add(final SqlTicket ticket) {
		try {
			Database.sql.insertInto(table(TABLE_NAME))
					.set(field("memberId"), Database.MEMBERSTable.getFromDiscordId(ticket.getMemberId()).get(0).getId())
					.set(field("channelId"), ticket.getChannelId())
					.set(field("time"), ticket.getTime())
					.set(field("category"), ticket.getCategory())
					.set(field("type"), ticket.getType())
					.set(field("priority"), ticket.getPriority())
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to add a new ticket to the database",
					"Member ID: " + ticket.getMemberId(),
					"Channel ID: " + ticket.getChannelId(),
					"Time: " + ticket.getTime()
			);
		}
	}

	public void setCategory(final long channelId, final Ticket.Category category) {
		try {
			Database.sql.update(table(TABLE_NAME))
					.set(field("category"), category.getId())
					.where(field("channelId").eq(channelId))
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to update ticket category in table " + TABLE_NAME);
		}
	}

	public void setType(final long channelId, final String type) {
		try {
			Database.sql.update(table(TABLE_NAME))
					.set(field("type"), type)
					.where(field("channelId").eq(channelId))
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to update ticket in table " + TABLE_NAME);
		}
	}

	public void setPriority(final long channelId, final String priority) {
		try {
			Database.sql.update(table(TABLE_NAME))
					.set(field("priority"), priority)
					.where(field("channelId").eq(channelId))
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to update ticket in table " + TABLE_NAME);
		}
	}

	public List<SqlTicket> get(final int id) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("id").eq(id));
			return select.fetch().into(SqlTicket.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get ticket from table " + TABLE_NAME);
			return null;
		}
	}

	public List<SqlTicket> get(final long channelId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("channelId").eq(channelId));
			return select.fetch().into(SqlTicket.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get ticket from table " + TABLE_NAME);
			return null;
		}
	}

	public List<SqlTicket> getFromMembedId(final long memberId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("memberId").eq(memberId));
			return select.fetch().into(SqlTicket.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get ticket from table " + TABLE_NAME);
			return null;
		}
	}

	public List<SqlTicket> getFromMembedId(final String memberId) {
		try {
			final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("memberId").eq(memberId));
			return select.fetch().into(SqlTicket.class);
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get ticket from table " + TABLE_NAME);
			return null;
		}
	}


	public List<Object> getMember(final int memberId) {
		try {
			final Query query = Database.sql.select(field(TABLE_NAME + ".*"), field("members.*"))
					.from(table("tickets")
							.join(table("members"))
							.on(field("tickets.memberId").eq(field("members.id")))
							.where(field("tickets.id").eq(memberId))
					);
			return query.getBindValues();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get ticket from table " + TABLE_NAME);
			return null;
		}
	}

	public void remove(final long channelId) {
		try {
			Database.sql.deleteFrom(table(TABLE_NAME)).where(field("channelId").eq(channelId)).execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to remove ticket from table " + TABLE_NAME);
		}
	}
}
