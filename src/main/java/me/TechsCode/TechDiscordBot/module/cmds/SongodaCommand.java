package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class SongodaCommand extends CommandModule {

    public SongodaCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!songoda";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return null;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.INFO;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        new TechEmbedBuilder("Songoda")
                .setText("Hey {username}!,\n".replace("{username}", member.getEffectiveName()) + "You can browse their marketplace at [marketplace](https://songoda.com/marketplace)")
                .send(channel);
    }

    @Override
    public int getCooldown() {
        return 10;
    }
}
