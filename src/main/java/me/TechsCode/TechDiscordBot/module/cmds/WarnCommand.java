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

public class WarnCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public WarnCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return "Warn a user.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "user", "Select a user.", true),
                new OptionData(OptionType.STRING, "reason", "Enter a reason.", true)
                        .addChoice("Ghost Pinging", "Ghost Pinging")
                        .addChoice("Mass Mentioning", "Mass Mentioning")
                        .addChoice("Mini-Modding", "Mini-Modding")
                        .addChoice("Spamming", "Spamming")
                        .addChoice("NSFW Content", "NSFW Content")
                        .addChoice("Advertisement", "Advertisement"),
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        User user = Objects.requireNonNull(e.getOption("user")).getAsUser();
        Member member = TechDiscordBot.getGuild().getMemberById(user.getId());

        String reason = Objects.requireNonNull(e.getOption("reason")).getAsString();

        MessageEmbed msg;

        assert member != null;
        if(member.getRoles().contains(STAFF_ROLE.query().first())){
            msg = new TechEmbedBuilder("User Warnings")
                    .text("You cannot warn a staff member.")
                    .build();
            e.replyEmbeds(msg).queue();
            return;
        }

        Warning warning = new Warning(user.getId(), m.getId(), reason, System.currentTimeMillis());
        warning.save();

        msg = new TechEmbedBuilder("User Warnings")
                   .addField("User:", warning.getMember().getAsMention(), false)
                   .addField("Reporter", warning.getReporter().getAsMention(), false)
                   .addField("Reason", warning.getReason(), false)
                   .build();
        e.replyEmbeds(msg).queue();

    }
}

