package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.objects.TicketPriority;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TicketModule extends Module {

    private TextChannel channel;
    private Message lastInstructions;

    private boolean isSelection;
    private String selectionUserId;
    private TicketPriority selectionPriority;
    private Plugin selectionPlugin;
    private int selectionStep;

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

    private final DefinedQuery<Category> TICKET_CATEGORIES = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("low priority tickets", "medium priority tickets", "high priority tickets");
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
            if(lastInstructions != null) lastInstructions.delete().complete();
        }));

        sendPriorityInstructions(null);
    }

    @Override
    public void onDisable() {
        if(lastInstructions != null) lastInstructions.delete().complete();
    }

    public void sendPriorityInstructions(Member member) {
        if(isSelection) return;
        selectionStep = 1;

        Emote lowPriority = PRIORITY_EMOTES.query().get(0);
        Emote mediumPriority = PRIORITY_EMOTES.query().get(1);
        Emote highPriority = PRIORITY_EMOTES.query().get(2);

        if(lastInstructions != null) lastInstructions.delete().queue();
        TechEmbedBuilder priority = new TechEmbedBuilder("Ticket Creation" + (member != null ? " (" + member.getEffectiveName() + ")" : ""))
                .setText("First, please react with the priority of the issue below:", "", lowPriority.getAsMention() + "- Low Priority", mediumPriority.getAsMention() + "- Medium Priority", highPriority.getAsMention() + "- High Priority", "", "*Please choose the priority based on the how urgent the issue is.*");

        lastInstructions = priority.send(channel);
        if(lastInstructions != null) lastInstructions.addReaction(lowPriority).complete();
        if(lastInstructions != null) lastInstructions.addReaction(mediumPriority).complete();
        if(lastInstructions != null) lastInstructions.addReaction(highPriority).complete();
    }

    public void sendPluginInstructions(Member member) {
        if(selectionStep != 1) return;
        selectionUserId = member.getId();
        isSelection = true;
        selectionStep = 2;

        String sb = PLUGIN_EMOTES.query().all().stream().map(emote -> emote.getAsMention() + " - " + Plugin.byEmote(emote).getRoleName()).collect(Collectors.joining("\n"));

        if(lastInstructions != null) lastInstructions.delete().queue();
        TechEmbedBuilder plugin = new TechEmbedBuilder("Ticket Creation (" + member.getEffectiveName() + ")")
                .setText("Secondly, please select which plugin the issue corresponds with below:", "", sb, "", ERROR_EMOTE.query().first().getAsMention() + " - Cancel", "");

        lastInstructions = plugin.send(channel);
        for(Emote emote : PLUGIN_EMOTES.query().all()) if(lastInstructions != null) lastInstructions.addReaction(emote).complete();
        if(lastInstructions != null) lastInstructions.addReaction(ERROR_EMOTE.query().first()).complete();
    }

    public void sendIssueInstructions(Member member) {
        if(selectionStep != 2) return;
        selectionStep = 3;
        isSelection = true;

        if(lastInstructions != null) lastInstructions.delete().queue();
        TechEmbedBuilder issue = new TechEmbedBuilder("Ticket Creation (" + member.getEffectiveName() + ")")
                .setText("Last but not least, please tell us what you're having an issue with!", "", ERROR_EMOTE.query().first().getAsMention() + " - Cancel", "", "*Try not to make the message over 1024 chars long.*", "*We'll cut it off due to Discord's Limitations!*");

        lastInstructions = issue.send(channel);
        if(lastInstructions != null) lastInstructions.addReaction(ERROR_EMOTE.query().first()).complete();
    }

    public void createTicket(Member member, TicketPriority priority, Plugin plugin, String issue) {
        String name = "ticket-" + member.getEffectiveName().replaceAll("[^a-zA-Z\\d\\s_-]", "").toLowerCase();
        if(name.equals("ticket-")) name = "ticket-" + member.getUser().getId(); //Make sure the ticket has an actual name. In case the regex result is empty.

        TextChannel ticketChannel = TechDiscordBot.getGuild().createTextChannel(name)
                .setParent(getCategoryByTicketPriority(priority))
                .setTopic(member.getAsMention() + "'s Ticket | Problem Solved? Please type in !close")
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
                .addField("Plugin", plugin.getEmoji().getAsMention(), true)
                .addField("Owned Plugins", plugins, true)
                .addField("Issue", issue, false)
                .send(ticketChannel);

        new TechEmbedBuilder("New Ticket")
                .setText(member.getAsMention() + " created a new ticket (" + ticketChannel.getAsMention() + ")")
                .send(channel);

        isSelection = false;
        selectionUserId = null;
        sendPriorityInstructions(null);
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
                        .setText("You took too long!")
                        .sendTemporary(channel, 10);

                isSelection = false;
                selectionUserId = null;
                sendPriorityInstructions(null);
            }
        }).start();
    }

    public boolean isTicketChat(TextChannel channel) {
        return channel.getName().contains("ticket-");
    }

    public TextChannel getOpenTicketChat(Member member) {
        return TechDiscordBot.getGuild().getTextChannels().stream().filter(channel -> isTicketChat(channel) && channel.getTopic() != null && channel.getTopic().contains(member.getAsMention())).findFirst().orElse(null);
    }

    public Member getMemberFromTicket(TextChannel channel) {
        if(channel == null || channel.getTopic() == null) return null;
        String id = channel.getTopic().split("<")[1].split(">")[0].replace("@", "");
        return channel.getGuild().getMemberById(id);
    }

    public Category getCategoryByTicketPriority(TicketPriority priority) {
        return TICKET_CATEGORIES.query().get(priority.getValue());
    }

    @SubscribeEvent
    public void onReactionAdd(MessageReactionAddEvent e) {
        if(e.getUser() == null || e.getMember() == null) return;
        if(e.getUser().isBot()) return;
        if(e.getChannel() != channel) return;

        if((selectionUserId != null && !e.getMember().getId().equals(selectionUserId))) {
            e.getReaction().removeReaction(e.getUser()).queue();
            return;
        }

        if(getOpenTicketChat(e.getMember()) != null) {
            new TechEmbedBuilder("Ticket Creation - Error")
                    .setText("You already have an open ticket (" + getOpenTicketChat(e.getMember()).getAsMention() + ")")
                    .error()
                    .sendTemporary(channel, 10);

            isSelection = false;
            selectionUserId = null;
            sendPriorityInstructions(null);
            return;
        }

        if(e.getReactionEmote().getName().equalsIgnoreCase("error") && selectionStep != 1) {
            isSelection = false;
            selectionUserId = null;
            sendPriorityInstructions(null);
            return;
        }

        if(selectionStep == 1 || selectionUserId == null) {
            selectionPriority = TicketPriority.valueOf(e.getReactionEmote().getEmote().getName().split("_")[0].toUpperCase());
            sendPluginInstructions(e.getMember());
            startTimeout(e.getMember().getId());
        } else if(selectionStep == 2) {
            Plugin plugin = Plugin.byEmote(e.getReactionEmote().getEmote());
            if (plugin == null || e.getMember().getRoles().stream().noneMatch(r -> r.getName().contains("Verified"))) {
                new TechEmbedBuilder("Ticket - Error")
                        .error()
                        .setText("You do not own any of Tech's Plugins!")
                        .sendTemporary(channel, 10);

                e.getReaction().removeReaction(e.getUser()).queue();
                sendPriorityInstructions(null);
                return;
            }

            if(e.getMember().getRoles().stream().noneMatch(r -> r.getName().equals(plugin.getRoleName()))) {
                new TechEmbedBuilder("Ticket - Error")
                        .error()
                        .setText("You do not own " + plugin.getRoleName() + "!")
                        .sendTemporary(channel, 10);

                e.getReaction().removeReaction(e.getUser()).queue();
                return;
            }

            selectionPlugin = Plugin.byEmote(e.getReactionEmote().getEmote());
            sendIssueInstructions(e.getMember());
        } else {
            String ezMention = TechDiscordBot.getJDA().getUserById("130340486920667136").getAsMention();
            new TechEmbedBuilder("Ticket Creation - Error")
                    .setText("This shouldn't be happening. Contact " + ezMention + " (EazyFTW#0001) immediately!")
                    .error()
                    .sendTemporary(channel, 10);
            isSelection = false;
            selectionUserId = null;
            sendPriorityInstructions(null);
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
    public void onMessageCmd(GuildMessageReceivedEvent e) {
        if(e.getAuthor().isBot()) return;
        if(!isTicketChat(e.getChannel())) return;
        if(e.getMember() == null) return;

        String[] args = Arrays.copyOfRange(e.getMessage().getContentDisplay().split(" "), 1, e.getMessage().getContentDisplay().split(" ").length);
        boolean isTicketCreator = e.getChannel().getTopic() != null && e.getChannel().getTopic().contains(e.getAuthor().getAsMention());

        if(e.getMessage().getContentDisplay().startsWith("!add ")) {
            Member memberToAdd = TechDiscordBot.getMemberFromString(e.getMessage(), args[0]);

            e.getMessage().delete().queue();

            if(!isTicketCreator && !TechDiscordBot.isStaff(e.getMember())) {
                new TechEmbedBuilder("Not Enough Perms")
                        .setText("You have to be the ticket creator or a staff member to add someone!")
                        .error()
                        .send(e.getChannel());
                return;
            }

            if(memberToAdd == null) {
                new TechEmbedBuilder("Tickets - Error")
                        .setText("Cannot find the specified user!")
                        .error()
                        .send(e.getChannel());
            } else if(e.getMember() == memberToAdd && isTicketCreator) {
                new TechEmbedBuilder("Tickets - Error")
                        .setText("You cannot be added to a ticket you're already in!")
                        .error()
                        .send(e.getChannel());
            } else {
                new TechEmbedBuilder("Ticket - Added User")
                        .success()
                        .setText("Successfully added " + memberToAdd.getAsMention() + " to the ticket!")
                        .send(e.getChannel());

                Collection<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));
                e.getChannel().getManager().putPermissionOverride(memberToAdd, permissionsAllow, new ArrayList<>()).queue();
            }
        } else if(e.getMessage().getContentDisplay().startsWith("!remove ")) {
            Member memberToRemove = TechDiscordBot.getMemberFromString(e.getMessage(), args[0]);

            e.getMessage().delete().queue();

            if(!isTicketCreator && !TechDiscordBot.isStaff(e.getMember())) {
                new TechEmbedBuilder("Not Enough Perms")
                        .setText("You have to be the ticket creator or a staff member to remove someone!")
                        .success().send(e.getChannel());
                return;
            }

            boolean isTicketCreator2 = (memberToRemove != null && (e.getChannel().getTopic() != null && e.getChannel().getTopic().contains(memberToRemove.getAsMention())));
            if(memberToRemove == null) {
                new TechEmbedBuilder("Tickets - Error")
                        .setText("Cannot find the specified user!")
                        .error()
                        .send(e.getChannel());
            } else if(TechDiscordBot.isStaff(memberToRemove) || isTicketCreator2) {
                new TechEmbedBuilder("Tickets - Error")
                        .setText(memberToRemove.getAsMention() + " cannot be removed from the ticket!")
                        .error()
                        .send(e.getChannel());
            } else {
                new TechEmbedBuilder("Ticket - Removed User")
                        .success()
                        .setText("Successfully removed " + memberToRemove.getAsMention() + " from the ticket!")
                        .send(e.getChannel());

                Collection<Permission> permissionsDeny = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));
                e.getChannel().getManager().putPermissionOverride(memberToRemove, new ArrayList<>(), permissionsDeny).queue();
            }
        } else if(e.getMessage().getContentDisplay().startsWith("!close")) {
            e.getMessage().delete().submit();
            if (isTicketCreator) {
                new TechEmbedBuilder("Ticket")
                        .setText("Thank you for contacting us " + e.getAuthor().getAsMention() + ". Consider writing a review if you enjoyed the support!")
                        .send(e.getChannel());

                //Transcripts.createTranscript(e.getMember(), channel);
                e.getChannel().delete().completeAfter(10, TimeUnit.SECONDS);

                new TechEmbedBuilder("Solved Ticket")
                        .setText("The ticket (" + e.getChannel().getName() + ") created by " + e.getAuthor().getAsMention() + " is now solved. Thanks for contacting us!")
                        .success()
                        .send(channel);
                isSelection = false;
                selectionUserId = null;
                sendPriorityInstructions(null);
            } else {
                if (!TechDiscordBot.isStaff(e.getMember())) {
                    new TechEmbedBuilder("Ticket")
                            .setText("You cannot close this ticket!")
                            .error()
                            .send(e.getChannel());
                    return;
                }

                Member member = getMemberFromTicket(e.getChannel());
                boolean hasReason = e.getMessage().getContentDisplay().split(" ").length > 1;
                String[] reasons = e.getMessage().getContentDisplay().split(" ");
                String reason = String.join(" ", Arrays.copyOfRange(reasons, 1, reasons.length));
                String reasonSend = (hasReason ? " \n \n**Reason**: " + reason : "");

                new TechEmbedBuilder("Ticket")
                        .setText(e.getAuthor().getAsMention() + " has closed this support ticket." + reasonSend)
                        .send(e.getChannel());

                e.getChannel().delete().completeAfter(10, TimeUnit.SECONDS);
                if (member != null) {
                    new TechEmbedBuilder("Closed Ticket")
                            .setText("The ticket (" + e.getChannel().getName() + ") from " + member.getAsMention() + " has been closed!")
                            .success()
                            .send(channel);
                    new TechEmbedBuilder("Closed Ticket")
                            .setText("Your ticket (" + e.getChannel().getName() + ") has been closed!" + reasonSend)
                            .success()
                            .send(member);

                    isSelection = false;
                    selectionUserId = null;
                    sendPriorityInstructions(null);
                } else {
                    new TechEmbedBuilder("Closed Ticket")
                            .setText("The ticket (" + e.getChannel().getName() + ") from *member has left* has been closed!")
                            .success()
                            .send(channel);

                    isSelection = false;
                    selectionUserId = null;
                    sendPriorityInstructions(null);
                }
            }
        }
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
                new Requirement(TICKET_CATEGORIES, 1, "Missing One Or More Ticket Categories (<low/medium/high> priority tickets)"),
                new Requirement(STAFF_ROLE, 1, "Missing 'Staff' Role")
        };
    }
}