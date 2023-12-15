package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GoogleCommand extends SimpleSlashCommand {

	public GoogleCommand() {
		// Set the command
		super("google");
		// Set the description of the command
		description("Google something on the web");
		// Set the options for the command
		options(new OptionData(OptionType.STRING, "search", "Your search phrase", true),
				new OptionData(OptionType.USER, "member", "The member for who the search is", false));
		// Set to only the main guild
		mainGuildOnly();
	}

	@Override
	protected void onCommand(@NotNull final SlashCommandInteractionEvent event) {

		String search = Objects.requireNonNull(event.getOption("search")).getAsString();
		String queryUrl = null;

		try {
			search = search.trim();
			search = URLEncoder
					.encode(search, StandardCharsets.UTF_8);
			queryUrl = "https://www.google.com/search?q=" + search;

		} catch (final Exception exception) {
			Common.throwError(exception, "Something went wrong while searching for " + search);
		}

		if (queryUrl == null) {
			event.replyEmbeds(new SimpleEmbedBuilder("ERROR")
					.text("Something went wrong while searching for " + search)
					.error()
					.build()
			).setEphemeral(true).queue();
		}

		OptionMapping targetOption = event.getOption("member");

		if (targetOption == null) {
			event.replyEmbeds(
					new SimpleEmbedBuilder("Google")
							.text(
									"Hi there,",
									"It seems that you don't know how to use google.",
									"No problem at all, here is an example on how to google.",
									"",
									"Here is a google search for your question: " + queryUrl,
									"",
									"We hope this helps!"
							)
							.build()
			).setEphemeral(true).queue();
		} else {
			event.reply("Success!").setEphemeral(true).queue(message -> {message.deleteOriginal().queue();});
			event.getChannel().sendMessage(targetOption.getAsUser().getAsMention()).addEmbeds(
					new SimpleEmbedBuilder("Google")
							.text(
									"Hi there,",
									"It seems that you don't know how to use google.",
									"No problem at all, here is an example on how to google.",
									"",
									"Here is a google search for your question: " + queryUrl,
									"",
									"We hope this helps!"
							)
							.build()
			).queue();
		}
	}
}
