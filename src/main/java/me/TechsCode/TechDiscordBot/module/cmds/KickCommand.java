package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class KickCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Staff"); }
    };

    public KickCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!kick"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.ADMIN; }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member m, String[] args) {
        if(args.length == 0) {
            new TechEmbedBuilder("Kick - Error")
                    .error()
                    .setText("You have to specify a member to kick!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            Member member = TechDiscordBot.getMemberFromString(message, args[0]);

            if(member == null) {
                new TechEmbedBuilder("Kick - Error")
                        .error()
                        .setText(args[0] + " is not a member!")
                        .sendTemporary(channel, 10, TimeUnit.SECONDS);
                return;
            }

            boolean canKick = member.getRoles().stream().noneMatch(r -> r.getName().equals("Staff"));

            if(!canKick) {
                new TechEmbedBuilder("Kick - Error")
                        .error()
                        .setText("You cannot kick " + member.getAsMention() + "! Nice try though.")
                        .sendTemporary(channel, 10, TimeUnit.SECONDS);
                return;
            }

            String reason = args.length == 1 ? null : String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            if(reason == null)
                member.kick().queue();

            if(reason != null)
                member.kick(reason).queue();

            new TechEmbedBuilder("Kicked " + member.getUser().getName() + "#" + member.getUser().getDiscriminator())
                    .success()
                    .setText("Successfully kicked " + member.getAsMention() + (reason == null ? "!" : " for `" + reason + "`!"))
                    .send(channel);
        }
    }
}