package me.TechsCode.TechDiscordBot.verification;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.spigotmc.data.APIStatus;
import me.TechsCode.TechDiscordBot.spigotmc.data.Purchase;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.reflect.Array;

public class VerificationUtil {

	public static boolean verificationDebug(){
		return true;
	}

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
			errorMessage.text("**The Spigot API is currently offline.**\nThe API will be restarted automatically please try again in a few minutes").error().sendTemporary(channel, 10);
			TechDiscordBot.getSpigotAPI().restartAPI();

			// TODO Add logger message
			return false;
		}
		return true;
	}

	public static boolean isVerifyingVerifiedUser(String username, TextChannel channel, TechEmbedBuilder errorMessage) {
		Purchase[] purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().username(username).toArray(new Purchase[0]);
		username = purchases[0].getUser().getUsername();
		String userId = purchases[0].getUser().getUserId();

		Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(userId);

		if (existingVerification != null) {
			Purchase purchase = TechDiscordBot.getSpigotAPI().getSpigotPurchases().userId(existingVerification.getUserId()).get(0);
			errorMessage.text("The Spigot User " + username + " is already linked with " + purchase.getUser().getUsername() + ". If you believe this is a mistake, please contact a Staff Member.").sendTemporary(channel, 10);

			// TODO Add logger message
			return true;
		}
		return false;
	}

	public static boolean hasPurchased(String username, TextChannel channel, TechEmbedBuilder errorMessage){
		Purchase[] purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().username(username).toArray(new Purchase[0]);
		username = purchases[0].getUser().getUsername();

		if (purchases.length == 0) {
			if(verificationDebug()) TechDiscordBot.log("[DEBUG] isVerifyingVerifiedUser check returned: true");
			errorMessage.text("Spigot user " + username + " does not own any of Tech's Plugins!\n\n*It may take up to 20 minutes for the bot to recognize new purchases.*\n\n*This could also be an issue with the api. If you believe this is a mistake, please contact a staff member!*");
			errorMessage.error().sendTemporary(channel, 10);

			// TODO Add logger message
			return true;
		}
		return false;
	}



}
