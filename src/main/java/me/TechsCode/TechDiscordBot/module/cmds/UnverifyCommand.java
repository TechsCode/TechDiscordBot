package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class UnverifyCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public UnverifyCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!unverify"; }

    @Override
    public String[] getAliases() { return new String[]{"!unlink"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.ADMIN; }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 0) {
            new TechEmbedBuilder("Unverify Command - Error")
                    .error()
                    .setText("Please specify a user to unverify!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            if(args[0].equals("offlineid")) {
                if(args.length > 1) {
                    process(args[1], channel);
                } else {
                    new TechEmbedBuilder("Unverify Command - Error")
                            .error()
                            .setText("Please specify a userid to unverify!")
                            .sendTemporary(channel, 10, TimeUnit.SECONDS);
                }
            } else if(args[0].equals("spigot")) {
                if(args.length > 1) {
                    processSpigotId(args[1], channel);
                } else {
                    new TechEmbedBuilder("Unverify Command - Error")
                            .error()
                            .setText("Please specify a spigot id to unverify!")
                            .sendTemporary(channel, 10, TimeUnit.SECONDS);
                }
            }
            if(message.getMentionedMembers().size() > 0) {
                Member member1 = message.getMentionedMembers().get(0);
                process(member1, channel);
            } else if (TechDiscordBot.getGuild().getMembers().stream().anyMatch(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(args[0]) || mem.getUser().getId().equalsIgnoreCase(args[0]))) {
                Member member1 = TechDiscordBot.getGuild().getMembers().stream().filter(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(args[0]) || mem.getUser().getId().equalsIgnoreCase(args[0])).findFirst().orElse(null);
                process(member1, channel);
            }
        }
    }

    public void process(Member member, TextChannel channel) {
        Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member.getUser().getId());

        if(verification == null) {
            new TechEmbedBuilder("Unverify Command - Error")
                    .error()
                    .setText(member.getAsMention() + " is not verified!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            verification.delete();

            new TechEmbedBuilder("Unverify Command - Success")
                    .success()
                    .setText("Successfully removed " + member.getAsMention() + "'s verification!")
                    .send(channel);
        }
    }

    public void processSpigotId(String spigotId, TextChannel channel) {
        Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(spigotId);

        if(verification == null) {
            new TechEmbedBuilder("Unverify Command - Error")
                    .error()
                    .setText("The spigot id '" + spigotId + "' is not verified!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            boolean isUserOnline = TechDiscordBot.getGuild().getMemberById(verification.getDiscordId()) != null;

            new TechEmbedBuilder("Unverify Command - Success")
                    .success()
                    .setText("Successfully removed " + (isUserOnline ? TechDiscordBot.getGuild().getMemberById(verification.getDiscordId()).getAsMention() : verification.getDiscordId()) + "'s verification!")
                    .send(channel);
            verification.delete();
        }
    }

    public void process(String member, TextChannel channel) {
        Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member);

        if(verification == null) {
            new TechEmbedBuilder("Unverify Command - Error")
                    .error()
                    .setText(member + " is not verified!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            verification.delete();

            new TechEmbedBuilder("Unverify Command - Success")
                    .success()
                    .setText("Successfully removed " + member + "'s verification!")
                    .send(channel);
        }
    }
}