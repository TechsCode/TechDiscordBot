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
        TechEmbedBuilder builder = new TechEmbedBuilder("Mute Command").error();

        if(args.length == 0) {
            builder.setText("Member is not found! Please specify a member in the arguments, either using their mention, name and discriminator, or user id.");
            builder.send(channel);
            return;
        }

        if(TechDiscordBot.getMemberFromString(message, args[0]) == null) {
            builder.setText("Member is not found!");
            builder.send(channel);
            return;
        }

        Member target = TechDiscordBot.getMemberFromString(message, args[0]);

        if(target == null) {
            builder.setText(args[0] + " is not a member!");
            builder.success().send(channel);
            return;
        }

        if(memberHasMutedRole(target)) {
            target.getGuild().removeRoleFromMember(target, MUTED_ROLE.query().first()).queue();

            builder.setText(target.getAsMention() + " is no longer muted!");
            builder.success().send(channel);
            return;
        }

        target.getGuild().addRoleToMember(target, MUTED_ROLE.query().first()).queue();

        builder.setText(target.getAsMention() + " is now muted!");
        builder.success().send(channel);
    }

    public boolean memberHasMutedRole(Member member) {
        return member.getRoles().contains(MUTED_ROLE.query().first());
    }
}
