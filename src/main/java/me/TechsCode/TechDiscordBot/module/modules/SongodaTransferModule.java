package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchase;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SongodaTransferModule extends Module {

    private final DefinedQuery<TextChannel> TRANSFER_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("songoda-transfer");
        }
    };

    private final DefinedQuery<TextChannel> TRANSFER_STAFF_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("songoda-to-transfer");
        }
    };

    private Message lastInstructions;

    private String currentId;
    private Selection selectionStep;
    private String email;
    private String username;

    private String plugins;

    public SongodaTransferModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        lastInstructions = null;
        username = null;
        email = null;

        plugins = "";
        selectionStep = Selection.WAITING;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(lastInstructions != null) lastInstructions.delete().complete();
        }));

        sendInstructions();
    }

    public void sendInstructions() {
        if(lastInstructions != null) lastInstructions.delete().queue();
        TechEmbedBuilder howItWorksMessage = new TechEmbedBuilder("Transfer your Songoda Account").setText("React to the emoji below to start the transfer to Spigot!\n\n**Spamming/abusing this will result in a ban!**");
        lastInstructions = howItWorksMessage.send(TRANSFER_CHANNEL.query().first());
        lastInstructions.addReaction(TechDiscordBot.getJDA().getEmoteById("433379431269138442")).queue();

        selectionStep = Selection.WAITING;
        username = null;
        email = null;
        plugins = "";
        currentId = "";
    }

    public void sendInstructionsAfter(Message message2) {
        TechEmbedBuilder howItWorksMessage = new TechEmbedBuilder("Transfer your Songoda Account").setText("React to the emoji below to start the transfer to Spigot!\n\n**Spamming/abusing this will result in a ban!**");

        howItWorksMessage.sendAfter(TRANSFER_CHANNEL.query().first(), 5, message -> {
            lastInstructions = message;
            lastInstructions.addReaction(TechDiscordBot.getJDA().getEmoteById("433379431269138442")).queue();

            selectionStep = Selection.WAITING;
            username = null;
            email = null;
            plugins = "";
            currentId = "";

            if(message2 != null) message2.delete().queue();
        });
    }

    @SubscribeEvent
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (e.getUser() == null || e.getMember() == null) return;
        if (e.getUser().isBot()) return;
        if (e.getChannel() != TRANSFER_CHANNEL.query().first()) return;
        if (selectionStep != Selection.WAITING) return;
        if (e.getMember().getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("Requested-Transfer"))) {
            e.getReaction().removeReaction(e.getUser()).queue();
            return;
        }

        currentId = e.getUserId();

        if (lastInstructions != null) lastInstructions.delete().queue();

        List<String> purchases = TechDiscordBot.getSongodaPurchases().stream()
                .filter(p -> p.getDiscord() != null && p.getDiscord().equals(e.getUser().getName() + "#" + e.getUser().getDiscriminator())).map(SongodaPurchase::getProduct).collect(Collectors.toList());

        plugins = Plugin.getEmotesByList(purchases);

        if(purchases.size() != 0) {
            selectionStep = Selection.SPIGOT_USERNAME;
            SongodaPurchase purchase = TechDiscordBot.getSongodaPurchases().stream()
                    .filter(p -> p.getDiscord() != null && p.getDiscord().equals(e.getUser().getName() + "#" + e.getUser().getDiscriminator())).findFirst().orElse(null);

            email = purchase.getEmail();
            username = purchase.getUsername();

            lastInstructions = new TechEmbedBuilder("Songoda Account Transfer (" + e.getUser().getName() + ")")
                    .setText("We've detected that you've bought " + plugins + " using your linked discord account.\n\nCould you please provide your Spigot Username?")
                    .send(TRANSFER_CHANNEL.query().first());
        } else {
            selectionStep = Selection.EMAIL;

            lastInstructions = new TechEmbedBuilder("Songoda Account Transfer (" + e.getUser().getName() + ")")
                    .setText("We couldn't find a plugin connected to your discord.\n\nCould you please provide your email tied to your Songoda account?")
                    .send(TRANSFER_CHANNEL.query().first());
        }

        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(3));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (selectionStep == Selection.WAITING) return;
            sendInstructions();
            new TechEmbedBuilder("Songoda Account Transfer (" + e.getUser().getName() + ") - Error")
                    .error()
                    .setText("You took too long!")
                    .sendTemporary(TRANSFER_CHANNEL.query().first(), 10);
        }).start();
    }

    @SubscribeEvent
    public void onChat(GuildMessageReceivedEvent e) {
        if(e.getAuthor().isBot()) return;
        if(e.getChannel() != TRANSFER_CHANNEL.query().first()) return;
        if(e.getMember() == null) return;

        e.getMessage().delete().queue();

        if(!e.getMember().getId().equals(currentId)) return;
        if(selectionStep == Selection.WAITING) return;

        if(e.getMessage().getContentDisplay().equalsIgnoreCase("cancel")) {
            sendInstructions();
            return;
        }

        if(selectionStep == Selection.SPIGOT_USERNAME) {
            lastInstructions.delete().queue();
            selectionStep = Selection.WAITING;

            new TechEmbedBuilder("Songoda Account Transfer (" + e.getAuthor().getName() + ")")
                    .setText("Currently informing Tech about your transfer request.\n\n**Please wait...**")
                    .sendTemporary(TRANSFER_CHANNEL.query().first(), 4);

            sendTransferRequest(email, username, e.getMessage().getContentDisplay(), e.getMember());
            new TechEmbedBuilder("Songoda Account Transfer (" + e.getAuthor().getName() + ")")
                    .setText("Successfully contacted Tech. You should receive the plugin on SpigotMC soon!")
                    .sendAfter(TRANSFER_CHANNEL.query().first(), 4, this::sendInstructionsAfter);

        } else if(selectionStep == Selection.EMAIL) {
            List<String> purchases = TechDiscordBot.getSongodaPurchases().stream()
                    .filter(p -> p.getEmail() != null && p.getEmail().equals(e.getMessage().getContentDisplay())).map(SongodaPurchase::getProduct).collect(Collectors.toList());

            plugins = Plugin.getEmotesByList(purchases);

            lastInstructions.delete().queue();
            if(purchases.size() == 0) {
                lastInstructions = new TechEmbedBuilder("Songoda Account Transfer (" + e.getAuthor().getName() + ")")
                        .setText("Could not find any purchases linked to that email.\nWant to try again?\n\nType `cancel` to cancel.")
                        .send(TRANSFER_CHANNEL.query().first());
            } else {
                selectionStep = Selection.SPIGOT_USERNAME;
                SongodaPurchase purchase = TechDiscordBot.getSongodaPurchases().stream()
                        .filter(p -> p.getEmail() != null && p.getEmail().equals(e.getMessage().getContentDisplay())).findFirst().orElse(null);

                email = purchase.getEmail();
                username = purchase.getUsername();

                lastInstructions = new TechEmbedBuilder("Songoda Account Transfer (" + e.getAuthor().getName() + ")")
                        .setText("We've detected that you've bought " + plugins + " using your linked discord account.\n\nCould you please provide your Spigot Username?")
                        .send(TRANSFER_CHANNEL.query().first());
            }
        }
    }

    public void sendTransferRequest(String songodaEmail, String songodaUsername, String spigotUsername, Member member) {
        member.getGuild().addRoleToMember(member, member.getGuild().getRolesByName("Requested-Transfer", true).get(0)).queue();
        new TechEmbedBuilder("Songoda Transfer Request")
                .setText("Request by " + member.getAsMention() + "\n\nPlugins - " + plugins + "\nSpigot Username - " + spigotUsername + "\n\nSongoda Email - " + (songodaEmail == null ? "N/A" : songodaEmail) + "\nSongoda Username - " + (songodaUsername == null ? "N/A" : songodaUsername))
                .send(TRANSFER_STAFF_CHANNEL.query().first());
    }

    @Override
    public void onDisable() {
        if(lastInstructions != null) lastInstructions.delete().complete();
    }

    @Override
    public String getName() {
        return "Songoda Transfer";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[] {
                new Requirement(TRANSFER_CHANNEL, 1, "Missing Transfer Channel (#songoda-transfer)"),
                new Requirement(TRANSFER_STAFF_CHANNEL, 1, "Missing Staff Transfer Channel (#songoda-to-transfer)")
        };
    }

    public enum Selection {

        WAITING,
        SPIGOT_USERNAME,
        EMAIL;

    }
}
