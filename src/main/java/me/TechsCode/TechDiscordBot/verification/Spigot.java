package me.TechsCode.TechDiscordBot.verification;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.VerificationLogs;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.spigotmc.data.APIStatus;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.TechsCode.TechDiscordBot.TechDiscordBot.getJDA;
import static me.TechsCode.TechDiscordBot.TechDiscordBot.getSpigotAPI;

public class Spigot {

	private static List<String> verificationQueue;

	public static boolean verify(GuildMessageReceivedEvent e){
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
				.text("Now go to your SpigotMC Profile and post `TechVerification." + code + "`\n\nLink to your Profile:\nhttps://www.spigotmc.org/members/" + username.toLowerCase() + "." + userId + "\n\n**Please verify yourself within 3 Minutes!**");

		Message m = e.getMessage().getChannel().sendMessage(verifyInstructions.build()).complete();
		verificationQueue.add(e.getAuthor().getId());
		String finalUsername = username;

		new Thread(() -> {
			try {
				Thread.sleep(TimeUnit.MINUTES.toMillis(3));
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			ProfileCommentList comments = getSpigotAPI().getSpigotProfileComments(finalUsername+"."+userId, false);

			for (ProfileComment all : comments) {
				if (all.getText().equals("TechVerification." + code)) {
					if (all.getUserId().equals(finalUsername+"."+userId)) {
						m.delete().complete();

						String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " Has verified as https://www.spigotmc.org/members/" + finalUsername.toLowerCase() + "." + userId;
						alertMsg(msg);

						VerificationLogs.log(
								new TechEmbedBuilder(e.getAuthor().getName() + "'s Verification Completed")
										.success().text(e.getAuthor().getName() + " has successfully verified their SpigotMC Account!")
										.thumbnail(avatarUrl)
						);
					}

					verificationQueue.remove(e.getAuthor().getId());

					if (all.getUserId().equals(finalUsername+"."+userId)) {
						TechDiscordBot.getStorage().createVerification(userId, e.getAuthor().getId());

						new TechEmbedBuilder("Verification Complete!")
								.text("You've been successfully verified!\n\nHere are your purchased plugins: " + Plugin.getMembersPluginsinEmojis(e.getMember()) + "\n\n*Your roles will be updated automatically from now on!*")
								.thumbnail(avatarUrl)
								.queue(e.getMember());
					} else {
						//TODO Change logger of verifying other account
						String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " Has tried to verify as https://www.spigotmc.org/members/" + finalUsername.toLowerCase() + "." + userId;
						alertMsg(msg);

						m.editMessage(errorMessage.text("Please verify your own account.").build()).complete().delete().queueAfter(10L, TimeUnit.SECONDS);
					}

					new TechEmbedBuilder()
							.text("You may now delete the message on your profile! [Go to Comment](https://www.spigotmc.org/profile-posts/" + all.getCommentId() + ")")
							.queue(e.getMember());
					return;
				}
			}

			verificationQueue.remove(e.getAuthor().getId());
			m.editMessage(errorMessage.text("**You took too long!**\n\nThe Verification process has timed out! Please try again.").build())
					.complete()
					.delete()
					.queueAfter(10, TimeUnit.SECONDS);
		}).start();

		return true;
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
