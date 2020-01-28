package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.objects.Module;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.RedirectUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvertisingFilter extends Module {

    private final String DISCORD_REGEX = ".*(https?:\\/\\/)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com\\/invite)\\/.+.*";
    private final Pattern URL_PATTERN = Pattern.compile("([--:\\w?@%&+~#=]*\\.[a-z]{2,4}\\/{0,2})((?:[?&](?:\\w+)=(?:\\w+))+|[--:\\w?@%&+~#=]+)?");

    public AdvertisingFilter(TechDiscordBot bot) { super(bot); }

    @SubscribeEvent
    public void receive(GuildMessageReceivedEvent e) {
        if(checkIfAdvertisement(e.getMessage()) && !isStaff(e.getMember())) removeAdvertisement(e.getMessage());
    }

    @SubscribeEvent
    public void update(GuildMessageUpdateEvent e) {
        if(checkIfAdvertisement(e.getMessage()) && !isStaff(e.getMember())) removeAdvertisement(e.getMessage());
    }

    private boolean isStaff(Member member) {
        Role staffRole = bot.getRoles("staff").first();
        return staffRole != null && member.getRoles().contains(staffRole);
    }

    private boolean checkIfAdvertisement(Message message) {
        String msg = message.getContentDisplay();
        if(msg.matches(DISCORD_REGEX)) return true;
        Matcher m = URL_PATTERN.matcher(msg);
        while (m.find()) {
            String url = RedirectUtil.getRedirectUrl(m.group());
            if(url.matches(DISCORD_REGEX)) return true;
        }
        return false;
    }

    private void removeAdvertisement(Message message) {
        message.delete().queue();
        new CustomEmbedBuilder("Advertising")
                .setText("Please do not advertise " + message.getAuthor().getAsMention() + "!").error()
                .sendTemporary(message.getTextChannel(), 10, TimeUnit.SECONDS);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public String getName() { return "Advertising Filter"; }

    @Override
    public Requirement[] getRequirements() { return new Requirement[0]; }
}
