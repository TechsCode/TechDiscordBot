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
        color(new Color(81, 153, 226));
    }

    public TechEmbedBuilder(String title) {
        if(title != null)
            setAuthor(title, "https://techscode.com", "https://i.imgur.com/nnegGEV.png");

        color(new Color(81, 153, 226));
        footer("Developed by Tech & Team");
    }

    public TechEmbedBuilder(String title, boolean footer) {
        if(title != null)
            setAuthor(title, "https://techscode.com", "https://i.imgur.com/nnegGEV.png");
        if(footer)
            footer("Developed by Tech & Team");

        color(new Color(81, 153, 226));
    }

    public TechEmbedBuilder(boolean footer) {
        if(footer)
            footer("Developed by Tech & Team");

        color(new Color(81, 153, 226));
    }

    public TechEmbedBuilder footer(String text) {
        setFooter("Tech's Plugin Support • " + text, "https://i.imgur.com/nzfiUTy.png");

        return this;
    }

    public TechEmbedBuilder error() {
        color(new Color(178,34,34));

        return this;
    }

    public TechEmbedBuilder success() {
        color(new Color(50, 205, 50));

        return this;
    }

    public TechEmbedBuilder text(String text) {
        setDescription(text);

        return this;
    }

    public TechEmbedBuilder text(String... text) {
        setDescription(String.join("\n", text));

        return this;
    }

    public TechEmbedBuilder thumbnail(String url) {
        super.setThumbnail(url);

        return this;
    }

    public TechEmbedBuilder color(Color color) {
        if(color == null)
            return this;

        super.setColor(color);
        return this;
    }

    public TechEmbedBuilder image(String url) {
        super.setImage(url);

        return this;
    }

    public TechEmbedBuilder field(String name, String value, boolean inline) {
        super.addField(name, value, inline);

        return this;
    }

    public TechEmbedBuilder blankField(boolean inline) {
        super.addBlankField(inline);

        return this;
    }

    public String getText() {
        return super.getDescriptionBuilder().toString();
    }

    @Deprecated
    // Try not to use this as it sleeps the bot basically.
    public Message complete(TextChannel textChannel) {
        return textChannel.sendMessage(build()).complete();
    }

    @Deprecated
    // Try not to use this as it sleeps the bot basically.
    public Message complete(Member member) {
        return complete(member.getUser());
    }

    public Message complete(User user) {
        try {
            return user.openPrivateChannel().complete().sendMessage(build()).complete();
        } catch (ErrorResponseException ignore) { }
        return null;
    }

    public void queue(TextChannel textChannel) {
        textChannel.sendMessageEmbeds(build()).queue();
    }

    public void queue(Member member) {
        queue(member.getUser());
    }

    public void queue(Member member, Consumer<Message> consumer) {
        queue(member.getUser(), consumer);
    }

    public void queue(User user) {
        try {
            user.openPrivateChannel().queue(c -> c.sendMessageEmbeds(build()).queue());
        } catch (Exception ignore) { }
    }

    public void queue(User user, Consumer<Message> consumer) {
        try {
            user.openPrivateChannel().queue(c -> c.sendMessageEmbeds(build()).queue(consumer));
        } catch (Exception ignore) { }
    }

    public void queue(TextChannel textChannel, Consumer<Message> consumer) {
        textChannel.sendMessageEmbeds(build()).queue(consumer);
    }

    public void queueAfter(TextChannel textChannel, int delay, TimeUnit unit) {
        textChannel.sendMessageEmbeds(build()).queueAfter(delay, unit);
    }

    public void queueAfter(TextChannel textChannel, int delay, TimeUnit unit, Consumer<Message> success) {
        textChannel.sendMessageEmbeds(build()).queueAfter(delay, unit, success);
    }

    public void queueAfter(User user, int delay, TimeUnit time) {
        try {
            user.openPrivateChannel().complete().sendMessageEmbeds(build()).queueAfter(delay, time);
        } catch (ErrorResponseException ignore) { }
    }

    public Message reply(Message message) {
        return reply(message, true);
    }

    public Message reply(Message message, boolean mention) {
        return message.reply(build()).mentionRepliedUser(mention).complete();
    }

    public void replyTemporary(Message message, int duration, TimeUnit timeUnit) {
        replyTemporary(message, true, duration, timeUnit);
    }

    public void replyTemporary(Message message, boolean mention, int duration, TimeUnit timeUnit) {
        message.replyEmbeds(build()).mentionRepliedUser(mention).queue((msg -> msg.delete().submitAfter(duration, timeUnit)));
    }

    public void sendTemporary(TextChannel textChannel, int duration, TimeUnit timeUnit) {
        queue(textChannel, (msg) -> msg.delete().submitAfter(duration, timeUnit));
    }

    public void sendTemporary(TextChannel textChannel, int duration) {
        sendTemporary(textChannel, duration, TimeUnit.SECONDS);
    }

    public ScheduledFuture<?> sendAfter(TextChannel textChannel, int duration, Consumer<Message> onSuccess) {
        return textChannel.sendMessageEmbeds(build()).queueAfter(duration, TimeUnit.SECONDS, onSuccess);
    }

    public ScheduledFuture<?> sendAfter(TextChannel textChannel, int duration, TimeUnit timeUnit, Consumer<Message> onSuccess) {
        return textChannel.sendMessageEmbeds(build()).queueAfter(duration, timeUnit, onSuccess);
    }
}
