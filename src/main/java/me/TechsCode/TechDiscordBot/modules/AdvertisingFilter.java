package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.RedirectUtil;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvertisingFilter extends Module {

    public AdvertisingFilter(TechDiscordBot bot) {
        super(bot);
    }

    private String DISCORD_REGEX = ".*(https?:\\/\\/)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com\\/invite)\\/.+.*";
    private String URL_REGEX = "([--:\\w?@%&+~#=]*\\.[a-z]{2,4}\\/{0,2})((?:[?&](?:\\w+)=(?:\\w+))+|[--:\\w?@%&+~#=]+)?";

    @SubscribeEvent
    public void recieve(MessageReceivedEvent e) {
        if(!e.getChannelType().equals(ChannelType.TEXT)) return;
        filterDiscordLinks(e.getMessage());
    }

    @SubscribeEvent
    public void update(MessageUpdateEvent e) {
        if(!e.getChannelType().equals(ChannelType.TEXT)) return;
        filterDiscordLinks(e.getMessage());
    }

    public void filterDiscordLinks(Message message) {
        String msg = message.getContentDisplay();
        if(msg.matches(DISCORD_REGEX)) {
            message.delete().queue();
            new CustomEmbedBuilder("Advertising").setText("Please do not advertise " + message.getAuthor().getAsMention() + "!").error().sendTemporary(message.getTextChannel(), 10, TimeUnit.SECONDS);
            return;
        }
        for(String url : getUrlsInMsg(msg)) {
            String url2 = RedirectUtil.getRedirectUrl(url);
            if(url2.matches(DISCORD_REGEX)){
                message.delete().queue();
                new CustomEmbedBuilder("Advertising").setText("Please do not advertise " + message.getAuthor().getAsMention() + "!").error().sendTemporary(message.getTextChannel(), 10, TimeUnit.SECONDS);
                return;
            }
        }
    }

    public List<String> getUrlsInMsg(String msg) {
        List<String> matches = new ArrayList<>();
        Matcher m = Pattern.compile(URL_REGEX).matcher(msg);
        while(m.find()) matches.add(m.group());
        return matches;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public String getName() {
        return "Advertising Filter";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[0];
    }
}
