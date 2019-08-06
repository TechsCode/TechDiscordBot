package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.CommandModule;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class RestartCommand extends CommandModule {

    private final DefinedQuery<Role> SUPPORTER_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Supporter");
        }
    };

    public RestartCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!restart";
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return SUPPORTER_ROLE;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, String[] args) {
        new CustomEmbedBuilder("Restart")
                .setText("The bot will be performing a restart..")
                .sendTemporary(channel, 10, TimeUnit.SECONDS);

        System.exit(0);
    }
}
