package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class UserInfoCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public UserInfoCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!userinfo";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"!uinfo"};
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.INFO;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        User user = member.getUser();
        new TechEmbedBuilder(user.getName() + "#" + user.getDiscriminator())
                .addField("Status", member.getOnlineStatus().getKey().substring(0, 1).toUpperCase() + member.getOnlineStatus().getKey().substring(1), true)
                .addField("Created At", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Joined At", member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Flags", user.getFlags().clone().stream().map(User.UserFlag::getName).collect(Collectors.joining(", ")) + ".", false)
                .addField("Roles", member.getRoles().stream().map(Role::getAsMention).collect(Collectors.joining(", ")) + ".", false)
                .setThumbnail(user.getAvatarUrl())
        .send(channel);
    }
}
