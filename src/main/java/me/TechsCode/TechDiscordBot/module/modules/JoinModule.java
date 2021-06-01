package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class JoinModule extends Module {

    private final DefinedQuery<Role> MUTED_ROLE = new DefinedQuery<Role>() {
        protected Query<Role> newQuery() {
            return JoinModule.this.bot.getRoles("Muted");
        }
    };

    public JoinModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() { }

    @Override
    public void onDisable() { }

    public String getName() {
        return "JoinModule";
    }

    @SubscribeEvent
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(e.getMember().getId());

        new TechEmbedBuilder("Welcome to Tech's Plugin Support")
                .text((existingVerification != null ? "Welcome to Tech's Plugin Support,\nIf you are looking for help, you must first verify your Spigot Account in <#695493411117072425>.\n\nAfter you have been verified, you will have access to the support channel(s)." : "Hello there,\nYou have previously verified yourself and your roles will be automatically updated as a result.\n\nThis update may take 10 to 15 minutes to complete.") + "\n\nThanks & Welcome,\nTechsCode & Team")
                .queue(e.getMember());
    }

    public Requirement[] getRequirements() {
        return new Requirement[0];
    }
}
