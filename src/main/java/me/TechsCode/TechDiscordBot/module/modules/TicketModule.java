package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.ServerLogs;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.objects.TicketPriority;
import me.TechsCode.TechDiscordBot.transcripts.TicketTranscript;
import me.TechsCode.TechDiscordBot.transcripts.TicketTranscriptOptions;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TicketModule extends Module {

    private TextChannel channel;
    private Message lastInstructions;
    private String selectionUserId;
    private TicketPriority selectionPriority;
    private Plugin selectionPlugin;

    private boolean isSelection;
    private int selectionStep;
    private final List<String> CLOSING_CHANNELS = new ArrayList<>();

    private final DefinedQuery<Emote> PRIORITY_EMOTES = new DefinedQuery<Emote>() {
        @Override
        protected Query<Emote> newQuery() {
            return bot.getEmotes("low_priority", "medium_priority", "high_priority");
        }
    };

    private final DefinedQuery<Emote> ERROR_EMOTE = new DefinedQuery<Emote>() {
        @Override
        protected Query<Emote> newQuery() {
            return bot.getEmotes("error");
        }
    };

    private final DefinedQuery<Emote> PLUGIN_EMOTES = new DefinedQuery<Emote>() {
        @Override
        protected Query<Emote> newQuery() {
            return bot.getEmotes(Arrays.stream(Plugin.values()).map(Plugin::getEmojiName).toArray(String[]::new));
        }
    };

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    private final DefinedQuery<Role> SUB_VERIFIED = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Sub Verified");
        }
    };

    private final DefinedQuery<Category> TICKET_CATEGORY = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("tickets");
        }
    };

    private final DefinedQuery<TextChannel> TICKET_CREATION_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("tickets");
        }
    };

    public TicketModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        channel = TICKET_CREATION_CHANNEL.query().first();

        selectionStep = 1;
        lastInstructions = null;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(lastInstructions != null)
                lastInstructions.delete().complete();
        }));

        sendPriorityInstructions(null);
    }

    @Override
    public void onDisable() {
        if(lastInstructions != null)
            lastInstructions.delete().complete();
    }

    public void sendPriorityInstructions(Member member) {
        if(isSelection) return;
        selectionStep = 1;

        Emote lowPriority = PRIORITY_EMOTES.query().get(0);

        if(lastInstructions != null) lastInstructions.delete().queue();
        TechEmbedBuilder priority = new TechEmbedBuilder("Ticket Creation" + (member != null ? " (" + member.getEffectiveName() + ")" : ""))
                .text("Please react below to create a ticket!");

        priority.queue(channel, message -> setLastInstructions(message, msg -> {
            try {
                msg.addReaction(lowPriority).queue();
            } catch (Exception ignored) {}
        }));
    }

    public void sendPluginInstructions(Member member) {
        if(selectionStep != 1) return;
        selectionUserId = member.getId();
        isSelection = true;
        selectionStep = 2;

        String sb = PLUGIN_EMOTES.query().all().stream().map(emote -> emote.getAsMention() + " - " + Plugin.byEmote(emote).getRoleName()).collect(Collectors.joining("\n"));

        if(lastInstructions != null) lastInstructions.delete().queue();
        TechEmbedBuilder plugin = new TechEmbedBuilder("Ticket Creation (" + member.getEffectiveName() + ")")
                .text("Please select which plugin the issue corresponds with below:", "", sb, "", ERROR_EMOTE.query().first().getAsMention() + " - Cancel", "");

        plugin.queue(channel, message -> setLastInstructions(message, msg -> {
            PLUGIN_EMOTES.query().all().stream().filter(emote -> msg != null).forEach(emote -> msg.addReaction(emote).queue((msg2) -> {
                try {
                    msg.addReaction(ERROR_EMOTE.query().first()).queue(null, null);
                } catch (Exception ignored) {}
            }));
        }));
    }

    public void sendIssueInstructions(Member member) {
        if(selectionStep != 2)
            return;

        selectionStep = 3;
        isSelection = true;

        if(lastInstructions != null)
            lastInstructions.delete().queue();

        TechEmbedBuilder issue = new TechEmbedBuilder("Ticket Creation (" + member.getEffectiveName() + ")")
                .text("Last but not least, please tell us what you're having an issue with!", "", ERROR_EMOTE.query().first().getAsMention() + " - Cancel", "", "*Try not to make the message over 1024 chars long.*", "*We'll cut it off due to Discord's Limitations!*");

        issue.queue(channel, message -> setLastInstructions(message, msg -> {
            try {
                msg.addReaction(ERROR_EMOTE.query().first()).queue();
            } catch (Exception ignored) {}
        }));
    }

    public void createTicket(Member member, TicketPriority priority, Plugin plugin, String issue) {
        String name = "ticket-" + member.getEffectiveName().replaceAll("[^a-zA-Z\\d\\s_-]", "").toLowerCase();
        if(name.equals("ticket-"))
            name = "ticket-" + member.getUser().getId(); //Make sure the ticket has an actual name. In case the regex result is empty.

        TextChannel ticketChannel = TechDiscordBot.getGuild().createTextChannel(name)
                .setParent(getTicketCategory())
                .setTopic(member.getAsMention() + "'s Ticket | Problem Solved? Please execute /ticket close.")
                .complete();

        ticketChannel.getManager().clearOverridesAdded().complete();
        ticketChannel.getManager().clearOverridesRemoved().complete();

        List<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));
        ticketChannel.getManager()
                .putPermissionOverride(STAFF_ROLE.query().first(), permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(member, permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(TechDiscordBot.getGuild().getPublicRole(), new ArrayList<>(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                .complete();

        String plugins = Plugin.getMembersPluginsinEmojis(member);
        new TechEmbedBuilder(member.getEffectiveName() + " - " + member.getUser().getId())
                .field("Plugin", plugin.getEmoji().getAsMention(), true)
                .field("Owned Plugins", plugins, true)
                .field("Issue", issue, false)
                .queue(ticketChannel);

        if(TechDiscordBot.getStorage().isSubVerifiedUser(member.getId())) {
            new TechEmbedBuilder("Sub Verified User Support")
                    .text("Original Pluginholder: " + TechDiscordBot.getGuild().getMemberById(TechDiscordBot.getStorage().getVerifiedIdFromSubVerifiedId(member.getId())).getAsMention())
                    .color(Color.ORANGE)
                    .queue(ticketChannel);
        }

        new TechEmbedBuilder("New Ticket")
                .text(member.getAsMention() + " created a new ticket (" + ticketChannel.getAsMention() + ")")
                .queue(channel);

        reset();
    }

    public void startTimeout(String userId) {
        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(this.selectionUserId == null || userId == null) return;
            if(this.selectionUserId.equals(userId) && isSelection) {
                new TechEmbedBuilder("Ticket - Error")
                        .error()
                        .text("You took too long!")
                        .sendTemporary(channel, 10);

                reset();
            }
        }).start();
    }

    public boolean isTicketChat(TextChannel channel) {
        return channel.getName().contains("ticket-");
    }

    public TextChannel getOpenTicketChat(Member member) {
        return TechDiscordBot.getGuild().getTextChannels().stream().filter(channel -> isTicketChat(channel) && channel.getTopic() != null && channel.getTopic().contains(member.getAsMention())).findFirst().orElse(null);
    }

    public static Member getMemberFromTicket(TextChannel channel) {
        if(channel == null || channel.getTopic() == null)
            return null;

        return channel.getGuild().getMemberById(channel.getTopic().split("<")[1].split(">")[0].replace("@", "").replace("!", ""));
    }

    public Category getTicketCategory() {
        return TICKET_CATEGORY.query().first();
    }

    @SubscribeEvent
    public void onReactionAdd(MessageReactionAddEvent e) {
        if(e.getUser() == null || e.getMember() == null || e.getUser().isBot() || e.getChannel() != channel)
            return;
        if((selectionUserId != null && !e.getMember().getId().equals(selectionUserId))) {
            e.getReaction().removeReaction(e.getUser()).queue();
            return;
        }

        if(getOpenTicketChat(e.getMember()) != null) {
            new TechEmbedBuilder("Ticket Creation - Error")
                    .text("You already have an open ticket (" + getOpenTicketChat(e.getMember()).getAsMention() + ")")
                    .error()
                    .sendTemporary(channel, 10);

            reset();
            return;
        }

        if(e.getReactionEmote().getName().equalsIgnoreCase("error") && selectionStep != 1) {
            reset();
            return;
        }

        if(selectionStep == 1 || selectionUserId == null) {
            selectionPriority = TicketPriority.valueOf(e.getReactionEmote().getEmote().getName().split("_")[0].toUpperCase());
            sendPluginInstructions(e.getMember());
            startTimeout(e.getMember().getId());
        } else if(selectionStep == 2) {
            Plugin plugin = Plugin.byEmote(e.getReactionEmote().getEmote());

            if (plugin == null)
                return;

            if(e.getMember().getRoles().stream().noneMatch(r -> r.getName().equals(plugin.getRoleName())) && !e.getMember().getRoles().contains(SUB_VERIFIED.query().first())) {
                new TechEmbedBuilder("Ticket - Error")
                        .error()
                        .text("You do not own " + plugin.getRoleName() + "!")
                        .sendTemporary(channel, 10);
                e.getReaction().removeReaction(e.getUser()).queue();

                return;
            }

            selectionPlugin = Plugin.byEmote(e.getReactionEmote().getEmote());
            sendIssueInstructions(e.getMember());
        } else {
            String ezMention = TechDiscordBot.getJDA().getUserById("130340486920667136").getAsMention();

            new TechEmbedBuilder("Ticket Creation - Error")
                    .text("This shouldn't be happening. Contact " + ezMention + " (EazyFTW#0001) immediately!")
                    .error()
                    .sendTemporary(channel, 10);

            reset();
        }
    }

    @SubscribeEvent
    public void onMessageIssue(GuildMessageReceivedEvent e) {
        if(e.getAuthor().isBot()) return;
        if(e.getChannel() != channel) return;
        if(e.getMember() == null) return;

        if(selectionUserId == null || !e.getMember().getId().equals(selectionUserId) || selectionStep != 3) {
            e.getMessage().delete().queue();
            return;
        }

        if(!isSelection) return;

        String message = e.getMessage().getContentDisplay();
        if(message.length() > 1024) message = message.substring(0, 1024); //Make sure It outputs the embed. Embed values cannot be longer than 1024 chars.

        e.getMessage().delete().queue();
        createTicket(e.getMember(), selectionPriority, selectionPlugin, message);
    }

    @SubscribeEvent
    public void onSlashCommand(SlashCommandEvent e) {
        if(e.getMember() == null || e.getMember().getUser().isBot()) return;

        if(e.getName().equals("ticket") && e.getSubcommandName() != null) {
            if(!isTicketChat(e.getTextChannel())) {
                e.reply("This channel is not a ticket!").setEphemeral(true).queue();
                return;
            }

            boolean isTicketCreator = e.getTextChannel().getTopic() != null && e.getTextChannel().getTopic().contains(e.getMember().getAsMention());

            if(!isTicketCreator && !TechDiscordBot.isStaff(e.getMember())) {
                e.reply("You have to be the ticket creator or a staff member to execute this command!").setEphemeral(true).queue();
                return;
            }

            Member member = e.getOption("member") == null ? null : e.getOption("member").getAsMember();
            if(member == null && !e.getSubcommandName().equals("close")) {
                e.reply("*Member is invalid!** This probably shouldn't be happening...").setEphemeral(true).queue();
                return;
            }

            switch (e.getSubcommandName()) {
                case "add":
                    if (e.getMember().equals(member)) {
                        e.reply("You can't be added to a ticket you're already in.").setEphemeral(true).queue();
                        return;
                    }

                    e.reply("Successfully added " + member.getAsMention() + " to this ticket!").queue();

                    Collection<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));
                    e.getTextChannel().getManager().putPermissionOverride(member, permissionsAllow, new ArrayList<>()).queue();
                    break;
                case "remove":
                    boolean isTicketCreator2 = e.getTextChannel().getTopic() != null && e.getTextChannel().getTopic().contains(member.getAsMention());

                    if (TechDiscordBot.isStaff(member) || isTicketCreator2) {
                        e.reply(member.getAsMention() + " can't be removed from this ticket!").setEphemeral(true).queue();
                        return;
                    }

                    e.reply("Successfully removed " + member.getAsMention() + " from this ticket!").queue();

                    Collection<Permission> permissionsDeny = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));
                    e.getTextChannel().getManager().putPermissionOverride(member, new ArrayList<>(), permissionsDeny).queue();
                    break;
                case "close":
                    //TicketTranscript transcript = TicketTranscript.buildTranscript(e.getTextChannel(), TicketTranscriptOptions.DEFAULT);
                    String channelId = e.getChannel().getId();

                    if (isTicketCreator) {
                        if (CLOSING_CHANNELS.contains(e.getChannel().getId())) {
                            e.reply("This ticket is already closing!").setEphemeral(true).queue();
                            return;
                        }

                        e.replyEmbeds(
                            new TechEmbedBuilder("Ticket")
                                    .text("Thank you for contacting us " + e.getMember().getAsMention() + ". Consider writing a review if you enjoyed the support!")
                                    .build()
                        ).queue();

//                        transcript.build(object -> {
//                            new TechEmbedBuilder("Transcript")
//                                    .text("You can view your recently closed ticket's transcript here:\n" + transcript.getUrl())
//                                    .color(Color.ORANGE)
//                                    .queue(e.getMember());
//
//                            ServerLogs.log(
//                                new TechEmbedBuilder("Ticket Transcript")
//                                        .text("Transcript of " + e.getMember().getAsMention() +  "'s ticket:\n" + transcript.getUrl())
//                                        .color(Color.ORANGE)
//                            );
//
//                            e.getTextChannel().delete().queueAfter(15, TimeUnit.SECONDS, s -> CLOSING_CHANNELS.remove(channelId));
//                            TechDiscordBot.getStorage().saveTranscript(object);
//                        });

                        new TechEmbedBuilder("Solved Ticket")
                                .text("The ticket (" + e.getTextChannel().getName() + ") created by " + e.getMember().getAsMention() + " is now solved. Thanks for contacting us!")
                                .success()
                                .queueAfter(channel, 15, TimeUnit.SECONDS, (msg) -> reset());
                    } else {
                        if (!TechDiscordBot.isStaff(e.getMember())) {
                            e.reply("You cannot close this ticket!").setEphemeral(true).queue();
                            return;
                        }

                        if (CLOSING_CHANNELS.contains(e.getChannel().getId())) {
                            e.reply("This ticket is already closing!").setEphemeral(true).queue();
                            return;
                        }

                        String reason = e.getOption("reason") == null ? null : e.getOption("reason").getAsString();

                        boolean hasReason = reason != null;
                        String reasonSend = (hasReason ? " \n\n**Reason**: " + reason : "");

                        Member ticketMember = getMemberFromTicket(e.getTextChannel());

                        e.replyEmbeds(
                                new TechEmbedBuilder("Ticket")
                                        .text(e.getMember().getAsMention() + " has closed this support ticket." + reasonSend)
                                        .build()
                        ).queue();

                        CLOSING_CHANNELS.add(e.getChannel().getId());
                        e.getTextChannel().delete().queueAfter(15, TimeUnit.SECONDS, s -> CLOSING_CHANNELS.remove(channelId));

                        if (ticketMember != null) {
                            new TechEmbedBuilder("Ticket Closed")
                                    .text("The ticket (" + e.getTextChannel().getName() + ") from " + ticketMember.getAsMention() + " has been closed!")
                                    .success()
                                    .queueAfter(channel, 15, TimeUnit.SECONDS, (msg) -> reset());
                            if (ticketMember != null) {
                                new TechEmbedBuilder("Ticket Closed")
                                        .text("Your ticket (" + e.getTextChannel().getName() + ") has been closed!" + reasonSend)
                                        .success()
                                        .queue(ticketMember);
                            }
                        } else {
                            new TechEmbedBuilder("Ticket Closed")
                                    .text("The ticket (" + e.getTextChannel().getName() + ") from *member has left* has been closed!")
                                    .success()
                                    .queueAfter(channel, 15, TimeUnit.SECONDS, (msg) -> reset());
                        }

//                        transcript.build(object -> {
//                            if(ticketMember != null) {
//                                ServerLogs.log(
//                                    new TechEmbedBuilder("Ticket Transcript")
//                                            .text(ticketMember == null ? "Transcript of #" + e.getChannel().getName() + ": " + transcript.getUrl() : "Transcript of " + ticketMember.getAsMention() +  "'s ticket:\n" + transcript.getUrl())
//                                            .color(Color.ORANGE)
//                                );
//
//                                if (ticketMember != null) {
//                                    new TechEmbedBuilder("Ticket Closed")
//                                            .text("Your ticket (" + e.getTextChannel().getName() + ") has been closed!" + reasonSend + "\n\nYou can view the transcript here:\n" + transcript.getUrl())
//                                            .success()
//                                            .queue(ticketMember);
//                                }
//                            }
//
//                            TechDiscordBot.getStorage().saveTranscript(object);
//                        });
                    }

                    break;
                default:
                    e.reply("Could not recognize this sub command!").setEphemeral(true).queue();
                    break;
            }
        }
    }

    public void reset() {
        isSelection = false;
        selectionUserId = null;
        sendPriorityInstructions(null);
    }

    public void setLastInstructions(Message message) {
        this.lastInstructions = message;
    }

    public void setLastInstructions(Message message, Consumer<Message> after) {
        this.lastInstructions = message;
        after.accept(this.lastInstructions);
    }

    @Override
    public String getName() {
        return "Tickets";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[] {
                new Requirement(TICKET_CREATION_CHANNEL, 1, "Missing Creation Channel (#tickets)"),
                new Requirement(TICKET_CATEGORY, 1, "Missing Tickets Category (tickets)"),
                new Requirement(STAFF_ROLE, 1, "Missing 'Staff' Role")
        };
    }
}
