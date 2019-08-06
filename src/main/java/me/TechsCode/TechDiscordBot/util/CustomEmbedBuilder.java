package me.TechsCode.TechDiscordBot.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CustomEmbedBuilder extends EmbedBuilder {

    public CustomEmbedBuilder(String title) {
        if(title != null) setAuthor(title, "http://techsco.de", "https://i.imgur.com/nnegGEV.png");
        setColor(new Color(81, 153, 226));
        setFooter("Developed by Tech");
    }

    public CustomEmbedBuilder setFooter(String text){
        setFooter("Tech's Plugin Support • "+text, "https://i.imgur.com/nzfiUTy.png");
        return this;
    }

    public CustomEmbedBuilder error(){
        setColor(new Color(178,34,34));
        return this;
    }

    public CustomEmbedBuilder success(){
        setColor(new Color(50, 205, 50));
        return this;
    }

    public CustomEmbedBuilder setText(String text){
        setDescription(text);
        return this;
    }

    public Message send(TextChannel textChannel){
        return textChannel.sendMessage(build()).complete();
    }

    public void sendTemporary(TextChannel textChannel, int duration, TimeUnit timeUnit){
        Message message = send(textChannel);
        message.delete().submitAfter(duration, timeUnit);
    }

    public void sendTemporary(TextChannel textChannel, int duration){
        sendTemporary(textChannel, duration, TimeUnit.SECONDS);
    }

    @Override
    public CustomEmbedBuilder addField(String name, String value, boolean inline) {
        super.addField(name, value, inline);
        return this;
    }

    @Override
    public CustomEmbedBuilder addBlankField(boolean inline) {
        super.addBlankField(inline);
        return this;
    }
}
