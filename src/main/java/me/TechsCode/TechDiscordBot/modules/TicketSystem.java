package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.*;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.Util;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TicketSystem extends Module {

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

    private final DefinedQuery<Category> UNRESPONDED_TICKETS_CATEGORY = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("unresponded-tickets");
        }
    };

    private final DefinedQuery<Category> RESPONDED_TICKETS_CATEGORY = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("responded-tickets");
        }
    };

    private final DefinedQuery<Category> IN_PROGRESS_TICKETS_CATEGORY = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("in-progress-tickets");
        }
    };

    private final DefinedQuery<Category> TECH_TICKETS_CATEGORY = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("tech-tickets");
        }
    };

    private final DefinedQuery<TextChannel> CREATION_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("tickets");
        }
    };

    private Message lastInstructions, apiNotAvailable;

    private String[] closeCommands = new String[]{"!solved", "!close", "-close", "-solved"};
    private String[] respondCommand = new String[]{"-r", "-respond", "!r", "!respond"};
    private String[] unRespondCommand = new String[]{"-ur", "-unrespond", "!ur", "!unrespond"};
    private String[] inProgressCommand = new String[]{"-i", "!i", "!ip", "-ip"};
    private String[] toTechCommands = new String[]{"!tech", "-tech", "!t", "-t"};
    private String[] addUserCommands = new String[]{"!add", "-add", "!adduser", "-adduser"};
    private String[] removeUserCommands = new String[]{"!remove", "-remove", "!removeuser", "-removeuser"};

    public TicketSystem(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        lastInstructions = null;
        apiNotAvailable = null;

        sendInstructions(CREATION_CHANNEL.query().first());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(lastInstructions != null) lastInstructions.delete().complete();
            if(apiNotAvailable != null) apiNotAvailable.delete().complete();
        }));

        /* Web API Offline Message Thread */
        new Thread(() -> {
            while (true) {
                if(bot.getSpigotAPI().isAvailable()) {
                    if(apiNotAvailable != null) {
                        apiNotAvailable = null;
                        sendInstructions(CREATION_CHANNEL.query().first());
                    }
                } else {
                    if(apiNotAvailable == null) {
                        lastInstructions.delete().queue();
                        CustomEmbedBuilder message = new CustomEmbedBuilder("Ticket Creation Disabled")
                                .setText("The Web API is currently unavailable. Please contact staff for more info!")
                                .error();
                        apiNotAvailable = message.send(CREATION_CHANNEL.query().first());
                        if(lastInstructions != null) lastInstructions.delete().complete();
                        CustomEmbedBuilder howItWorksMessage = new CustomEmbedBuilder("How to Create a Ticket")
                                .setText("Please react with the plugin that you need help with below!");
                        lastInstructions = howItWorksMessage.send(CREATION_CHANNEL.query().first());
                        for(Plugin pl : Plugin.values()) lastInstructions.addReaction(pl.getEmoji()).queue();
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
        if(lastInstructions != null) lastInstructions.delete().complete();
        if(apiNotAvailable != null) apiNotAvailable.delete().complete();
    }

    @SubscribeEvent
    public void onGuildMessage(GuildMessageReceivedEvent e) {
        TextChannel channel = e.getChannel();
        if(e.getAuthor().isBot()) return;
        if (isTicketChat(channel)) {
            boolean isTicketCreator = channel.getTopic().contains(e.getAuthor().getAsMention());
            if(!channel.getParent().getName().contains("tech")) {
                if (isTicketCreator) {
                    channel.getManager().setParent(UNRESPONDED_TICKETS_CATEGORY.query().first()).queue();
                } else if (Util.isStaff(e.getMember())) {
                    if(channel.getParent().equals(UNRESPONDED_TICKETS_CATEGORY.query().first())) {
                        if(getMemberFromTicket(channel) != null) {
                            Message msg = channel.sendMessage(getMemberFromTicket(channel).getAsMention()).complete();
                            msg.delete().complete();
                        }
                    }
                    channel.getManager().setParent(RESPONDED_TICKETS_CATEGORY.query().first()).queue();
                } else {
                    channel.getManager().setParent(UNRESPONDED_TICKETS_CATEGORY.query().first()).queue();
                }
            }
            if (isAddUserCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if(!isTicketCreator && !Util.isStaff(e.getMember())) {
                    new CustomEmbedBuilder("Not Enough Perms")
                            .setText("You have to be the ticket creator to add someone!")
                            .success().send(channel);
                    return;
                }
                Member member = null;
                String[] args = Arrays.copyOfRange(e.getMessage().getContentDisplay().split(" "), 1, e.getMessage().getContentDisplay().split(" ").length);
                if(e.getMessage().getMentionedMembers().size() > 0) member = e.getMessage().getMentionedMembers().get(0);
                try { if(member == null) if(bot.getGuild().getMemberById(args[0]) != null) member = bot.getGuild().getMemberById(args[0]); } catch (Exception ignored) {}
                if(member == null) {
                    new CustomEmbedBuilder("Error")
                            .setText("Error finding the user!")
                            .error()
                            .success().send(channel);
                } else {
                    if(member == e.getMember() && isTicketCreator) {
                        new CustomEmbedBuilder("Error")
                                .error()
                                .setText("You cannot remove yourself from the ticket!")
                                .send(channel);
                    }
                    new CustomEmbedBuilder("Added User")
                            .success()
                            .setText("Successfully added " + member.getAsMention() + " to the ticket!")
                            .success().send(channel);
                    Collection<Permission> permissionsAllow = new ArrayList<>();
                    permissionsAllow.add(Permission.MESSAGE_ADD_REACTION);
                    permissionsAllow.add(Permission.MESSAGE_ATTACH_FILES);
                    permissionsAllow.add(Permission.MESSAGE_EMBED_LINKS);
                    permissionsAllow.add(Permission.MESSAGE_READ);
                    permissionsAllow.add(Permission.MESSAGE_WRITE);
                    permissionsAllow.add(Permission.MESSAGE_HISTORY);
                    channel.getManager().putPermissionOverride(member, permissionsAllow, new ArrayList<>()).queue();
                }
            } else if (isRemoveUserCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if (!isTicketCreator && !Util.isStaff(e.getMember())) {
                    new CustomEmbedBuilder("Not Enough Perms")
                            .setText("You have to be the ticket creator to remove someone!")
                            .success().send(channel);
                    return;
                }
                Member member = null;
                String[] args = Arrays.copyOfRange(e.getMessage().getContentDisplay().split(" "), 1, e.getMessage().getContentDisplay().split(" ").length);
                if (e.getMessage().getMentionedMembers().size() > 0) member = e.getMessage().getMentionedMembers().get(0);
                try {
                    if (member == null)
                        if (bot.getGuild().getMemberById(args[0]) != null) member = bot.getGuild().getMemberById(args[0]);
                } catch (Exception ignored) {
                }
                if (member == null) {
                    new CustomEmbedBuilder("Error")
                            .setText("Error finding the user!")
                            .error()
                            .success().send(channel);
                } else {
                    if(Util.isStaff(member)) {
                        new CustomEmbedBuilder("Error")
                                .setText("You cannot remove a Staff Member!")
                                .error()
                                .success().send(channel);
                        return;
                    }
                    boolean isTicketCreator2 = channel.getTopic().contains(member.getAsMention());
                    if(isTicketCreator2) {
                        new CustomEmbedBuilder("Error")
                                .setText("You cannot remove the Ticket Creator!")
                                .error()
                                .success().send(channel);
                        return;
                    }
                    new CustomEmbedBuilder("Removed User")
                            .success()
                            .setText("Successfully remove " + member.getAsMention() + " from the ticket!")
                            .success().send(channel);
                    Collection<Permission> permissionsDeny = new ArrayList<>();
                    permissionsDeny.add(Permission.MESSAGE_ADD_REACTION);
                    permissionsDeny.add(Permission.MESSAGE_ATTACH_FILES);
                    permissionsDeny.add(Permission.MESSAGE_EMBED_LINKS);
                    permissionsDeny.add(Permission.MESSAGE_READ);
                    permissionsDeny.add(Permission.MESSAGE_WRITE);
                    permissionsDeny.add(Permission.MESSAGE_HISTORY);
                    channel.getManager().putPermissionOverride(member, new ArrayList<>(), permissionsDeny).queue();
                }
            } else if (isCloseCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                TextChannel creationChannel = CREATION_CHANNEL.query().first();
                if (isTicketCreator) {
                    new CustomEmbedBuilder("Ticket")
                            .setText("Thank you for contacting us " + e.getAuthor().getAsMention() + "! Consider writing a review if you enjoyed the support.")
                            .send(channel);
                    channel.delete().completeAfter(20, TimeUnit.SECONDS);
                    new CustomEmbedBuilder("Solved Ticket")
                            .setText("The ticket (" + channel.getName() + ") from " + e.getAuthor().getAsMention() + " is now solved. Thanks for contacting us!")
                            .success().send(creationChannel);
                    sendInstructions(creationChannel);
                } else {
                    if(!Util.isStaff(e.getMember())) return;
                    boolean hasReason = e.getMessage().getContentDisplay().split(" ").length > 1;
                    String[] reasons = e.getMessage().getContentDisplay().split(" ");
                    String reason = String.join(" ", Arrays.copyOfRange(reasons, 1, reasons.length));
                    String reasonSend = (hasReason ? " \n \n**Reason**: " + reason : "");
                    new CustomEmbedBuilder("Ticket")
                            .setText(e.getAuthor().getAsMention() + " has closed this support ticket." + reasonSend)
                            .send(channel);
                    channel.delete().completeAfter(20, TimeUnit.SECONDS);
                    Member member = getMemberFromTicket(channel);
                    if (member != null) {
                        new CustomEmbedBuilder("Closed Ticket")
                                .setText("The ticket (" + channel.getName() + ") from " + member.getAsMention() + " has been closed!")
                                .success().send(creationChannel);
                        new CustomEmbedBuilder("Closed Ticket")
                                .setText("Your ticket (" + channel.getName() + ") has been closed!" + reasonSend)
                                .success().send(member);
                        sendInstructions(creationChannel);
                    } else {
                        new CustomEmbedBuilder("Closed Ticket")
                                .setText("The ticket (" + channel.getName() + ") from *member has left* has been closed!")
                                .success().send(creationChannel);
                        sendInstructions(creationChannel);
                    }
                }
            } else if (isToTechCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if(channel.getParent().getName().contains("tech")) {
                    if (Util.isStaff(e.getMember())) channel.getManager().setParent(UNRESPONDED_TICKETS_CATEGORY.query().first()).queue();
                } else {
                    if (Util.isStaff(e.getMember())) channel.getManager().setParent(TECH_TICKETS_CATEGORY.query().first()).queue();
                }
            } else if (isRespondedCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if (Util.isStaff(e.getMember())) channel.getManager().setParent(RESPONDED_TICKETS_CATEGORY.query().first()).queue();
            } else if (isUnRespondedCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if (Util.isStaff(e.getMember())) channel.getManager().setParent(UNRESPONDED_TICKETS_CATEGORY.query().first()).queue();
            } else if (isInProgressCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if (Util.isStaff(e.getMember())) channel.getManager().setParent(IN_PROGRESS_TICKETS_CATEGORY.query().first()).queue();
            }
        }
    }

    public boolean isCloseCommand(String msg) {
        return Arrays.stream(closeCommands).anyMatch(msg::startsWith);
    }

    public boolean isInProgressCommand(String msg) {
        return Arrays.stream(inProgressCommand).anyMatch(msg::startsWith);
    }

    public boolean isRespondedCommand(String msg) {
        return Arrays.stream(respondCommand).anyMatch(msg::startsWith);
    }

    public boolean isUnRespondedCommand(String msg) {
        return Arrays.stream(unRespondCommand).anyMatch(msg::startsWith);
    }

    public boolean isToTechCommand(String msg) {
        return Arrays.stream(toTechCommands).anyMatch(msg::startsWith);
    }

    public boolean isAddUserCommand(String msg) {
        return Arrays.stream(addUserCommands).anyMatch(msg::startsWith);
    }

    public boolean isRemoveUserCommand(String msg) {
        return Arrays.stream(removeUserCommands).anyMatch(msg::startsWith);
    }

    @SubscribeEvent
    public void onReaction(GuildMessageReactionAddEvent e) {
        if(e.getUser().isBot()) return;
        if(!e.getChannel().equals(CREATION_CHANNEL.query().first())) return;
        Member reactor = e.getMember();
        Plugin plugin = Plugin.byEmote(e.getReactionEmote().getEmote());
        if(plugin == null) return;
        if(reactor.getRoles().stream().noneMatch(r -> r.getName().contains("Verified"))) {
            e.getReaction().removeReaction().queue();
            return;
        }
        e.getReaction().removeReaction().queue();
        if(e.getMember().getUser().isBot()) return;
        if(e.getChannel() == null) return;
        TextChannel channel = e.getChannel();
        TextChannel creationChannel;
        try {
            creationChannel = CREATION_CHANNEL.query().first();
        } catch (NullPointerException ex) {
            creationChannel = channel.getGuild().getTextChannelsByName("tickets", true).get(0);
        }
        if(!channel.equals(creationChannel)) return;
        TextChannel ticketChat = getOpenTicketChat(e.getMember());
        if(ticketChat != null) {
            new CustomEmbedBuilder("Error")
                    .setText("You already have an open ticket (" + ticketChat.getAsMention() + ")").error()
                    .sendTemporary(creationChannel, 10);
            sendInstructions(e.getChannel());
            return;
        }
        List<Plugin> plugins = Plugin.fromUser(e.getMember());
        if(!plugins.contains(plugin)) {
            if(apiNotAvailable != null) return;
            new CustomEmbedBuilder("Error")
                    .setText("You don't own the plugin you selected! (" + plugin.getEmoji().getAsMention() + " " + plugin.getRoleName() + ")").error()
                    .sendTemporary(creationChannel, 10);
            sendInstructions(e.getChannel());
            return;
        }
        ticketChat = createTicketChannel(e.getMember());
        ticketChat.getManager().clearOverridesRemoved();
        ticketChat.getManager().clearOverridesAdded();
        Collection<Permission> permissionsAllow = new ArrayList<>();
        permissionsAllow.add(Permission.MESSAGE_ADD_REACTION);
        permissionsAllow.add(Permission.MESSAGE_ATTACH_FILES);
        permissionsAllow.add(Permission.MESSAGE_EMBED_LINKS);
        permissionsAllow.add(Permission.MESSAGE_READ);
        permissionsAllow.add(Permission.MESSAGE_WRITE);
        permissionsAllow.add(Permission.MESSAGE_HISTORY);
        ticketChat.getManager()
                .putPermissionOverride(STAFF_ROLE.query().first(), permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(e.getMember(), permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(bot.getGuild().getPublicRole(), new ArrayList<>(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                .complete();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Plugin p : plugins) {
            if (i != 0) sb.append(" ");
            sb.append(p.getEmoji().getAsMention());
            i++;
        }
        if(i == 0) sb.append("None");
        new CustomEmbedBuilder(reactor.getEffectiveName() + " - " + reactor.getUser().getId())
                .setText("Please describe your issue below.")
                .addField("Plugin", plugin.getEmoji().getAsMention(), true)
                //Maybe Soon: .addField("Plugin Version (Latest)", plugin.getLatestUpdate(), true)
                .addField("Owned Plugins", sb.toString(), true)
                .send(ticketChat);
        new CustomEmbedBuilder("New Ticket")
                .setText(reactor.getAsMention() + " created a new ticket (" + ticketChat.getAsMention() + ")")
                .send(creationChannel);
        sendInstructions(creationChannel);
    }

    public void sendInstructions(TextChannel textChannel) {
        if(lastInstructions != null) lastInstructions.delete().complete();
        if(apiNotAvailable != null) {
            apiNotAvailable.delete().complete();
            CustomEmbedBuilder message = new CustomEmbedBuilder("Ticket Creation Disabled")
                    .setText("The Web API is currently unavailable. Please contact staff for more info!\n\n**Note:** If you bought any of Techs plugin's from Songoda, you\nshould still be able to create a ticket!")
                    .error();
            apiNotAvailable = message.send(CREATION_CHANNEL.query().first());
        }
        CustomEmbedBuilder howItWorksMessage = new CustomEmbedBuilder("How to Create a Ticket")
                .setText("Please react with the plugin that you need help with below!");
        lastInstructions = howItWorksMessage.send(textChannel);
        for(Plugin pl : Plugin.values()) lastInstructions.addReaction(pl.getEmoji()).queue();
    }

    public boolean isTicketChat(TextChannel channel) {
        return channel.getName().contains("ticket-");
    }

    public TextChannel createTicketChannel(Member member) {
        String name = "ticket-" + member.getEffectiveName().replaceAll("[^a-zA-Z\\d\\s_-]", "").toLowerCase();
        if(name.equals("ticket-")) name = "ticket-" + member.getUser().getId();
        return (TextChannel) bot.getGuild().getController().createTextChannel(name)
                .setParent(UNRESPONDED_TICKETS_CATEGORY.query().first())
                .setTopic("Ticket created by " + member.getAsMention() + " | Problem Solved? Please type in !solved")
                .complete();
    }

    public TextChannel getOpenTicketChat(Member member) {
        for(TextChannel channel : bot.getGuild().getTextChannels()) {
            if(isTicketChat(channel)) {
                String topic = channel.getTopic();
                if(topic != null) {
                    if (topic.contains(member.getAsMention())) {
                        return channel;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "Ticket System";
    }

    public Member getMemberFromTicket(TextChannel channel) {
        String id = channel.getTopic().split("<")[1].split(">")[0].replace("@", "");
        return channel.getGuild().getMemberById(id);
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(CREATION_CHANNEL, 1, "Missing Creation Channel (#tickets)"),
                new Requirement(TICKET_CATEGORY, 1, "Missing Tickets Category (tickets)"),
                new Requirement(UNRESPONDED_TICKETS_CATEGORY, 1, "Missing Tickets Category (unresponded-tickets)"),
                new Requirement(RESPONDED_TICKETS_CATEGORY, 1, "Missing Tickets Category (responded-tickets)"),
                new Requirement(TECH_TICKETS_CATEGORY, 1, "Missing Tickets Category (tech-tickets)"),
                new Requirement(IN_PROGRESS_TICKETS_CATEGORY, 1, "Missing Tickets Category (in-progress-tickets)"),
                new Requirement(STAFF_ROLE, 1, "Missing 'Staff' Role")
        };
    }
}
