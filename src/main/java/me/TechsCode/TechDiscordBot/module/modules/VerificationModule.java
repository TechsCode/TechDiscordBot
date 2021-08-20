package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.VerificationLogs;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.spigotmc.data.APIStatus;
import me.TechsCode.TechDiscordBot.spigotmc.data.ProfileComment;
import me.TechsCode.TechDiscordBot.spigotmc.data.Purchase;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.ProfileCommentList;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.TechsCode.TechDiscordBot.TechDiscordBot.getJDA;
import static me.TechsCode.TechDiscordBot.TechDiscordBot.getSpigotAPI;

public class VerificationModule extends Module {

    private final DefinedQuery<TextChannel> VERIFICATION_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("\uD83D\uDCD8︱verification");
        }
    };

    private TextChannel channel;
    private Message lastInstructions;

    private List<String> verificationQueue;

    public VerificationModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        channel = VERIFICATION_CHANNEL.query().first();
        channel.getIterableHistory()
                .takeAsync(100)
                .thenAccept(msg -> channel.purgeMessages(msg.stream().filter(m -> m.getEmbeds().size() > 0 && m.getEmbeds().get(0).getAuthor() != null && m.getEmbeds().get(0).getAuthor().getName() != null && m.getEmbeds().get(0).getAuthor().getName().equals("How It Works")).collect(Collectors.toList())));

        lastInstructions = null;
        verificationQueue = new ArrayList<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (lastInstructions != null)
                lastInstructions.delete().complete();
        }));

        sendInstructions();
    }

    @Override
    public void onDisable() {
        if (lastInstructions != null) lastInstructions.delete().submit();
    }

    public void sendInstructions() {
        if (lastInstructions != null)
            lastInstructions.delete().complete();

        TechEmbedBuilder howItWorksMessage = new TechEmbedBuilder("How It Works").text("**SpigotMC**\nType your SpigotMC Username in this Chat to verify.\n\n**MC-Market**\nTo verify your MC-Market purchases, please contact a\n<@&854044253885956136> or <@&608113993038561325> for manual verification.\n\n**Songoda**\nTo verify your Songoda purchases, please link your\nDiscord account to the Songoda website, simple as that!\n\nMake sure your profile is set to public and **not** private.\nVerification is not working? Please contact a staff member in <#311178000026566658>.");
        lastInstructions = howItWorksMessage.complete(channel);
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if (e.getMember() == null) return;
        if (e.getAuthor().isBot()) return;
        if (!e.getChannel().equals(channel)) return;

        String username = e.getMessage().getContentDisplay();
        e.getMessage().delete().complete();

        TechEmbedBuilder errorMessage = new TechEmbedBuilder("Error (" + e.getAuthor().getName() + ")").error();

        if (!TechDiscordBot.getBot().getSpigotStatus().isUsable()) {
            errorMessage.text("**The API is currently offline.**\nThere is no ETA of when it will be back up.\nYou will have to wait to verify until then.").error().sendTemporary(channel, 10);

            String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + "Tried to verify but the the api is down!";
            alertMsg(msg);

            return;
        }

        if (verificationQueue.contains(e.getAuthor().getId())) {
            errorMessage.text("Please follow the instruction above!").sendTemporary(channel, 15);
            return;
        }

        Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(e.getAuthor().getId());
        if (existingVerification != null) {
            errorMessage.text("You've already linked to your SpigotMC Account and your roles will be updated automatically!").sendTemporary(channel, 15);
            return;
        }

        if (username.contains(" ")) {
            errorMessage.text("Please type in your SpigotMC Username!").sendTemporary(channel, 5);
            return;
        }

        Purchase[] purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().username(username).toArray(new Purchase[0]);

        if (purchases.length == 0) {
            errorMessage.text("User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " does not own any of Tech's Plugins!\n\n*It may take up to 20 minutes for the bot to recognize new purchases.*\n\n*This could also be an issue with the api. If you believe this is a mistake, please contact a staff member!*");

            if (TechDiscordBot.getBot().getSpigotStatus() == APIStatus.NOT_FETCHING) {
                errorMessage.text(errorMessage.getText() + "\n\n**The API is currently not fetching new information, this could also be the issue.");

                String msg = "User (" + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + ") tried to verify but the the api is down!";
                alertMsg(msg);

                return;
            }

            errorMessage.error().sendTemporary(channel, 10);
            return;
        }

        username = purchases[0].getUser().getUsername();
        String userId = purchases[0].getUser().getUserId();
        String avatarUrl = purchases[0].getUser().getAvatar();

        existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(userId);

        if (existingVerification != null) {
            Purchase purchase = TechDiscordBot.getSpigotAPI().getSpigotPurchases().userId(existingVerification.getUserId()).get(0);

            String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " Has tried to verify as https://www.spigotmc.org/members/" + username.toLowerCase() + "." + userId + " But this user is already verified!";
            alertMsg(msg);

            errorMessage.text("The SpigotMC User " + username + " is already linked with " + purchase.getUser().getUsername() + ". If you believe this is a mistake, please contact a Staff Member.").sendTemporary(channel, 10);
            return;
        }

        String code = UUID.randomUUID().toString().split("-")[0];

        TechEmbedBuilder instructions = new TechEmbedBuilder("Verify " + e.getAuthor().getName())
                .thumbnail(avatarUrl)
                .text("Now go to your SpigotMC Profile and post `TechVerification." + code + "`\n\nLink to your Profile:\nhttps://www.spigotmc.org/members/" + username.toLowerCase() + "." + userId + "\n\n**Please verify yourself within 3 Minutes!**");

        Message m = e.getMessage().getChannel().sendMessage(instructions.build()).complete();
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

                    sendInstructions();
                    this.verificationQueue.remove(e.getAuthor().getId());

                    if (all.getUserId().equals(finalUsername+"."+userId)) {
                        TechDiscordBot.getStorage().createVerification(userId, e.getAuthor().getId());

                        new TechEmbedBuilder("Verification Complete!")
                                .text("You've been successfully verified!\n\nHere are your purchased plugins: " + Plugin.getMembersPluginsinEmojis(e.getMember()) + "\n\n*Your roles will be updated automatically from now on!*")
                                .thumbnail(avatarUrl)
                                .queue(e.getMember());
                    } else {
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
    }

    private void alertMsg(String msg) {
        new TechEmbedBuilder()
                .text(msg)
                .queue(getJDA().getUserById("619084935655063552"));
        new TechEmbedBuilder()
                .text(msg)
                .queue(getJDA().getUserById("319429800009662468"));
    }

    @Override
    public String getName() {
        return "Verification";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(VERIFICATION_CHANNEL, 1, "Missing Verification Channel (#verification)")
        };
    }
}
