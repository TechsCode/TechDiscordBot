package me.TechsCode.TechDiscordBot.verification;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.spigotmc.data.APIStatus;
import me.TechsCode.TechDiscordBot.spigotmc.data.Purchase;
import me.TechsCode.TechDiscordBot.spigotmc.data.User;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.PurchasesList;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;

import static me.TechsCode.TechDiscordBot.TechDiscordBot.getJDA;

public class VerificationUtil {

	public static boolean isVerified(ButtonClickEvent e, TextChannel channel, TechEmbedBuilder errorMessage) {
		Member member = e.getMember();
		assert member != null;

		Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member.getId());
		if (existingVerification != null) {
			errorMessage.text("You've already linked to your SpigotMC Account and your roles will be updated automatically!").sendTemporary(channel, 10);
			return true;
		}
		return false;
	}

	public static boolean spigotApiUsable(ButtonClickEvent e, TextChannel channel, TechEmbedBuilder errorMessage) {
		if (!TechDiscordBot.getBot().getSpigotStatus().isVerifyUsable()) {
			errorMessage.text("**The Spigot API is currently offline.**\nThe API is being restarted automatically right now. It will be operational again in 10 minutes!").error().sendTemporary(channel, 10);
			TechDiscordBot.getSpigotAPI().restartAPI();
			logger("The API was not fetching and is now being restarted!");
			return false;
		}
		return true;
	}

	public static boolean isVerifyingVerifiedUser(GuildMessageReceivedEvent e, String username, TextChannel channel, TechEmbedBuilder errorMessage) {
		PurchasesList purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().username(username);
		if(purchases.isEmpty()) return false;

		User user = purchases.get(0).getUser();
		Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(user.getUserId());

		if (existingVerification != null) {
			errorMessage.text("The Spigot User " + user.getUsername() + " is already linked with <@" + existingVerification.getDiscordId() + ">. If you believe this is a mistake, please contact a Staff Member.").sendTemporary(channel, 10);

			logger("User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " Has tried to verify as https://www.spigotmc.org/members/" + user.getUsername() + "." + user.getUserId() + " But this user is already verified!");
			return true;
		}
		return false;
	}

	public static boolean hasPurchased(String username, TextChannel channel, TechEmbedBuilder errorMessage){
		Purchase[] purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().username(username).toArray(new Purchase[0]);
		username = purchases[0].getUser().getUsername();

		if (purchases.length == 0) {
			errorMessage.text("Spigot user " + username + " does not own any of Tech's Plugins!\n\n*It may take up to 20 minutes for the bot to recognize new purchases.*\n\n*This could also be an issue with the api. If you believe this is a mistake, please contact a staff member!*");
			errorMessage.error().sendTemporary(channel, 10);
			return true;
		}
		return false;
	}

	public static void logger(String msg){
		new TechEmbedBuilder()
				.text(msg)
				.queue(getJDA().getUserById("619084935655063552"));
		new TechEmbedBuilder()
				.text(msg)
				.queue(getJDA().getUserById("319429800009662468"));
	}

}
