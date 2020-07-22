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

import java.text.SimpleDateFormat;
import java.util.Date;

public class RemindMeCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public RemindMeCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!remindme";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"!remind"};
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
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        Reminder reminder = TechDiscordBot.getRemindersManager().createReminder(member.getUser(), channel, args);

        if(reminder == null) {
            new TechEmbedBuilder("Reminder - Error")
                    .setText("An error has occurred. Did you specify a time and a reason?\n\n**Here are some examples!**:\n`!remind 1 day Fix x thing.`\n`!remindme 30 hours I need help.`\n`!remindme 30 hours I need help. dm` (makes it a dm)")
                    .error()
                    .send(channel);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a");
            String date = sdf.format(new Date(reminder.getTime()));

            new TechEmbedBuilder("Reminder Set!")
                    .setText("I will remind you in **" + reminder.getHumanTime() + "** (" + date + ") for **" + reminder.getReminder() + "**!")
                    .success()
                    .send(channel);
        }
    }
}
