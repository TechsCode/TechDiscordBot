package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.CommandModule;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class MuteCommand extends CommandModule {

    private final DefinedQuery<Role> MUTED_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Muted");
        }
    };
    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public MuteCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!mute";
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return STAFF_ROLE;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 0) {
            new CustomEmbedBuilder("Mute Command").setText("Member is not found! Please specify a member in the arguments, either using their mention, name and discriminator, or user id.").error().send(channel);
        } else if(getMemberFromString(message, args[0]) != null) {
            Member memberS = getMemberFromString(message, args[0]);
            if(memberHasMutedRole(memberS)) {
                memberS.getGuild().getController().removeRolesFromMember(memberS, MUTED_ROLE.query().first()).queue();
                new CustomEmbedBuilder("Mute Command").setText(memberS.getAsMention() + " is no longer muted!").success().send(channel);
            } else {
                memberS.getGuild().getController().addRolesToMember(memberS, MUTED_ROLE.query().first()).queue();
                new CustomEmbedBuilder("Mute Command").setText(memberS.getAsMention() + " is now muted!").success().send(channel);
            }
        } else {
            new CustomEmbedBuilder("Mute Command").setText("Member is not found!").error().send(channel);
        }
    }

    public boolean memberHasMutedRole(Member member) {
        return member.getRoles().contains(MUTED_ROLE.query().first());
    }

    public Member getMemberFromString(Message msg, String s) {
        if (msg.getMentionedMembers().size() > 0) {
            return msg.getMentionedMembers().get(0);
        } else if (bot.getGuild().getMembers().stream().anyMatch(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(s) || mem.getUser().getId().equalsIgnoreCase(s))) {
            return bot.getGuild().getMembers().stream().filter(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(s) || mem.getUser().getId().equalsIgnoreCase(s)).findFirst().orElse(null);
        }
        return null;
    }
}
