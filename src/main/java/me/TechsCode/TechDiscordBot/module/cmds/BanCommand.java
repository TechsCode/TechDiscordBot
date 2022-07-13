package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.PunishLogs;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Objects;

public class BanCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public BanCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Ban a member from this guild.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "member", "The member to ban.", true),
                new OptionData(OptionType.STRING, "reason", "The reason to ban the member.", true)
        };
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        Member member = Objects.requireNonNull(e.getOption("member")).getAsMember();
        String reason = Objects.requireNonNull(e.getOption("reason")).getAsString();

        Member selfMember = Objects.requireNonNull(e.getGuild()).getSelfMember();
        if (member != null && !selfMember.canInteract(member)) {
            e.reply("This user is too powerful for me to ban.").queue();
            return;
        }

        assert member != null;
        if (member.getRoles().contains(STAFF_ROLE.query().first())) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Ban - Error")
                            .error()
                            .text("You cannot ban this user")
                            .build()
            ).queue();

        } else if (member == e.getMember()) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Ban - Error")
                            .error()
                            .text("You cannot ban yourself")
                            .build()
            ).queue();

        } else {
            member.ban(0, reason).queue();

            e.replyEmbeds(
                    new TechEmbedBuilder("Banned " + member.getUser().getName() + "#" + member.getUser().getDiscriminator())
                            .success()
                            .text("Successfully banned " + member.getAsMention() + " for `" + reason + "`!")
                            .build()
            ).queue();

            PunishLogs.log(
                    new TechEmbedBuilder("Banned " + member.getUser().getName() + "#" + member.getUser().getDiscriminator())
                            .success()
                            .text("Successfully banned " + member.getAsMention() + " for `" + reason + "`!")
            );
        }
    }
}
