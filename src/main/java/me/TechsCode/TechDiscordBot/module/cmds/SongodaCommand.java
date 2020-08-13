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
                .setText("We have removed all of our plugins from Songoda.com, this was put in place on June 6th, 2020. We do not provide support or updates on Songoda since then, if you are a client please seek #songoda-transfer. The reasoning for our decision on migrating off of Songoda is for a plethora of reasons. If you refer to this [Reddit Post](https://www.reddit.com/r/admincraft/comments/hqibzi/is_songoda_bad/). You can see some of the things brought to light by the community. After the initial removal of our plugins, we were [contacted](https://media.discordapp.net/attachments/346344529651040268/739882619545845811/unknown.png) by Brianna (Songoda Owner). This furthers our point of removing our plugins from Songoda is the best for our plugins.\n\n- Brooke (<@740067104564183151>)")
                .send(channel);
    }

    @Override
    public int getCooldown() {
        return 10;
    }
}
