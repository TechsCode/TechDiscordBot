package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import me.TechsCode.TechDiscordBot.verification.Spigot;
import me.TechsCode.TechDiscordBot.verification.VerificationUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Collections;
import java.util.stream.Collectors;

public class VerificationModule extends Module {

    private TextChannel channel;
    private Message lastSelectionEmbed;
    private String selectedMarket = null;
    private Message instruction;

    public VerificationModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "Verification";
    }

    private final DefinedQuery<TextChannel> VERIFICATION_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("\uD83D\uDCD8︱verification");
        }
    };

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(VERIFICATION_CHANNEL, 1, "Missing Verification Channel (#verification)")
        };
    }

    @Override
    public void onEnable() {
        channel = VERIFICATION_CHANNEL.query().first();
        channel.getIterableHistory()
                .takeAsync(100)
                .thenAccept(msg -> channel.purgeMessages(msg.stream().filter(m -> m.getEmbeds().size() > 0 && m.getEmbeds().get(0).getAuthor() != null && m.getEmbeds().get(0).getAuthor().getName() != null && m.getEmbeds().get(0).getAuthor().getName().equals("Marketplace Selector")).collect(Collectors.toList())));

        lastSelectionEmbed = null;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (lastSelectionEmbed != null)
                lastSelectionEmbed.delete().complete();
        }));

        sendSelection();
    }

    @Override
    public void onDisable() {
        if (lastSelectionEmbed != null) lastSelectionEmbed.delete().submit();
    }

    private void deleteSelection() {
        if (lastSelectionEmbed != null){
            lastSelectionEmbed.delete().submit();
            lastSelectionEmbed = null;
        }
    }

    public void sendSelection() {
        deleteSelection();

        channel.sendMessageEmbeds( new TechEmbedBuilder("Marketplace Selector")
            .text("Have you purchased one or more of our plugins and wish to verify yourself?\n\n"+
            "You have come to the right place!\n"+
            "Just select the emoji below that corresponds to the marketplace where you bought the plugin(s), and we will explain the next steps after your selection.")
            .build()
        ).setActionRow(
                Button.primary("spigot", "Spigot").withEmoji(Emoji.fromMarkdown("<:spigot:879756315747053628>")),
                Button.primary("mc-market", "MC-Market").withEmoji(Emoji.fromMarkdown("<:mcmarket:879756190089895988>")),
                Button.primary("songoda", "Songoda").withEmoji(Emoji.fromMarkdown("<:songoda:879756362861666375>")),
                Button.primary("polymart", "Polymart").withEmoji(Emoji.fromMarkdown("<:polymart:879756228589400145>")).asDisabled()
        ).queue((message) -> {
            lastSelectionEmbed = message;
        });
    }

    @SubscribeEvent
    public void onButtonClick(ButtonClickEvent e) {
        Member member = e.getMember();
        assert member != null;

        TechEmbedBuilder errorMessage = new TechEmbedBuilder("Error (" + member.getUser().getName() + ")").error();

        if(e.getComponentId().equals("spigot")){
            if(!VerificationUtil.spigotApiUsable(e, channel, errorMessage)) return;
            if(VerificationUtil.isVerified(e, channel, errorMessage)) return;

            deleteSelection();

            channel.getManager().putPermissionOverride(member, Collections.singleton(Permission.MESSAGE_WRITE), Collections.singletonList(Permission.MESSAGE_TTS));
            instruction = channel.sendMessage(Spigot.sendInstructions().build()).complete();
            selectedMarket = "spigot";
            //TODO Add logger when started verification
        }
        if(e.getComponentId().equals("mc-market")){
            deleteSelection();
            selectedMarket = "mc-market";
            //TODO Add the mc-market verification trigger
        }
        if(e.getComponentId().equals("songoda")){
            new TechEmbedBuilder("Songoda Verification").text("To verify your Songoda purchase you need to connect your discord account to your songoda account.\n\nNeed help with connecting?\nYou can connect your account [here](https://songoda.com/account/integrations)").error().sendTemporary(channel, 15);;
        }
        if(e.getComponentId().equals("polymart")){
            new TechEmbedBuilder("Polymart Verification").text("It is not possible to verify your Polymart purchase because we haven't uploaded them yet.").error().sendTemporary(channel, 15);
            selectedMarket = "polymart";
        }
    }

    @SubscribeEvent
    public void onMessasge(GuildMessageReceivedEvent e) {
        Member member = e.getMember();
        if (member == null) return;
        if (e.getAuthor().isBot()) return;
        if(e.getChannel() != channel) return;

        TechEmbedBuilder errorMessage = new TechEmbedBuilder("Error (" + member.getUser().getName() + ")").error();
        String username = e.getMessage().getContentDisplay();

        e.getMessage().delete().submit();

        if(selectedMarket == null) {
            new TechEmbedBuilder("ERROR").text("It seems that you have fooled the system!\n\n*It seems you haven't selected a marketplace to verify your self. If you think this is a mistake contact a staff member!*").error().sendTemporary(channel, 10);
            sendSelection();
            return;
        }

        switch (selectedMarket) {
            case "spigot":
                instruction.delete().complete();
                if (!VerificationUtil.isVerifyingVerifiedUser(username, channel, errorMessage) || !VerificationUtil.hasPurchased(username, channel, errorMessage)) {
                    newSelection();
                    break;
                } else if (Spigot.verify(e)) {
                    newSelection();
                    break;
                } else {
                    newSelection();
                    break;
                }
            case "mc-market":
                newSelection();
                break;
            case "polymart":
                newSelection();
                break;
        }
    }

    private void newSelection(){
        sendSelection();
        selectedMarket = null;
    }




//##################################################################//



//#########################################################//

//    @SubscribeEvent
//    public void onMessage(GuildMessageReceivedEvent e) {
//        if (e.getMember() == null) return;
//        if (e.getAuthor().isBot()) return;
//        if (!e.getChannel().equals(channel)) return;
//
//        String username = e.getMessage().getContentDisplay();
//        e.getMessage().delete().complete();
//
//        TechEmbedBuilder errorMessage = new TechEmbedBuilder("Error (" + e.getAuthor().getName() + ")").error();
//
//        if (!TechDiscordBot.getBot().getSpigotStatus().isUsable()) {
//            errorMessage.text("**The API is currently offline.**\nThere is no ETA of when it will be back up.\nYou will have to wait to verify until then.").error().sendTemporary(channel, 10);
//
//            String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + "Tried to verify but the the api is down!";
//            alertMsg(msg);
//
//            return;
//        }
//
//        if (verificationQueue.contains(e.getAuthor().getId())) {
//            errorMessage.text("Please follow the instruction above!").sendTemporary(channel, 15);
//            return;
//        }
//
//        Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(e.getAuthor().getId());
//        if (existingVerification != null) {
//            errorMessage.text("You've already linked to your SpigotMC Account and your roles will be updated automatically!").sendTemporary(channel, 15);
//            return;
//        }
//
//        if (username.contains(" ")) {
//            errorMessage.text("Please type in your SpigotMC Username!").sendTemporary(channel, 5);
//            return;
//        }
//
//        Purchase[] purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().username(username).toArray(new Purchase[0]);
//
//        if (purchases.length == 0) {
//            errorMessage.text("User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " does not own any of Tech's Plugins!\n\n*It may take up to 20 minutes for the bot to recognize new purchases.*\n\n*This could also be an issue with the api. If you believe this is a mistake, please contact a staff member!*");
//
//            if (TechDiscordBot.getBot().getSpigotStatus() == APIStatus.NOT_FETCHING) {
//                errorMessage.text(errorMessage.getText() + "\n\n**The API is currently not fetching new information, this could also be the issue.");
//
//                String msg = "User (" + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + ") tried to verify but the the api is down!";
//                alertMsg(msg);
//
//                return;
//            }
//
//            errorMessage.error().sendTemporary(channel, 10);
//            return;
//        }
//
//        username = purchases[0].getUser().getUsername();
//        String userId = purchases[0].getUser().getUserId();
//        String avatarUrl = purchases[0].getUser().getAvatar();
//
//        existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(userId);
//
//        if (existingVerification != null) {
//            Purchase purchase = TechDiscordBot.getSpigotAPI().getSpigotPurchases().userId(existingVerification.getUserId()).get(0);
//
//            String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " Has tried to verify as https://www.spigotmc.org/members/" + username.toLowerCase() + "." + userId + " But this user is already verified!";
//            alertMsg(msg);
//
//            errorMessage.text("The SpigotMC User " + username + " is already linked with " + purchase.getUser().getUsername() + ". If you believe this is a mistake, please contact a Staff Member.").sendTemporary(channel, 10);
//            return;
//        }
//
//        String code = UUID.randomUUID().toString().split("-")[0];
//
//        TechEmbedBuilder instructions = new TechEmbedBuilder("Verify " + e.getAuthor().getName())
//                .thumbnail(avatarUrl)
//                .text("Now go to your SpigotMC Profile and post `TechVerification." + code + "`\n\nLink to your Profile:\nhttps://www.spigotmc.org/members/" + username.toLowerCase() + "." + userId + "\n\n**Please verify yourself within 3 Minutes!**");
//
//        Message m = e.getMessage().getChannel().sendMessage(instructions.build()).complete();
//        verificationQueue.add(e.getAuthor().getId());
//        String finalUsername = username;
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(TimeUnit.MINUTES.toMillis(3));
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//
//            ProfileCommentList comments = getSpigotAPI().getSpigotProfileComments(finalUsername+"."+userId, false);
//
//            for (ProfileComment all : comments) {
//                if (all.getText().equals("TechVerification." + code)) {
//                    if (all.getUserId().equals(finalUsername+"."+userId)) {
//                        m.delete().complete();
//
//                        String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " Has verified as https://www.spigotmc.org/members/" + finalUsername.toLowerCase() + "." + userId;
//                        alertMsg(msg);
//
//                        VerificationLogs.log(
//                                new TechEmbedBuilder(e.getAuthor().getName() + "'s Verification Completed")
//                                        .success().text(e.getAuthor().getName() + " has successfully verified their SpigotMC Account!")
//                                        .thumbnail(avatarUrl)
//                        );
//                    }
//
//                    sendSelection();
//                    this.verificationQueue.remove(e.getAuthor().getId());
//
//                    if (all.getUserId().equals(finalUsername+"."+userId)) {
//                        TechDiscordBot.getStorage().createVerification(userId, e.getAuthor().getId());
//
//                        new TechEmbedBuilder("Verification Complete!")
//                                .text("You've been successfully verified!\n\nHere are your purchased plugins: " + Plugin.getMembersPluginsinEmojis(e.getMember()) + "\n\n*Your roles will be updated automatically from now on!*")
//                                .thumbnail(avatarUrl)
//                                .queue(e.getMember());
//                    } else {
//                        String msg = "User " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator() + " Has tried to verify as https://www.spigotmc.org/members/" + finalUsername.toLowerCase() + "." + userId;
//                        alertMsg(msg);
//
//                        m.editMessage(errorMessage.text("Please verify your own account.").build()).complete().delete().queueAfter(10L, TimeUnit.SECONDS);
//                    }
//
//                    new TechEmbedBuilder()
//                            .text("You may now delete the message on your profile! [Go to Comment](https://www.spigotmc.org/profile-posts/" + all.getCommentId() + ")")
//                            .queue(e.getMember());
//                    return;
//                }
//            }
//
//            verificationQueue.remove(e.getAuthor().getId());
//            m.editMessage(errorMessage.text("**You took too long!**\n\nThe Verification process has timed out! Please try again.").build())
//                    .complete()
//                    .delete()
//                    .queueAfter(10, TimeUnit.SECONDS);
//        }).start();
//    }
}
