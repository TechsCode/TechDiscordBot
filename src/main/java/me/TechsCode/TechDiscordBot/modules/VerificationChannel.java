package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.storage.Verification;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.spigot.ProfileComment;
import me.TechsCode.TechDiscordBot.util.spigot.SpigotMC;
import me.TechsCode.TechsCodeAPICli.objects.Purchase;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VerificationChannel extends Module {

    private final DefinedQuery<TextChannel> VERIFICATION_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("verification");
        }
    };

    private SpigotMC spigotMC;

    private TextChannel channel;
    private Message lastInstructions, apiNotAvailable;
    private List<String> verificationQueue;

    public VerificationChannel(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        this.spigotMC = new SpigotMC();

        channel = VERIFICATION_CHANNEL.query().first();

        lastInstructions = null;
        apiNotAvailable = null;
        verificationQueue = new ArrayList<>();

        sendInstructions();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(lastInstructions != null) {
                    lastInstructions.delete().complete();
                }

                if(apiNotAvailable != null) {
                    apiNotAvailable.delete().complete();
                }
            }
        });

        /* Web API Offline Message Thread */
        new Thread(() -> {
            while (true) {
                if(bot.getTechsCodeAPI().isAvailable()) {
                    if(apiNotAvailable != null) {
                        apiNotAvailable.delete().submit();
                        apiNotAvailable = null;
                    }
                } else {
                    if(apiNotAvailable == null) {
                        CustomEmbedBuilder message = new CustomEmbedBuilder("Verification Disabled")
                                .setText("The Web API is currently unavailable. Please contact staff for more info!")
                                .error();

                        apiNotAvailable = message.send(channel);
                    }
                }

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(15));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDisable() {
        if(lastInstructions != null) {
            lastInstructions.delete().submit();
        }
    }

    @Override
    public String getName() {
        return "Verification Channel";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(VERIFICATION_CHANNEL, 1, "Missing Verification Channel (#verification)")
        };
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if(e.getAuthor().isBot()) return;

        if(!e.getChannel().equals(VERIFICATION_CHANNEL.query().first())) {
            return;
        }

        // Remove typed in message
        e.getMessage().delete().complete();

        // Preparing error message for future usages
        CustomEmbedBuilder errorMessage = new CustomEmbedBuilder("Error (" + e.getAuthor().getName() + ")").error();

        if(verificationQueue.contains(e.getAuthor().getId())) {
            errorMessage.setText("Please follow the instruction above!").sendTemporary(channel, 15);
            return;
        }

        Verification existingVerification = bot.getStorage().retrieveVerificationWithDiscord(e.getAuthor().getId());
        if(existingVerification != null) {
            errorMessage.setText("You are already linked to your SpigotMC Account and your roles will be updated automatically!").sendTemporary(channel, 15);
            return;
        }

        String username = e.getMessage().getContentDisplay();
        if(username.contains(" ")) {
            errorMessage.setText("Please type in your SpigotMC name!").sendTemporary(channel, 5);
            return;
        }

        Purchase[] purchases = bot.getTechsCodeAPI().getPurchases().username(username).get();

        if(purchases.length == 0) {
            errorMessage.setText("The user " + username + " does not own any of Tech's Plugins. It can take up to 15 minutes from purchase for the Bot to recognize your purchase.").sendTemporary(channel, 10);
            return;
        }

        username = purchases[0].getUsername();
        String userId = purchases[0].getUserId();

        existingVerification = bot.getStorage().retrieveVerificationWithSpigot(userId);
        if(existingVerification != null) {
            Purchase purchase = bot.getTechsCodeAPI().getPurchases().userId(existingVerification.getUserId()).first();
            errorMessage.setText("The SpigotMC User " + username + " is already linked with " + purchase.getUsername() + ". If you believe this is a mistake, please contact Tech.").sendTemporary(channel, 10);
            return;
        }

        CustomEmbedBuilder instructions = new CustomEmbedBuilder("Verification of " + e.getAuthor().getName())
        .setText("Now go to your SpigotMC Profile and post ``TechVerification`` \n\nLink to your Profile:\nhttps://www.spigotmc.org/members/" + userId + "\n\n**Please do that within 3 Minutes.**");

        Message m = instructions.send(channel);

        verificationQueue.add(e.getAuthor().getId());

        new Thread() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();

                while (System.currentTimeMillis()-start < TimeUnit.MINUTES.toMillis(3)) {
                    for(ProfileComment all : spigotMC.getComments(userId)) {
                        if(all.getUserId().equals(userId) && all.getText().equals("TechVerification")) {
                            m.delete().complete();

                            new CustomEmbedBuilder("Verification Completed for " + e.getAuthor().getName()).success()
                                    .setText(e.getAuthor().getName() + " has successfully verified their SpigotMC Account!")
                                    .send(channel);

                            sendInstructions();
                            verificationQueue.remove(e.getAuthor().getId());

                            bot.getStorage().createVerification(userId, e.getAuthor().getId());
                            return;
                        }
                    }
                }

                m.delete().complete();
                verificationQueue.remove(e.getAuthor().getId());
                errorMessage.setText("The Verification process has timed out! Please try again.").sendTemporary(channel, 15);
            }
        }.start();
        return;
    }

    public void sendInstructions() {
        if(lastInstructions != null) {
            lastInstructions.delete().complete();
        }

        CustomEmbedBuilder howItWorksMessage = new CustomEmbedBuilder("How It Works")
                .setText("Put your SpigotMC name in this Chat to verify.");

        lastInstructions = howItWorksMessage.send(channel);
    }
}
