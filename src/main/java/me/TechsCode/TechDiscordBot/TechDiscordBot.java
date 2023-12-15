package me.techscode.techdiscordbot;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import me.techscode.techdiscordbot.actions.buttons.ApplyButton;
import me.techscode.techdiscordbot.actions.buttons.TicketButtons;
import me.techscode.techdiscordbot.actions.buttons.verification.*;
import me.techscode.techdiscordbot.commands.applications.ApplicationCommand;
import me.techscode.techdiscordbot.commands.common.*;
import me.techscode.techdiscordbot.commands.console.SayConsoleCommand;
import me.techscode.techdiscordbot.commands.debug.DatabaseCommand;
import me.techscode.techdiscordbot.commands.staff.*;
import me.techscode.techdiscordbot.commands.staff.punishment.BanCommand;
import me.techscode.techdiscordbot.commands.staff.punishment.KickCommand;
import me.techscode.techdiscordbot.commands.staff.punishment.TimeoutCommand;
import me.techscode.techdiscordbot.commands.staff.punishment.WarnCommand;
import me.techscode.techdiscordbot.commands.tickets.TicketCommand;
import me.techscode.techdiscordbot.commands.tickets.TicketStaffCommand;
import me.techscode.techdiscordbot.commands.verification.CodeCommand;
import me.techscode.techdiscordbot.database.TranscriptSqlManager;
import me.techscode.techdiscordbot.model.reminders.ReminderManager;
import me.techscode.techdiscordbot.modules.*;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TechDiscordBot extends SimpleBot {

	public static TranscriptSqlManager transcriptSqlManager;

	private static ReminderManager remindersManager;

	/**
	 * A very ugly way of starting the bot but there is no other way
	 * of doing this. This might get fixed in the feature
	 *
	 * @param args Arguments passed to the bot
	 */
	public static void main(final String[] args) {
		// The method that starts the whole bot
		new SimpleBot() {

			@Override
			public void onPreLoad() {
			}

			@Override
			protected void onBotLoad() {
				Common.log(Common.consoleLine(),
						ConsoleColor.CYAN + "   Starting the bot " + SimpleBot.getName() + " V" + getVersion(),
						Common.consoleLine()
				);

				transcriptSqlManager = new TranscriptSqlManager();

				//Database.MEMBERS.createTable();
			}

			@Override
			protected void onBotStart() {
			}

			@Override
			protected void onReloadableStart() {

				/*
				 * Register all commands here
				 */
				registerCommands(
						new UserInfoCommand(),
						new CodeCommand(),
						new GoogleCommand(),
						new AnswerCommand(),
						new PluginCommand(),
						new RemindCommand(),
						new ApplicationCommand(),
						new TicketCommand(),
						new TicketStaffCommand(),
						new LinkCommand(),
						new QuestionCommand(),
						new RoleCommand(),
						new WikiCommand(),
						new UpdateCommand()
				);

				registerCommands(
						new BanCommand(),
						new KickCommand(),
						new TimeoutCommand(),
						new WarnCommand(),
						new BotCommands()
				);

				// Register all debug commands
				registerCommands(
						new DatabaseCommand()
				);

				/*
				 * Add all events here
				 */
				getJDA().addEventListener(new PrivateMessageReceiveModule());
				getJDA().addEventListener(new BotMentionModule());
				getJDA().addEventListener(new MemberJoinModule(), new MemberLeaveModule());
				getJDA().addEventListener(new RolesModule());
				getJDA().addEventListener(new MessageReceive());

				/*
				 * Add all console command here
				 */
				registerConsoleCommand(new SayConsoleCommand());

				/*
				 * All other methods down here
				 */
				// Send the verification embed
				/*VerificationModule.embed();
				TicketModule.embed();
				ApplyModule.embed();*/


				new TicketButtons.TicketButton().build();
				new ApplyButton.Button().build();
				new VerifyButton.Button().build();

				remindersManager = new ReminderManager();
				Common.log("Loading reminders..");
				try {
					remindersManager.load();
				} catch (final SQLException e) {
					throw new RuntimeException(e);
				}

				jda.addEventListener(remindersManager);

				/*
				 * A new thread to check for preorders
				 * Ugly, but it works
				 */
				/*if (Settings.Modules.Preorder.enabled) {
					Common.log("Preorder system enabled",
							"Checking for preorders every 15 minutes..");
					// Check the preorders on startup
					PreorderModule.CheckPreorders();

					new Thread(() -> {
						while (true) {
							try {
								Thread.sleep(TimeUnit.MINUTES.toMillis(15));
								Debugger.debug("Checking for preorders..");
								PreorderModule.CheckPreorders();
							} catch (final InterruptedException e) {
								Common.throwError(e, "Error while sleeping the preorder thread");
							}
						}
					}).start();
				}*/

				// Run this method every hour
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

				executor.scheduleWithFixedDelay(new Runnable() {
					@Override
					public void run() {
						Common.log("Checking all verification channels..");
						VerificationModule.pinger(getMainGuild());
					}
				}, 5, 60, TimeUnit.MINUTES);

			}
		};
	}

	@Override
	protected void onBotStart() {
	}

	@Override
	public String[] getStartupLogo() {
		return new String[]{
				"  ______          __    ____  _                          ______        __ ",
				" /_  __/__  _____/ /_  / __ \\(_)_____________  _________/ / __ )____  / /_",
				"  / / / _ \\/ ___/ __ \\/ / / / / ___/ ___/ __ \\/ ___/ __  / __  / __ \\/ __/",
				" / / /  __/ /__/ / / / /_/ / (__  ) /__/ /_/ / /  / /_/ / /_/ / /_/ / /_  ",
				"/_/  \\___/\\___/_/ /_/_____/_/____/\\___/\\____/_/   \\__,_/_____/\\____/\\__/  "
		};
	}

	@Override
	public int getFoundedYear() {
		return 2022;
	}

	@Override
	public String getVersion() {
		return "0.1.25";
	}

	public static ReminderManager getRemindersManager() {
		return remindersManager;
	}

	public static TranscriptSqlManager getTranscriptSqlManager() {
		return transcriptSqlManager;
	}
}
