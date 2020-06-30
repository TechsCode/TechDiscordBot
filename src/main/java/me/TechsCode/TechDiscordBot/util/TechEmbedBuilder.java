package me.TechsCode.TechDiscordBot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TechEmbedBuilder extends EmbedBuilder {

    public TechEmbedBuilder() {
        setColor(new Color(81, 153, 226));
    }

    public TechEmbedBuilder(String title) {
        if(title != null) setAuthor(title, "http://techsco.de", "https://i.imgur.com/nnegGEV.png");
        setColor(new Color(81, 153, 226));
        setFooter("Developed by Tech & Team");
    }

    public TechEmbedBuilder(String title, boolean footer) {
        if(title != null) setAuthor(title, "http://techsco.de", "https://i.imgur.com/nnegGEV.png");
        setColor(new Color(81, 153, 226));
        if(footer) setFooter("Developed by Tech & Team");
    }

    public TechEmbedBuilder(boolean footer) {
        setColor(new Color(81, 153, 226));
        if(footer) setFooter("Developed by Tech & Team");
    }

    public TechEmbedBuilder setFooter(String text) {
        setFooter("Tech's Plugin Support • " + text, "https://i.imgur.com/nzfiUTy.png");
        return this;
    }

    public TechEmbedBuilder error() {
        setColor(new Color(178,34,34));
        return this;
    }

    public TechEmbedBuilder success() {
        setColor(new Color(50, 205, 50));
        return this;
    }

    public TechEmbedBuilder setText(String text) {
        setDescription(text);
        return this;
    }

    public TechEmbedBuilder setText(String... text) {
        setDescription(String.join("\n", text));
        return this;
    }

    public Message send(TextChannel textChannel) { return textChannel.sendMessage(build()).complete(); }

    public Message sendAfter(TextChannel textChannel, TimeUnit unit, int amount) { return textChannel.sendMessage(build()).completeAfter(amount, unit); }

    public Message send(User user) {
        try {
            return user.openPrivateChannel().complete().sendMessage(build()).complete();
        } catch (ErrorResponseException ignore) {} //Ignore if user doesn't have DMs open.
        return null;
    }

    public Message send(Member member) {
        return send(member.getUser());
    }

    public void sendTemporary(TextChannel textChannel, int duration, TimeUnit timeUnit) {
        Message message = send(textChannel);
        message.delete().submitAfter(duration, timeUnit);
    }

    public ScheduledFuture<?> sendAfter(TextChannel textChannel, int duration, Consumer<Message> onSuccess) {
        return textChannel.sendMessage(build()).queueAfter(duration, TimeUnit.SECONDS, onSuccess);
    }

    public ScheduledFuture<?> sendAfter(TextChannel textChannel, int duration, TimeUnit timeUnit, Consumer<Message> onSuccess) {
        return textChannel.sendMessage(build()).queueAfter(duration, timeUnit, onSuccess);
    }

    public void sendTemporary(TextChannel textChannel, int duration) {
        sendTemporary(textChannel, duration, TimeUnit.SECONDS);
    }

    @Override
    public TechEmbedBuilder setThumbnail(String url) {
        super.setThumbnail(url);
        return this;
    }

    @Override
    public TechEmbedBuilder setColor(Color color) {
        super.setColor(color);
        return this;
    }

    @Override
    public TechEmbedBuilder setImage(String url) {
        super.setImage(url);
        return this;
    }

    @Override
    public TechEmbedBuilder addField(String name, String value, boolean inline) {
        super.addField(name, value, inline);
        return this;
    }

    @Override
    public TechEmbedBuilder addBlankField(boolean inline) {
        super.addBlankField(inline);
        return this;
    }
}