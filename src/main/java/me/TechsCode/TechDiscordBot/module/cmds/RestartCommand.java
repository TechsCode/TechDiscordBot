package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestartCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public RestartCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public String getDescription() {
        return "Restart the bot.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, SlashCommandEvent e) {
        try {
            List<Message> messages = new ArrayList<>();

            messages.addAll(TechDiscordBot.getJDA().getTextChannelById("695493411117072425").getHistory().retrievePast(1).complete());
            messages.addAll(TechDiscordBot.getJDA().getTextChannelById("695294630803275806").getHistory().retrievePast(1).complete());
            messages.addAll(TechDiscordBot.getJDA().getTextChannelById("727403767523442759").getHistory().retrievePast(1).complete());

            messages.forEach(m -> m.delete().complete());

            e.reply("Bot Restarting").setEphemeral(true).complete();
            Thread.sleep(500);

            Runtime.getRuntime().exec("cmd.exe /c start C:\Users\Administrator\Desktop\TechBot\start.bat");
            Thread.sleep(1000);

            System.exit(0);
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
