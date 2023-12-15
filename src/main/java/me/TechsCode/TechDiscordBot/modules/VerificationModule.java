package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.actions.buttons.verification.*;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.verification.SqlVerification;
import me.techscode.techdiscordbot.model.enums.Marketplace;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main verification moduel
 */
public class VerificationModule {

	// ----------------------------------------------------------------------------------------
	// Static
	// ----------------------------------------------------------------------------------------

	/**
	 * The verification channel ID
	 */
	private static final String VERIFICATION_CHANNEL_ID = Settings.Modules.Verification.channel;

	// ----------------------------------------------------------------------------------------
	// Methods
	// ----------------------------------------------------------------------------------------

	/**
	 * The main verification embed that has the market buttons under-need it
	 */
	public static void embed() {

		Debugger.debug("Verification", "Sending verification embed in channel " + VERIFICATION_CHANNEL_ID);
		// The Verification text channel
		final TextChannel channel = Objects.requireNonNull(TechDiscordBot.getJDA().getGuildById(SimpleSettings.Bot.MainGuild())).getTextChannelById(VERIFICATION_CHANNEL_ID);

		// Create the verification embed
		final SimpleEmbedBuilder embed = new SimpleEmbedBuilder("Verification | How it works")
				.text(
						"Welcome to the verification channel. Press the button below to verify your purchase. If you have any questions, please ask around in <#311178000026566658>"
						/*"Welcome to the verification channel. If you have purchased any product of TechsCode you can verify your purchase here. Simply click on the button of the marketplace you purchased from and follow the instructions. If you have any questions, please ask around in <#311178000026566658>",
						"",
						"Select your marketplace down below to start the verification process",
						"",
						"**Note:** *Do not use Polymart verification. This is enabled only for me <@619084935655063552> to test it out!*",
						"",
						"**Questions, Bug reports, Suggestions/Feedback needs to be send directly to <@619084935655063552>**"*/
				)
				.color(new Color(81, 153, 226));

		// Debug message to see what the verification channel is
		assert channel != null;
		Debugger.debug("Verification", "channel name == " + channel.getName() + " channel id == " + channel.getId());

		// Send the embed with the buttons to the verification channel
		channel.sendMessageEmbeds(embed.build())
				.setActionRow(
						new VerifyButton.Button().build()
						/*new SpigotButton.Button().build(),
						new BuiltByBitButton.Button().build(),
						new SongodaButton.Button().build(),
						new PolymartButton.Button().build()*/
				).queue();
	}

	public static void pinger(Guild guild) {
		// A system that checks every hour if a channel is still there.
		// This is for all channels in category 1168185109543931964
		// If the channel is there after 24 hours of the creationg time of that channel
		// It will ping Leadership role
		// If there is no channel or the channel is jonger than 24 hours it will do nothing

		// Get the category
		final Category category = guild.getCategoryById("1168185109543931964");

		// Get all the channels in the category
		List<TextChannel> channels = category.getTextChannels();

		// Loop through all the channels
		for (TextChannel channel : channels) {
			// Get the creation time of the channel
			long creationTime = channel.getTimeCreated().toEpochSecond();

			// Get the current time
			long currentTime = System.currentTimeMillis() / 1000L;

			// Get the difference between the creation time and the current time
			long difference = currentTime - creationTime;

			// If the difference is bigger than 24 hours
			if (difference > 86400) {
				// Check if leadership has been pinged
				if (channel.getTopic() == null || !channel.getTopic().contains("Leadership has been pinged")) {
					// If not, add it to the topic
					channel.getManager().setTopic(channel.getTopic() + " | Leadership has been pinged").queue();
					// Ping the leadership role
					channel.sendMessage("<@&" + Settings.Roles.leadership + ">").queue();
				} else {
					// If so, do nothing
					return;
				}
			}
		}
	}
}
