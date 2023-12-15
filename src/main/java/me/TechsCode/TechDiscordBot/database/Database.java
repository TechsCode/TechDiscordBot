package me.techscode.techdiscordbot.database;

import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.tables.*;
import org.jooq.DSLContext;

// TODO: Check all debug and error messages from all the database files

public class Database {

	public static DSLContext sql = TechDiscordBot.getSqlManager().getDslContext();

	public static MembersTable MEMBERSTable = new MembersTable();
	public static Reminders REMINDERS = new Reminders();
	public static Transcripts TRANSCRIPTS = new Transcripts();
	public static Verifications VERIFICATIONS = new Verifications();
	public static Purchases PURCHASES = new Purchases();
	public static Tickets TICKETS = new Tickets();
	public static ApplicationsTable APPLICATIONSTable = new ApplicationsTable();
	public static PatreonTable PATREONTable = new PatreonTable();

}
