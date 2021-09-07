package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Warning;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Objects;

public class DeleteWarnCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public DeleteWarnCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "delwarn";
    }

    @Override
    public String getDescription() {
        return "Delete a warning.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "id", "Warning id.", true),
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String id = Objects.requireNonNull(e.getOption("id")).getAsString();

        Warning warning = TechDiscordBot.getStorage().retrieveWarningById(id);

        MessageEmbed msg = new TechEmbedBuilder("Warning Deleted")
                .text("Warning for "+warning.getMember().getAsMention()+" with id "+warning.getId()+" has been successfully deleted.")
                .build();
        e.replyEmbeds(msg).queue();

        warning.delete();
    }
}

