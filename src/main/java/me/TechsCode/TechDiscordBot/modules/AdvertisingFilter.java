package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.concurrent.TimeUnit;

public class AdvertisingFilter extends Module {

    public AdvertisingFilter(TechDiscordBot bot) {
        super(bot);
    }

    @SubscribeEvent
    public void recieve(MessageReceivedEvent e) {
        String msg = e.getMessage().getContentDisplay();

        if(msg.matches("(https?:\\/\\/)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com\\/invite)\\/.+[a-z]")) {
            e.getMessage().delete().queue();
            new CustomEmbedBuilder("Advertising").setText("Please do not advertise " + e.getAuthor().getAsMention() + "!").error().sendTemporary(e.getTextChannel(), 10, TimeUnit.SECONDS);
        }
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
