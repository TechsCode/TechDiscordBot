package me.techscode.techdiscordbot.commands.common;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

/**
 * TODO: Fix all the commands and finish this file!
 */
public class AnswerCommand extends SimpleSlashCommand {

	/**
	 * Create the timeout command with its specific settings
	 */
	public AnswerCommand() {
		super("answer");
		description("give pre made answers to question");

		mainGuildOnly();

		subCommands(
				new SubcommandData("hex", "Hex & gradient explanation"),
				new SubcommandData("placeholderapi", "PlaceholderAPI explanation"),
				new SubcommandData("test", "Test something out but no idea what!")

		);
	}

	/**
	 * The main code of the timeout command
	 *
	 * @param event SlashCommandInteractionEvent
	 */
	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {

		switch (Objects.requireNonNull(event.getSubcommandName())) {
			case "hex":
				event.replyEmbeds(
						new SimpleEmbedBuilder("How To Use Hex & Gradient")
								.text("In order to use **Hex** and **Gradients** in Ultra and Insane plugins," +
										"you will have to use two different formats.\n" +
										"\n" +
										"__For Hex Colors__ >\n" +
										"```\n" +
										"{#RRGGBB}Some Text```\n" +
										"__For Gradients Colors__ >\n" +
										"```{#RRGGBB>}Some Text{#RRGGBB<}```\n" +
										"To be able to create gradient colors in an easier way and having a preview of them you can use this [**website**](https://rgb.techscode.com/), you have to select as `Type` **TechsCode {#rrggbb>}** then write the text you want to use in `Message`, after modify the colors and finally copy the text where it will be written `Output`.")
								.success().build()
				).queue();
				break;
			case "placeholderapi":
				event.replyEmbeds(
						new SimpleEmbedBuilder("How To Use Placeholders")
								.text("In order to use **Placedholers** in Ultra and Insane plugins," +
										"you will need to install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/).\n" +
										"\n" +
										"A list of all possible placeholders can be found [**HERE**](https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders)\n" +
										"For a list of all our plugin placeholders, you will need to check our plugin wiki pages for each plugin you want to use placeholders from.")
								.success().build()
				).queue();
			case "test":
				event.reply(com.eaio.util.text.HumanTime.approximately("29 m 30m 100 ms")).queue();
				break;
		}
	}
}
