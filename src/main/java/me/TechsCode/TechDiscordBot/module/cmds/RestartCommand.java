package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.reminders.Reminder;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RestartCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
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
    public String[] getAliases() {
        return new String[]{"!restart"};
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return STAFF;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADMIN;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public boolean deleteCommandMsg() {
        return true;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        try {
            List<Message> messages = new ArrayList<>();

            messages.addAll(TechDiscordBot.getJDA().getTextChannelById("695493411117072425").getHistory().retrievePast(1).complete());
            messages.addAll(TechDiscordBot.getJDA().getTextChannelById("695294630803275806").getHistory().retrievePast(1).complete());
            messages.addAll(TechDiscordBot.getJDA().getTextChannelById("727403767523442759").getHistory().retrievePast(1).complete());

            messages.forEach(m -> m.delete().complete());

            channel.sendMessage("Bot Restarting").complete();
            Thread.sleep(500);

            Runtime.getRuntime().exec("cmd.exe /c start C:\\Users\\Administrator\\Desktop\\Discord-Bot\\start.bat");
            Thread.sleep(1000);

            System.exit(0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
