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
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    private Message lastInstructions;

    private String[] closeCommands = new String[]{"!solved", "!close", "-close", "-solved"};
    private String[] respondCommand = new String[]{"-r","-respond","@r","!respond"};
    private String[] inProgressCommand = new String[]{"-i","!i"};
    private String[] toTechCommands = new String[]{"!tech", "-tech", "!t", "-t"};

    public TicketSystem(TechDiscordBot bot) {
        super(bot);
    }

    @SubscribeEvent
    public void commands(GuildMessageReceivedEvent e) {
        TextChannel channel = e.getChannel();

        if (isTicketChat(channel)) {
            if (isCloseCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();

                boolean closedByUser = channel.getTopic().contains(e.getAuthor().getAsMention());

                TextChannel creationChannel = CREATION_CHANNEL.query().first();

                if (closedByUser) {
                    new CustomEmbedBuilder("Ticket")
                            .setText("Thank you for contacting us " + e.getAuthor().getAsMention() + "! Consider writing a review if you enjoyed the support.")
                            .send(channel);

                    channel.delete().completeAfter(20, TimeUnit.SECONDS);
                    new CustomEmbedBuilder("Solved Ticket")
                            .setText("The ticket (" + channel.getName() + ") from " + e.getAuthor().getAsMention() + " is now solved. Thanks for contacting us!")
                            .success().send(creationChannel);
                    sendInstructions(creationChannel);
                } else {
                    boolean hasReason = e.getMessage().getContentDisplay().split(" ").length > 1;
                    String[] reasons = e.getMessage().getContentDisplay().split(" ");
                    String reason = String.join(" ", Arrays.copyOfRange(reasons, 1, reasons.length));
                    String reasonSend = (hasReason ? " \n \n**Reason**: " + reason : "");
                    new CustomEmbedBuilder("Ticket")
                            .setText(e.getAuthor().getAsMention() + " has closed this support ticket." + reasonSend)
                            .send(channel);
                    channel.delete().completeAfter(20, TimeUnit.SECONDS);
                    String id = channel.getTopic().split("<")[1].split(">")[0].replace("@", "");
                    Member member = channel.getGuild().getMemberById(id);
                    if (member != null) {
                        new CustomEmbedBuilder("Closed Ticket")
                                .setText("The ticket (" + channel.getName() + ") from " + member.getAsMention() + " has been closed!")
                                .success().send(creationChannel);
                        sendInstructions(creationChannel);
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
                if (Util.isStaff(e.getMember()))
                    channel.getManager().setParent(TECH_TICKETS_CATEGORY.query().first()).queue();
            } else if (isRespondedCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if (Util.isStaff(e.getMember())) channel.getManager().setParent(RESPONDED_TICKETS_CATEGORY.query().first()).queue();
            } else if (isInProgressCommand(e.getMessage().getContentDisplay().toLowerCase())) {
                e.getMessage().delete().submit();
                if (Util.isStaff(e.getMember()))
                    channel.getManager().setParent(IN_PROGRESS_TICKETS_CATEGORY.query().first()).queue();
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

    public boolean isToTechCommand(String msg) {
        return Arrays.stream(toTechCommands).anyMatch(msg::startsWith);
    }

    @SubscribeEvent
    public void createChannel(GuildMessageReceivedEvent e) {
        if(e.getMember().getUser().isBot()) return;

        TextChannel channel = e.getChannel();
        TextChannel creationChannel = CREATION_CHANNEL.query().first();

        if(!channel.equals(creationChannel)) return;

        TextChannel ticketChat = getOpenTicketChat(e.getMember());

        e.getMessage().delete().complete();

        if(ticketChat != null){
            new CustomEmbedBuilder("Error")
                    .setText("You already have an open ticket ("+ticketChat.getAsMention()+")").error()
                    .sendTemporary(creationChannel, 10);

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
                .putPermissionOverride(STAFF_ROLE.query().first(), permissionsAllow, Arrays.asList(Permission.MESSAGE_TTS))
                .putPermissionOverride(e.getMember(), permissionsAllow, Arrays.asList(Permission.MESSAGE_TTS))
                .putPermissionOverride(bot.getGuild().getPublicRole(), new ArrayList<>(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                .complete();

        new CustomEmbedBuilder("Ticket Info")
                .setText(e.getMessage().getContentDisplay())
                .setFooter("Ticket created by "+e.getAuthor().getName())
                .send(ticketChat);

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(Plugin p : Plugin.fromUser(e.getMember())) {
            if(i != 0) sb.append(" ");
            sb.append(p.getEmoji().getAsMention());
            i++;
        }

        new CustomEmbedBuilder(false)
                .setText(sb.toString())
                .send(ticketChat);

        new CustomEmbedBuilder("New Ticket")
                .setText(e.getAuthor().getAsMention()+" created a new ticket ("+ticketChat.getAsMention()+")")
                .send(creationChannel);
    }

    public void sendInstructions(TextChannel textChannel) {
        if(lastInstructions != null) {
            lastInstructions.delete().complete();
        }

        CustomEmbedBuilder howItWorksMessage = new CustomEmbedBuilder("How to Create a Ticket")
                .setText("You want to receive direct support from us? \nType in your question or issue below and we will get to you as soon as possible!");

        lastInstructions = howItWorksMessage.send(textChannel);
    }

    public boolean isTicketChat(TextChannel channel) {
        return channel.getName().contains("ticket-");
    }

    public TextChannel createTicketChannel(Member member) {
        String name = "ticket-" + member.getUser().getName().toLowerCase().substring(0, Math.min(member.getUser().getName().toLowerCase().length(), 10));

        return (TextChannel) bot.getGuild().getController().createTextChannel(name)
                .setParent(TICKET_CATEGORY.query().first())
                .setTopic("Ticket from " + member.getAsMention() + " | Problem Solved? Please type in !solved")
                .complete();
    }

    public TextChannel getOpenTicketChat(Member member) {
        for(TextChannel channel : bot.getGuild().getTextChannels()) {
            if(isTicketChat(channel)){
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
    public void onEnable() {
        sendInstructions(CREATION_CHANNEL.query().first());
    }

    @Override
    public void onDisable() {
        if(lastInstructions != null){
            lastInstructions.delete().complete();
        }
    }

    @Override
    public String getName() {
        return "Ticket System";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(CREATION_CHANNEL, 1, "Missing Creation Channel (#tickets)"),
                new Requirement(TICKET_CATEGORY, 1, "Missing Tickets Category (tickets)"),
                new Requirement(UNRESPONDED_TICKETS_CATEGORY, 1, "Missing Tickets Category (unresponded-tickets)"),
                //new Requirement(RESPONDED_TICKETS_CATEGORY, 1, "Missing Tickets Category (responded-tickets)"),
                new Requirement(TECH_TICKETS_CATEGORY, 1, "Missing Tickets Category (tech-tickets)"),
                //new Requirement(IN_PROGRESS_TICKETS_CATEGORY, 1, "Missing Tickets Category (in-progress-tickets)"),
                new Requirement(STAFF_ROLE, 1, "Missing 'Staff' Role")
        };
    }
}