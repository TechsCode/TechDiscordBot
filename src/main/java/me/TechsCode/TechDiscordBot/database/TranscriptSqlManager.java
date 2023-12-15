package me.techscode.techdiscordbot.database;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.techscode.techdiscordbot.settings.Settings;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;

public class TranscriptSqlManager {

	private static HikariDataSource dataSource;
	private DSLContext dslContext = null;
	private final String host = SimpleSettings.Database.Host();
	private final String db = "TechDiscordBot";
	private final String url = "jdbc:mysql://" + host + "/" + db;
	private final String userName = Settings.Database.Transcript.username;
	private final String password = Settings.Database.Transcript.password;

	public TranscriptSqlManager() {
		if (!SimpleSettings.Database.Enabled()) {
			Common.log("MySQL system Disabled!");
			return;
		}
		Common.log("Transcript Database system Enabled! Starting up Transcript MYSQL system");

		final HikariConfig config = new HikariConfig();
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(15);
		config.setJdbcUrl(url);
		config.setUsername(userName);
		config.setPassword(password);

		try {
			dataSource = new HikariDataSource(config);
			dslContext = DSL.using(dataSource, SQLDialect.MYSQL);
			Common.log("Transcript MYSQL system Started!");
		} catch (final Exception e) {
			Debugger.printStackTrace(e);
		}

		if (!dataSource.isClosed()) {
			Common.log("Successfully connected to Transcript MySQL database!");
		} else {
			Common.log("Failed to connect to Transcript MySQL database!");
		}
	}

	public void codeGenerator() {
		new org.jooq.meta.jaxb.Configuration()
				.withJdbc(new Jdbc()
						.withDriver("com.mysql.jdbc.Driver")
						.withUrl(url)
						.withUser(userName)
						.withPassword(password)
				)
				.withGenerator(
						new Generator()
								.withDatabase(
										new org.jooq.meta.jaxb.Database()
												.withName(db)
												.withIncludes(".*")
												.withExcludes("" +
														"UNUSED_TABLE                # This table (unqualified name) should not be generated" +
														"| PREFIX_.*                   # Objects with a given prefix should not be generated" +
														"| SECRET_SCHEMA\\.SECRET_TABLE # This table (qualified name) should not be generated" +
														"| SECRET_ROUTINE              # This routine (unqualified name) ..." +
														""
												)
												.withInputSchema("[your database schema / owner / name]")
								)
								.withTarget(
										new Target()
												.withPackageName("com.greazi.discordbotfoundation.mysql")
												.withDirectory("src/main/java/com/greazi/discordbotfoundation/mysql/generated")
								)
				);
	}

	public static HikariDataSource getDataSource() {
		return dataSource;
	}

	public DSLContext getDslContext() {
		return dslContext;
	}

}
