package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.reminders.Reminder;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RemindMeCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public RemindMeCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "remind";
    }

    @Override
    public String getDescription() {
        return "Set a reminder to remind yourself.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "time", "How long FROM NOW to be reminded.", true),
                new OptionData(OptionType.STRING, "reminder", "What to be reminded about.", true),
        };
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 3;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, InteractionHook hook, SlashCommandEvent e) {
        String time = e.getOption("time").getAsString();
        String reminder = e.getOption("reminder").getAsString();

        Reminder r = TechDiscordBot.getRemindersManager().createReminder(e.getUser(), time, reminder, channel);

        if(r == null) {
            e.replyEmbeds(
                new TechEmbedBuilder("Reminder - Error")
                    .setText("An error has occurred. Did you specify a time and a reason?\n\n**Here are some examples!**:\n`/remind 1 day Fix x thing.`\n`/remindme 30 hours I need help.`\n`/remindme 30 hours I need help. dm` (makes it a dm)")
                    .error()
                    .build()
            ).queue();
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm:ss a");
            String date = sdf.format(new Date(r.getTime()));

            e.replyEmbeds(
                new TechEmbedBuilder("Reminder Set!")
                    .setText("I will remind you in **" + r.getHumanTime() + "** (" + date + ") for **" + r.getReminder() + "**!")
                    .success()
                    .build()
            ).queue();
        }
    }
}
