package me.TechsCode.TechDiscordBot.tickets;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TranscriptMessage {

    private EmbedChatMessage embedChatMessage;
    private ChatMessage chatMessage;

    public TranscriptMessage(Message message) {
        String avatarURL = message.getAuthor().getAvatarUrl() != null ? message.getAuthor().getAvatarUrl() : "https://i.imgur.com/nnegGEV.png";
        if(message.getEmbeds().size() == 0) {
            this.chatMessage = new ChatMessage(message.getAuthor().getName(), message.getContentDisplay(), getDate(message.getTimeCreated()), avatarURL);
        } else {
            MessageEmbed embed = message.getEmbeds().get(0);
            MessageEmbed.AuthorInfo authorInfo = embed.getAuthor();
            Color color = embed.getColor() != null ? embed.getColor() : new Color(91, 107, 113);
            this.embedChatMessage = new EmbedChatMessage(message.getAuthor().getName(), (authorInfo != null && authorInfo.getName() != null ? authorInfo.getName() : ""), (embed.getDescription() == null ? "" : embed.getDescription()), hexFromColor(color), avatarURL, getDate(message.getTimeCreated()));
        }
    }

    private String hexFromColor(Color color) { return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()); }

    private String getDate(OffsetDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a").withZone(ZoneOffset.UTC)) + " UTC";
    }

    public boolean isEmbed() {
        return this.embedChatMessage != null && this.chatMessage == null;
    }

    public boolean isNormal() {
        return !this.isEmbed();
    }

    public EmbedChatMessage getEmbedChatMessage() {
        return embedChatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
