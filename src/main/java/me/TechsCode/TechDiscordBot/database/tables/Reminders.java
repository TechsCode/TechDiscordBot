package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlReminder;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.*;

public class Reminders {
	public static final String TABLE_NAME = "reminders";

	public Reminders() {
		createTable();
	}

	public void createTable() {
		try {
			Database.sql.createTableIfNotExists(TABLE_NAME)
					.column("id", BIGINT.identity(true)).primaryKey("id")
					.column("memberId", BIGINT)
					.column("channelId", BIGINT)
					.column("messageId", BIGINT)
					.column("time", TIMESTAMP)
					.column("type", INTEGER.nullable(true))
					.column("reminder", VARCHAR)
					.constraint(
							DSL.foreignKey("memberId").references(MembersTable.TABLE_NAME, "id")
					)
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to create table " + TABLE_NAME);
		}
	}

	public void add(final SqlReminder reminder) {
		try {
			Database.sql.insertInto(table(TABLE_NAME))
					.set(field("memberId"), Database.MEMBERSTable.getFromDiscordId(reminder.getMember().getIdLong()).get(0).getId())
					.set(field("channelId"), reminder.getChannelId())
					.set(field("messageId"), reminder.getMessageId())
					.set(field("time"), reminder.getTime())
					.set(field("type"), reminder.getType())
					.set(field("reminder"), reminder.getReminder())
					.execute();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to add member to table " + TABLE_NAME);
		}
	}

	public List<Object> getMember(final int memberId) {
		try {
			final Query query = Database.sql.select(field(TABLE_NAME + ".*"), field("members.*"))
					.from(table("reminders")
							.join(table("members"))
							.on(field("reminders.memberId").eq(field("members.id")))
							.where(field("reminders.id").eq(memberId))
					);
			return query.getBindValues();
		} catch (final Exception e) {
			Common.throwError(e, "Failed to get member from table " + TABLE_NAME);
			return null;
		}
	}
}
