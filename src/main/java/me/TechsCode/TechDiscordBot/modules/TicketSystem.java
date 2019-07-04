package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.requirements.CategoryRequirement;
import me.TechsCode.TechDiscordBot.requirements.ChannelRequirement;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TicketSystem extends Module {

    private Category category;
    private TextChannel textChannel;

    private Message lastInstructions;

    private String[] closeCommands = new String[]{"!solved", "!close", "-close"};

    public TicketSystem(TechDiscordBot bot) {
        super(bot);
    }

    @SubscribeEvent
    public void closeCommand(MessageReceivedEvent e){
        TextChannel channel = (TextChannel) e.getChannel();

        if(isTicketChat(channel)) {
            if(ArrayUtils.contains(closeCommands, e.getMessage().getContentDisplay().toLowerCase())){
                e.getMessage().delete().submit();

                boolean closedByUser = channel.getTopic().contains(e.getAuthor().getAsMention());

                new CustomEmbedBuilder("Ticket")
                        .setText(closedByUser ? "Thank you for contacting us "+e.getAuthor().getAsMention()+"! Consider writing a review if you enjoyed the support" : e.getAuthor().getAsMention()+" has closed this support ticket")
                        .send(channel);

                channel.delete().completeAfter(20, TimeUnit.SECONDS);

                if(closedByUser){
                    new CustomEmbedBuilder(closedByUser ? "Solved Ticket" : "Closed Ticket")
                            .setText("The ticket ("+channel.getName()+") from "+e.getAuthor().getAsMention()+" is now solved. Thanks for contacting us")
                            .success().send(textChannel);

                    sendInstructions();
                }
            }
        }
    }

    @SubscribeEvent
    public void createChannel(MessageReceivedEvent e) {
        TextChannel channel = (TextChannel) e.getChannel();

        if(e.getMember().getUser().isBot()) return;

        if(!channel.equals(textChannel)) return;

        TextChannel ticketChat = getOpenTicketChat(e.getMember());

        e.getMessage().delete().complete();

        if(ticketChat != null){
            new CustomEmbedBuilder("Error")
                    .setText("You already have an open ticket ("+ticketChat.getAsMention()+")").error()
                    .sendTemporary(textChannel, 10);

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

        Role supporter = bot.getRole("Supporter");

        ticketChat.getManager()
                .putPermissionOverride(supporter, permissionsAllow, Arrays.asList(Permission.MESSAGE_TTS))
                .putPermissionOverride(e.getMember(), permissionsAllow, Arrays.asList(Permission.MESSAGE_TTS))
                .putPermissionOverride(bot.getGuild().getPublicRole(), new ArrayList<>(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                .complete();

        new CustomEmbedBuilder("Ticket Info")
                .setText(e.getMessage().getContentDisplay())
                .setFooter("Ticket created by "+e.getAuthor().getName())
                .send(ticketChat);

        new CustomEmbedBuilder("New Ticket")
                .setText(e.getAuthor().getAsMention()+" created a new ticket ("+ticketChat.getAsMention()+")")
                .send(textChannel);
    }

    public void sendInstructions(){
        if(lastInstructions != null){
            lastInstructions.delete().complete();
        }

        CustomEmbedBuilder howItWorksMessage = new CustomEmbedBuilder("How to create a ticket")
                .setText("You want to receive direct support from us? \nType in your question or issue below and we will get to you as soon as possible!");

        lastInstructions = howItWorksMessage.send(textChannel);
    }

    public boolean isTicketChat(TextChannel channel) {
        return channel.getName().contains("ticket-");
    }

    public TextChannel createTicketChannel(Member member) {
        String name = "ticket-"+ UUID.randomUUID().toString().split("-")[0];

        return (TextChannel) bot.getGuild().getController().createTextChannel(name)
                .setParent(category)
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
        category = bot.getCategory("Tickets");
        textChannel = bot.getChannel("tickets");

        sendInstructions();
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
                new CategoryRequirement("Tickets"),
                new ChannelRequirement("Tickets")
        };
    }
}
