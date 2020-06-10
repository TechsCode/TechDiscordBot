package me.TechsCode.TechDiscordBot.tickets;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.storage.Transcript;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.PasswordGenerator;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jsoup.helper.Validate;

import java.util.Collections;
import java.util.List;

public class TranscriptBuilder {

    public static class Builder {

        private String html, password,  channelId;
        private Member member;

        private static final DefinedQuery<TextChannel> TRANSCRIPTS_CHANNEL = new DefinedQuery<TextChannel>() {
            @Override
            protected Query<TextChannel> newQuery() { return TechDiscordBot.getBot().getChannels("transcripts"); }
        };

        public Builder() {}

        public Builder channel(TextChannel channel) {
            Validate.notNull(channel, "Channel cannot be null!");
            this.channelId = channel.getId();
            String channelName = channel.getName();
            List<Message> messages = channel.getHistory().retrievePast(100).complete();
            Collections.reverse(messages);
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>").append("<html class=\"full-motion theme-dark platform-web\" data-rh=\"lang,style,class\" style=\"font-size: 100%; display: inline;\" lang=\"en-US\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, shrink-to-fit=no\"><title>#%CHANNEL_NAME% - {GUILD_NAME}</title><link rel=\"stylesheet\" href=\"/assets/bootstrap/css/bootstrap.min.css\"><link rel=\"stylesheet\" href=\"/css/Navigation-Clean.css\"><link rel=\"stylesheet\" href=\"/css/wtg-alert-1.css\"><link rel=\"stylesheet\" href=\"/assets/wtg-alert.css\"></head>");
            html.append("<body>").append("<nav class=\"navbar navbar-light navbar-expand-md navigation-clean\"><div class=\"container\"><h1 class=\"channel-name\">#%CHANNEL_NAME%</h1></div></nav>");
            for(Message message : messages) {
                html.append("<div class=\"container\">");
                TranscriptMessage msg = new TranscriptMessage(message);
                if(msg.isNormal()) {
                    ChatMessage chatMessage = msg.getChatMessage();
                    String data = "<div class=\"message\" style=\"border-left-color: #07b7ae!important; border-left: 0px solid; border-radius: 5px\"> <div class=\"col\" style=\"margin-top: 17px;\"> <strong style=\"margin-left: 90px;font-size: 20px;font-family: Verdana;\">{USERNAME}</strong><span style=\"color: rgb(173,173,173);margin-left: 5px;font-size: 16px;font-family: Verdana;\">{TIMESTAMP}</span></div><div class=\"col\"><img class=\"rounded-circle img-fluid bg-white border rounded border-white shadow-lg\" src=\"{AVATAR}\" width=\"60\" height=\"80\" style=\"margin-top: 0px;margin-left: 14px;\"><span class=\"text-break d-inline\" style=\"margin-left: 15px;font-size: 18px;color: rgb(225,225,225);\">{MESSAGE}</span></div></div>";
                    data = data.replace("{USERNAME}", chatMessage.getFrom()).replace("{TIMESTAMP}", chatMessage.getTimestamp()).replace("{AVATAR}", chatMessage.getAvatarURL()).replace("{MESSAGE}", chatMessage.getContent());
                    html.append(data);
                } else {
                    EmbedChatMessage chatMessage = msg.getEmbedChatMessage();
                    String data = "<div class=\"message\" style=\"border-left-color: {COLOR}!important; border-left: 10px solid; border-radius: 5px\"> <div class=\"col\" style=\"margin-top: 17px;\"><strong style=\"margin-left: 90px;font-size: 20px;font-family: Verdana;\">{USERNAME}</strong><span style=\"color: rgb(173,173,173);margin-left: 5px;font-size: 16px;font-family: Verdana;\">{TIMESTAMP}</span></div><div class=\"col\"><img class=\"rounded-circle img-fluid bg-white border rounded border-white shadow-lg\" src=\"{AVATAR}\" width=\"60\" height=\"80\" style=\"margin-top: 0px;margin-left: 14px;\"><span class=\"text-break d-inline\" style=\"margin-left: 15px;font-size: 18px;color: rgb(225,225,225);\"><strong>{TITLE}:&nbsp;</strong>{MESSAGE}</span></div></div>";
                    data = data.replace("{USERNAME}", chatMessage.getFrom()).replace("{TIMESTAMP}", chatMessage.getTimestamp()).replace("{AVATAR}", chatMessage.getAvatarURL()).replace("{MESSAGE}", chatMessage.getContent()).replace("{COLOR}", chatMessage.getColor()).replace("{TITLE}", chatMessage.getTitle());
                    html.append(data);
                }
                html.append("</div>");
                html.append("<div style=\"padding-top: 75px\"></div>");
            }

            html.append("<script src=\"/js/jquery.js\"></script>").append("<script src=\"/bootstrap/js/bootstrap.min.js\"></script>");
            html.append("</body>").append("</html>");
            String finished = html.toString();
            finished = finished.replace("%CHANNEL_NAME%", channelName).replace("{GUILD_NAME}", TechDiscordBot.getGuild().getName());
            this.html = finished;
            return this;
        }

        public Builder member(Member member) {
            this.member = member;
            return this;
        }

        public Builder password(int length) {
            this.password = new PasswordGenerator.PasswordGeneratorBuilder()
                    .useUpper(true)
                    .useLower(true)
                    .useDigits(true)
                    .build()
                    .generate(length);
            return this;
        }

        public Builder password() {
            return password(10);
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public String getPassword() {
            return this.password;
        }

        public String getHtml() {
            return this.html;
        }

        public String getChannelId() {
            return this.channelId;
        }

        public String getUrl() {
            return "https://tickets.techscode.de/" + this.channelId + "/" + this.password;
        }

        public Builder message() {
            new TechEmbedBuilder("New Transcript")
                    .setText((this.member == null ? "Unknown" : member.getAsMention()) + "'s Ticket Transcript.\n\nhttps://tickets.techscode.de/" + this.channelId + "/" + this.password)
                    .send(TRANSCRIPTS_CHANNEL.query().first());
            return this;
        }

        public Builder upload() {
            Transcript transcript = new Transcript(this.channelId, this.html, this.password);
            TechDiscordBot.getStorage().uploadTranscript(transcript);
            return this;
        }
    }
}