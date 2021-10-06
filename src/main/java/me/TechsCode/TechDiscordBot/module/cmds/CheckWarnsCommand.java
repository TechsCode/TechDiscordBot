package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Warning;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Objects;
import java.util.Set;

public class CheckWarnsCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public CheckWarnsCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "checkwarns";
    }

    @Override
    public String getDescription() {
        return "Check the warnings of a user.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "user", "Select user.", true),
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        User user = Objects.requireNonNull(e.getOption("user")).getAsUser();

        Set<Warning> warnings = TechDiscordBot.getStorage().retrieveWarningsByUserID(user.getId());
        
        TechEmbedBuilder embed = new TechEmbedBuilder("User Warned");

        embed.text("This user has "+warnings.size()+" warnings.");

        for (Warning warning : warnings) {
            embed.addField(warning.getTimeFormatted(), "Reason: "+warning.getReason()+" | ID: "+warning.getId()+"\nIssuer: "+warning.getReporter().getAsMention(), false);
        }
        
        e.replyEmbeds(embed.build()).queue();
    }
}

