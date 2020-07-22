package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

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

    public MuteCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!mute"; }

    @Override
    public String[] getAliases() { return null; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.INFO; }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 0) {
            new TechEmbedBuilder("Mute Command").setText("Member is not found! Please specify a member in the arguments, either using their mention, name and discriminator, or user id.").error().send(channel);
        } else if(TechDiscordBot.getMemberFromString(message, args[0]) != null) {
            Member memberS = TechDiscordBot.getMemberFromString(message, args[0]);
            if(memberHasMutedRole(memberS)) {
                memberS.getGuild().removeRoleFromMember(memberS, MUTED_ROLE.query().first()).queue();
                new TechEmbedBuilder("Mute Command").setText(memberS.getAsMention() + " is no longer muted!").success().send(channel);
            } else {
                memberS.getGuild().addRoleToMember(memberS, MUTED_ROLE.query().first()).queue();
                new TechEmbedBuilder("Mute Command").setText(memberS.getAsMention() + " is now muted!").success().send(channel);
            }
        } else {
            new TechEmbedBuilder("Mute Command").setText("Member is not found!").error().send(channel);
        }
    }

    public boolean memberHasMutedRole(Member member) {
        return member.getRoles().contains(MUTED_ROLE.query().first());
    }
}
