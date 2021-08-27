package me.TechsCode.TechDiscordBot.verification;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.VerificationLogs;
import me.TechsCode.TechDiscordBot.spigotmc.data.ProfileComment;
import me.TechsCode.TechDiscordBot.spigotmc.data.Purchase;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.ProfileCommentList;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.TechsCode.TechDiscordBot.TechDiscordBot.getJDA;
import static me.TechsCode.TechDiscordBot.TechDiscordBot.getSpigotAPI;

public class Spigot {

	private static List<String> verificationQueue;
	private boolean isDone = false;

	public static boolean verify(GuildMessageReceivedEvent e){
		if(e.getMember() == null)return false;
		verificationQueue = new ArrayList<>();

		TextChannel channel = e.getChannel();

		String username = e.getMessage().getContentDisplay();
		TechEmbedBuilder errorMessage = new TechEmbedBuilder("Error (" + e.getAuthor().getName() + ")").error();

		if (verificationQueue.contains(e.getAuthor().getId())) {
			errorMessage.text("Please follow the instruction above!").sendTemporary(channel, 15);
			return false;
		}

		if (username.contains(" ")) {
			errorMessage.text("Please type in your SpigotMC Username!").sendTemporary(channel, 5);
			return false;
		}

		Purchase[] purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().username(username).toArray(new Purchase[0]);

		username = purchases[0].getUser().getUsername();
		String userId = purchases[0].getUser().getUserId();
		String avatarUrl = purchases[0].getUser().getAvatar();

		String code = UUID.randomUUID().toString().split("-")[0];

		TechEmbedBuilder verifyInstructions = new TechEmbedBuilder("Verify " + e.getAuthor().getName())
				.thumbnail(avatarUrl)
				.text("Now go to your SpigotMC Profile and post `TechVerification." + code + "`\n\nLink to your Profile:\nhttps://www.spigotmc.org/members/" + username.toLowerCase() + "." + userId + "\n\n**The bot will check your spigot profile after 3 minutes!**");

		Message m = e.getMessage().getChannel().sendMessage(verifyInstructions.build()).complete();
		verificationQueue.add(e.getAuthor().getId());
		String finalUsername = username;

		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(3));
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		ProfileCommentList comments = getSpigotAPI().getSpigotProfileComments(finalUsername+"."+userId, false); //showAll = false only gets the messages from the user
		if(comments.isEmpty()){
			String msg = "The bot did not find any messages posted by you on your profile!";
			alertMsg(msg);
			m.editMessageEmbeds(errorMessage.text("No messages found on that account.").build()).complete().delete().completeAfter(10L, TimeUnit.SECONDS);

			return false;
		}

		for (ProfileComment user : comments) {
			if (user.getText().equals("TechVerification." + code)) {
				m.delete().complete();
				TechDiscordBot.getStorage().createVerification(userId, e.getAuthor().getId());
				verificationQueue.remove(e.getAuthor().getId());

				new TechEmbedBuilder("Verification Complete!")
						.text("You've been successfully verified!\n\nHere are your purchased plugins: " + Plugin.getMembersPluginsinEmojis(e.getMember()) + "\n\n*Your roles will be updated automatically from now on!*")
						.thumbnail(avatarUrl)
						.queue(Objects.requireNonNull(e.getMember()));

				new TechEmbedBuilder()
						.text("You may now delete the message on your profile! [Go to Comment](https://www.spigotmc.org/profile-posts/" + user.getCommentId() + ")")
						.queue(e.getMember());

				String msg = "<@"+e.getMember().getId()+"> verified as https://www.spigotmc.org/members/" + finalUsername.toLowerCase() + "." + userId;
				alertMsg(msg);
				VerificationLogs.log(
						new TechEmbedBuilder(e.getAuthor().getName() + "'s Verification Completed")
								.success().text(e.getAuthor().getName() + " has successfully verified their SpigotMC Account!")
								.thumbnail(avatarUrl)
				);

				return true;
			}
		}

		m.editMessageEmbeds(errorMessage.text("The bot did not find any messages containing `TechVerification." + code + "` on your profile!").build()).complete().delete().completeAfter(20, TimeUnit.SECONDS);

		return false;
	}

	public static TechEmbedBuilder sendInstructions() {
		TechEmbedBuilder embed = new TechEmbedBuilder("Spigot Verification")
				.text("To verify your Spigot purchase we need your spigot username.\n\n**Type in your spigot username below and press enter.**")
				.thumbnail("https://i.ibb.co/tZ0gGZp/728421352721088542a.png");
		return embed;
	}

	private static void alertMsg(String msg) {
		new TechEmbedBuilder()
				.text(msg)
				.queue(getJDA().getUserById("619084935655063552"));
		new TechEmbedBuilder()
				.text(msg)
				.queue(getJDA().getUserById("319429800009662468"));
	}
}
