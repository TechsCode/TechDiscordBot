package me.techscode.techdiscordbot.commands.debug;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlMember;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.List;

public class DatabaseCommand extends SimpleSlashCommand {


	public DatabaseCommand() {
		super("database");
		description("Test the database connection, outputs and more.");

		mainGuildOnly();

		subcommandGroup(new SubcommandGroupData("debug", "Debug some things from the database").addSubcommands(
				new SubcommandData("member", "Check the database connection").addOption(OptionType.MENTIONABLE, "member", "The user to check", true),
				new SubcommandData("verification", "Check the verification of a user").addOption(OptionType.MENTIONABLE, "member", "The user to check", true)
				)
		);

		subCommands(
				new SubcommandData("import", "Import all members from the guild to the database")
		);

	}

	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		if (event.getSubcommandGroup().equals("debug")) {
			switch (event.getSubcommandName()) {
				case "member":
					Member member = event.getOption("member").getAsMember();
					final List<SqlMember> sqlMemberList = Database.MEMBERSTable.getFromDiscordId(member.getIdLong());
					for (final SqlMember sqlMember : sqlMemberList) {
						event.replyEmbeds(new SimpleEmbedBuilder("Database check")
								.text("Success fully retrieved the member " + sqlMember.getDiscordMember().getAsMention() + " from the database.")
								.field("ID: ", String.valueOf(sqlMember.getDiscordId()), true)
								.field("Table ID:", String.valueOf(sqlMember.getId()), true)
								.success().build()
						).setEphemeral(true).queue();
					}
					return;
				case "verification":

			}
		}
		switch (event.getSubcommandName()) {
			case "import":
				event.replyEmbeds(new SimpleEmbedBuilder("Database import")
						.text("Importing all members from the guild to the database.")
						.thumbnail("https://i.imgur.com/jcuD6gb.gif")
						.build()
				).setEphemeral(true).queue();


				final Thread thread = new Thread(() -> {
					event.getGuild().loadMembers().onSuccess(members -> {
						int numInteractions = members.size() +1;
						int i = 0;
						for (final Member member : members) {

							if (Database.MEMBERSTable.getFromDiscordId(member.getIdLong()).isEmpty()) {
								event.getHook().editOriginalEmbeds(new SimpleEmbedBuilder("Database import")
										.text("Importing all members from the guild to the database.")
										.thumbnail("https://i.imgur.com/jcuD6gb.gif")
										.build()
								).queue();

								Database.MEMBERSTable.add(new SqlMember(member.getIdLong()));
								Common.log("Imported " + member.getUser().getAsTag() + " to the database.");
							} else {
								Common.log("Skipped " + member.getUser().getAsTag() + " because it's already in the database.");
							}

							if (i % 10 == 0) {
								event.getHook().editOriginalEmbeds(new SimpleEmbedBuilder("Database import")
										.text("Importing all members from the guild to the database.", "Progress: " + i + "/" + members.size())
										.thumbnail("https://i.imgur.com/jcuD6gb.gif")
										.build()
								).queue();
							}

							i++;
						}
						Common.log("Finished importing all members from the guild to the database.");

						if (i == members.size()) {
							event.getHook().editOriginalEmbeds(new SimpleEmbedBuilder("Database import")
									.text("All members have been imported.")
									.success().build()
							).queue();
						}
					});
				});
				thread.setName("DatabaseCommand-" + getUser().getId());
				thread.start();
		}
	}
}
