package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.ServerLogs;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class EventsModule extends Module {

    public EventsModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {}

    @SubscribeEvent
    public void memberJoin(GuildMemberJoinEvent e) {
        ServerLogs.log(
                new TechEmbedBuilder("Member Joined!")
                    .success()
                    .text("Welcome " + e.getMember().getAsMention() + " (" + e.getUser().getName() + "#" + e.getUser().getDiscriminator() + ", " + e.getUser().getId() + ")")
                    .thumbnail(e.getMember().getUser().getAvatarUrl())
        );
    }

    @SubscribeEvent
    public void memberLeave(GuildMemberRemoveEvent e) {
        ServerLogs.log(
                new TechEmbedBuilder("Member Left!")
                        .error()
                        .text(e.getUser().getAsMention() + " (" + e.getUser().getName() + "#" + e.getUser().getDiscriminator() + ", " + e.getUser().getId() + ")")
                        .thumbnail(e.getUser().getAvatarUrl())
        );
    }

    @SubscribeEvent
    public void memberBan(GuildBanEvent e) {
        ServerLogs.log(
                new TechEmbedBuilder("Member Banned!")
                        .error()
                        .text("Don't say goodbye to " + e.getUser().getAsMention() + " (" + e.getUser().getName() + "#" + e.getUser().getDiscriminator() + ", " + e.getUser().getId() + ")")
                        .thumbnail(e.getUser().getAvatarUrl())
        );
    }

    @Override
    public void onDisable() {}

    @Override
    public String getName() {
        return "Events";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[0];
    }
}
