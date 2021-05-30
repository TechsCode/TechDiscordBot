package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class UserInfoCommand extends CommandModule {

    public UserInfoCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Get information about a specific user.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.MENTIONABLE, "member", "Member to get info about. (Default: You)")
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        Member member = e.getOption("member") == null ? m : (Member) e.getOption("member").getAsMentionable();
        User user = member.getUser();

        e.replyEmbeds(new TechEmbedBuilder(user.getName() + "#" + user.getDiscriminator())
                .addField("Status", member.getOnlineStatus().getKey().substring(0, 1).toUpperCase() + member.getOnlineStatus().getKey().substring(1), true)
                .addField("Created At", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Joined At", member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Flags", user.getFlags().clone().stream().map(User.UserFlag::getName).collect(Collectors.joining(", ")) + ".", false)
                .addField("Roles", member.getRoles().stream().map(Role::getAsMention).collect(Collectors.joining(", ")) + ".", false)
                .setThumbnail(user.getAvatarUrl())
                .build()
        ).queue();
    }
}
