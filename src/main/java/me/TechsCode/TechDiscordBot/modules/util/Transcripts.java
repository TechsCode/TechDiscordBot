package me.TechsCode.TechDiscordBot.modules.util;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import org.jsoup.Jsoup;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static j2html.TagCreator.*;

public class Transcripts {

    private static final DefinedQuery<TextChannel> TRANSCRIPTS_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return TechDiscordBot.getBot().getChannels("transcripts"); }
    };

    public static String createTranscript(Member member, TextChannel channel) {
        List<Message> messages = channel.getHistory().retrievePast(100).complete();
        Collections.reverse(messages);
        StringBuilder html = new StringBuilder();
        html.append("<body>");
        String info = div(
                div(
                    img().withClass("info_guild-icon").withSrc(channel.getGuild().getIconUrl())
                ).withClass("info_guild-icon-container"),
                div(
                    div().withClass("info_guild-name").withText(channel.getGuild().getName()),
                        div().withClass("info_channel-name").withText("#" + channel.getName()),
                        div().withClass("info_channel-message-count").withText(messages.size() + " message" + (messages.size() == 1 ? "" : "s"))
                ).withClass("info_metadata")
        ).withClass("info").render();
        html.append(info);
        html.append("<div class=\"chatlog\">");
        Member lastMember = null;
        int i = 0;
        for(Message message : messages) {
            boolean isLastMember = lastMember == message.getMember();
            lastMember = message.getMember();
            if(isLastMember) {
                html.append("<div class=\"chatlog_message\">");
            } else {
                //if(i != 0) html.append("</div>");
                html.append("<div class=\"chatlog_message-group\">");
            }
            List<MessageEmbed> embeds = message.getEmbeds();
            if(embeds.size() == 0) {
                if(!isLastMember) {
                    if(i != 1) html.append("");
                    html.append("<div class=chatlog_author-avatar-container><img class=\"chatlog_author-avatar\" src=\"");
                } else {

                }
                if(!isLastMember) html.append("<div class=chatlog_author-avatar-container><img class=\"chatlog_author-avatar\" src=\"")
                        .append(message.getAuthor().getAvatarUrl())
                        .append("\"></div>")
                        .append("<div class=\"chatlog_messages\"><span class=\"chatlog_author-name\" title=\"")
                        .append(message.getAuthor().getName())
                        .append("#")
                        .append(message.getAuthor().getDiscriminator())
                        .append("\">").append(message.getAuthor().getName());
                if(message.getAuthor().isBot()) html.append("<span class=\"chatlog_bot-tag\">BOT</span>");
                html.append("</span><span class=\"chatlog_timestamp\">")
                        .append(getDate(message.getCreationTime()))
                        .append("</span>")
                        .append("<div class=\"chatlog_message\">").append("<div class=\"chatlog_content\"><span class=\"markdown\">")
                        .append(message.getContentDisplay()).append("</span>");
                if(message.getReactions().size() > 0) {
                    html.append("<div class=\"chatlog_reactions\">");
                    for (MessageReaction r : message.getReactions()) {
                        if(r == null) continue;
                        try {
                            html.append("<div class=\"chatlog_reaction\">" + "<img class=\"emoji-small\" src=\"")
                                    .append(r.getReactionEmote().getEmote().getImageUrl())
                                    .append("\">")
                                    .append("<span class=\"chatlog_reaction-count\">")
                                    .append(r.getCount())
                                    .append("</span></div>");
                        } catch (NullPointerException ex) {
                            html.append("<div class=\"chatlog_reaction\">")
                                    .append(r.getReactionEmote().getName())
                                    .append("<span class=\"chatlog_reaction-count\">")
                                    .append(r.getCount())
                                    .append("</span></div>");
                        }
                    }
                    html.append("</div>");
                }
                html.append("</div></div></div></div>");
            } else {
                if(!isLastMember) html.append("<div class=chatlog_author-avatar-container><img class=\"chatlog_author-avatar\" src=\"")
                        .append(message.getAuthor().getAvatarUrl())
                        .append("\"></div>")
                        .append("<div class=\"chatlog_messages\"><span class=\"chatlog_author-name\" title=\"")
                        .append(message.getAuthor().getName())
                        .append("#")
                        .append(message.getAuthor().getDiscriminator())
                        .append("\">").append(message.getAuthor().getName());
                if(message.getAuthor().isBot()) html.append("<span class=\"chatlog_bot-tag\">BOT</span>");
                html.append("</span><span class=\"chatlog_timestamp\">")
                        .append(getDate(message.getCreationTime()))
                        .append("</span>")
                        .append("<div class=\"chatlog_message\">").append("<div class=\"chatlog_content\"><span class=\"markdown\">")
                        .append(message.getContentDisplay()).append("</span>");
                for(MessageEmbed embed : embeds) {
                    html.append("<div class=\"chatlog_embed\"><div class=\"chatlog_embed-color-pill\" style=\"")
                            .append(embed.getColor() != null ? "background-color: " + hexFromColor(embed.getColor()) : "")
                            .append(";\"></div><div class=\"chatlog_embed-content-container\"><div class=\"chatlog_embed-content\"><div class=\"chatlog_embed-text\">");
                    if(embed.getAuthor() != null && (embed.getAuthor().getName() != null)) {
                        if(embed.getAuthor().getUrl() != null && !embed.getAuthor().getUrl().isEmpty()) {
                            html.append("<div class=\"chatlog_embed-author\">");
                            if (embed.getAuthor().getIconUrl() != null)
                                html.append("<img class=\"chatlog_embed-author-icon\" src=\"")
                                        .append(embed.getAuthor().getIconUrl())
                                        .append("\">");
                            html.append("<span class=\"chatlog_embed-author-name\"><a href=\"")
                                    .append(embed.getAuthor().getUrl()).append("\">")
                                    .append(embed.getAuthor().getName())
                                    .append("</a></span>");
                        } else {
                            html.append("<div class=\"chatlog_embed-author\">");
                            if(embed.getAuthor().getIconUrl() != null) html.append("<img class=\"chatlog_embed-author-icon\" src=\"")
                                    .append(embed.getAuthor().getIconUrl())
                                    .append("\">");
                            html.append("<span class=\"chatlog_embed-author-name\">")
                                    .append(embed.getAuthor().getName())
                                    .append("</span>");
                        }
                        html.append("</div>");
                    }
                    if(embed.getTitle() != null && !embed.getTitle().isEmpty()) {
                        html.append("<div class=\"chatlog_embed-title\">")
                                .append(embed.getTitle())
                                .append("</div>");
                    }
                    html.append("<div class=\"chatlog_embed-description\"><span class=\"markdown\">")
                            .append(embed.getDescription() == null ? "" : embed.getDescription())
                            .append("</span></div>")
                            .append("<div class=\"chatlog_embed-fields\">");
                    if(embed.getFields().size() > 0) {
                        for(MessageEmbed.Field f : embed.getFields()) {
                            html.append("<div class=\"chatlog_embed-field").append(f.isInline() ? "-inline" : "").append("\">");
                            html.append("<div class=\"chatlog_embed-field-name\">")
                                    .append(f.getName()).append("</div>");
                            html.append("<div class=\"chatlog_embed-field-value\">")
                                    .append(f.getValue()).append("</div>");
                            html.append("</div>");
                        }
                    }
                    html.append("</div></div><div class=\"chatlog_embed-thumbnail-container\">");
                    if(embed.getThumbnail() != null) html.append("<a class=\"chatlog_embed-thumbnail-link\"><img class=\"chatlog_embed-thumbnail\" src=\"").append(embed.getThumbnail().getUrl()).append("\"></a>");
                    html.append("</div></div><div class=\"chatlog_embed-footer\">");
                    if(embed.getFooter() != null && embed.getFooter().getIconUrl() != null) html.append("<img class=\"chatlog_embed-footer-icon\" src=\"")
                            .append(embed.getFooter().getIconUrl())
                            .append("\">");
                    if(embed.getFooter() != null && embed.getFooter().getText() != null) html.append("<span class=\"chatlog_embed-footer-text\">")
                            .append(embed.getFooter().getText())
                            .append("</span>");
                    html.append("</div></div></div>");
                    if(message.getReactions().size() > 0) {
                        html.append("<div class=\"chatlog_reactions\">");
                        for (MessageReaction r : message.getReactions()) {
                            if(r == null) continue;
                            try {
                                html.append("<div class=\"chatlog_reaction\">" + "<img class=\"emoji-small\" src=\"")
                                        .append(r.getReactionEmote().getEmote().getImageUrl())
                                        .append("\">")
                                        .append("<span class=\"chatlog_reaction-count\">")
                                        .append(r.getCount())
                                        .append("</span></div>");
                            } catch (NullPointerException ex) {
                                html.append("<div class=\"chatlog_reaction\">")
                                        .append(r.getReactionEmote().getName())
                                        .append("<span class=\"chatlog_reaction-count\">")
                                        .append(r.getCount())
                                        .append("</span></div>");
                            }
                        }
                        html.append("</div>");
                    }
                }
                html.append("</div></div></div></div>");
            }
            i++;
            if(isLastMember) html.append("</div>");
        }
        html.append("</div>");
        html.append("</body>");
        String finished = prettyPrintHTML(html.toString());
        String pass = TechDiscordBot.getBot().getStorage().uploadTranscript(channel, finished);
        new CustomEmbedBuilder("New Transcript")
                .setText(member.getAsMention() + "'s Ticket Transcript.\n\nhttp://tickets.techsco.de/" + channel.getId() + "/" + pass)
                .send(TRANSCRIPTS_CHANNEL.query().first());
        return html.toString();
    }

    public static String prettyPrintHTML(String html) { return Jsoup.parseBodyFragment(html).body().html(); }

    public static String hexFromColor(Color color) { return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()); }

    public static String getDate(OffsetDateTime date) { return date.format(DateTimeFormatter.ofPattern("MMMM, dd yyyy hh:mm a")); }
}
