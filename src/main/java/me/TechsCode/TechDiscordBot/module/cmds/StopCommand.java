package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class StopCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public StopCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!stop"; }

    @Override
    public String[] getAliases() { return null; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.ADMIN; }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 1 && args[0].equals("acTualLy sToP")) {
            new TechEmbedBuilder("Stop")
                .setText("The bot will now stop!")
                .send(channel);
            TechDiscordBot.getJDA().shutdownNow();
            System.exit(0);
        } else {
            new TechEmbedBuilder("Stop")
                    .setText("Hello, " + member.getAsMention() + "! I've detected that you're trying to stop the me!" +
                            "\n\nI do not like that, especially that if I do stop, I will not be restarted! If you **REALLY** wish to stop me, type the following command:\n`!stop acTualLy sToP`")
                    .send(channel);

        }
    }
}
