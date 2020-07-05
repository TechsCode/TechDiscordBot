package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.SpigotAPI.client.objects.Purchase;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.spigotmc.ProfileComment;
import me.TechsCode.TechDiscordBot.spigotmc.SpigotMC;
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

public class VerificationModule extends Module {

    private final DefinedQuery<TextChannel> VERIFICATION_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("verification");
        }
    };

    private TextChannel channel;
    private Message lastInstructions, apiNotAvailable;

    private List<String> verificationQueue;

    public VerificationModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        channel = VERIFICATION_CHANNEL.query().first();

        lastInstructions = null;
        //apiNotAvailable = null;
        verificationQueue = new ArrayList<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(lastInstructions != null) lastInstructions.delete().complete();
        }));

        sendInstructions();
    }

    @Override
    public void onDisable() {
        if (lastInstructions != null) lastInstructions.delete().submit();
    }

    public void sendInstructions() {
        if(lastInstructions != null) lastInstructions.delete().complete();
        TechEmbedBuilder howItWorksMessage = new TechEmbedBuilder("How It Works").setText("Type your SpigotMC Username in this Chat to verify.\n\nVerification not working? Feel free to contact a staff member in <#311178000026566658>.");
        lastInstructions = howItWorksMessage.send(channel);
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if (e.getMember() == null) return;
        if (e.getAuthor().isBot()) return;
        if (!e.getChannel().equals(channel)) return;

        e.getMessage().delete().complete();

        TechEmbedBuilder errorMessage = new TechEmbedBuilder("Error (" + e.getAuthor().getName() + ")").error();

        if (!TechDiscordBot.getSpigotAPI().isAvailable()) {
            errorMessage.setText("**The API is currently offline.**\nThere is no ETA of when it will be back up.\nYou will have to wait to verify until then.").error().sendTemporary(channel, 10);
            return;
        }

        if (verificationQueue.contains(e.getAuthor().getId())) {
            errorMessage.setText("Please follow the instruction above!").sendTemporary(channel, 15);
            return;
        }

        Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(e.getAuthor().getId());
        if (existingVerification != null) {
            errorMessage.setText("You're already linked to your SpigotMC Account and your roles will be updated automatically!").sendTemporary(channel, 15);
            return;
        }

        String username = e.getMessage().getContentDisplay();

        if (username.contains(" ")) {
            errorMessage.setText("Please type in your SpigotMC Username!").sendTemporary(channel, 5);
            return;
        }

        Purchase[] purchases = TechDiscordBot.getSpigotAPI().getPurchases().username(username).get();

        if (purchases.length == 0) {
            errorMessage.setText("The user '" + username + "' does not own any of Tech's Plugins!\n\n*It may take up to 20 minutes for the bot to recognize new purchases.*\n\n*This could also be an issue with the api. If you believe this is a mistake, please contact a staff member!*").sendTemporary(channel, 10);
            return;
        }

        username = purchases[0].getUsername();
        String userId = purchases[0].getUserId();
        String avatarUrl = purchases[0].getAvatarUrl();

        existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(userId);
        if(existingVerification != null) {
            Purchase purchase = TechDiscordBot.getSpigotAPI().getPurchases().userId(existingVerification.getUserId()).first();
            errorMessage.setText("The SpigotMC User " + username + " is already linked with " + purchase.getUsername() + ". If you believe this is a mistake, please contact a Staff Member.").sendTemporary(channel, 10);
            return;
        }

        String code = UUID.randomUUID().toString().split("-")[0];

        TechEmbedBuilder instructions = new TechEmbedBuilder("Verify " + e.getAuthor().getName())
                .setThumbnail(avatarUrl)
                .setText("Now go to your SpigotMC Profile and post `TechVerification." + code + "`\n\nLink to your Profile:\nhttps://www.spigotmc.org/members/" + username.toLowerCase() + "." + userId + "\n\n**Please verify yourself within 3 Minutes!**");

        Message m = instructions.send(channel);
        verificationQueue.add(e.getAuthor().getId());

        new Thread(() -> {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < TimeUnit.MINUTES.toMillis(3)) {
                for(ProfileComment all : SpigotMC.getComments(userId)) {
                    if(all.getUserId().equals(userId) && (all.getText().equals("TechVerification." + code))) {
                        m.delete().complete();
                        new TechEmbedBuilder(e.getAuthor().getName() + "'s Verification Completed").success()
                                .setText(e.getAuthor().getName() + " has successfully verified their SpigotMC Account!")
                                .setThumbnail(avatarUrl)
                                .send(channel);
                        sendInstructions();
                        verificationQueue.remove(e.getAuthor().getId());
                        TechDiscordBot.getStorage().createVerification(userId, e.getAuthor().getId());
                        new TechEmbedBuilder("Verification Complete!")
                                .setText("You've been successfully verified!\n\nHere are your purchased plugins: " + Plugin.getMembersPluginsinEmojis(e.getMember()) + "\n\n*Your roles will be updated automatically from now on!*")
                                .setThumbnail(avatarUrl)
                                .send(e.getMember());
                        new TechEmbedBuilder()
                                .setText("You may now delete the message on your profile! [Go to Comment](https://www.spigotmc.org/profile-posts/" + all.getCommentId() + ")")
                                .send(e.getMember());
                        return;
                    }
                }
            }

            m.delete().complete();
            verificationQueue.remove(e.getAuthor().getId());
            errorMessage.setText("**You took too long!**\n\nThe Verification process has timed out! Please try again.").sendTemporary(channel, 15);
        }).start();
    }

    @Override
    public String getName() {
        return "Verification";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[] {
                new Requirement(VERIFICATION_CHANNEL, 1, "Missing Verification Channel (#verification)")
        };
    }
}