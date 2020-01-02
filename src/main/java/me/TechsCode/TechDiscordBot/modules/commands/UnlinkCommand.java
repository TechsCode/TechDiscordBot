package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.CommandModule;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.storage.Verification;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class UnlinkCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public UnlinkCommand(TechDiscordBot bot) {  super(bot); }

    @Override
    public String getCommand() {
        return "!unlink";
    }

    @Override
    public String[] getAliases() {
        return null;
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
            new CustomEmbedBuilder("Unlink")
                    .error()
                    .setText("Please specify a user to unverify!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            if(args[0].equals("offlineid")) {
                if(args.length > 1) {
                    process(args[1], channel);
                } else {
                    new CustomEmbedBuilder("Unlink")
                            .error()
                            .setText("Please specify a userid to unverify!")
                            .sendTemporary(channel, 10, TimeUnit.SECONDS);
                }
            }
            if(message.getMentionedMembers().size() > 0) {
                Member member1 = message.getMentionedMembers().get(0);
                process(member1, channel);
            } else if (bot.getGuild().getMembers().stream().anyMatch(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(args[0]) || mem.getUser().getId().equalsIgnoreCase(args[0]))) {
                Member member1 = bot.getGuild().getMembers().stream().filter(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(args[0]) || mem.getUser().getId().equalsIgnoreCase(args[0])).findFirst().orElse(null);
                process(member1, channel);
            }
        }
    }

    public void process(Member member, TextChannel channel) {
        Verification verification = bot.getStorage().retrieveVerificationWithDiscord(member.getUser().getId());
        if(verification == null) {
            new CustomEmbedBuilder("Unlink")
                    .error()
                    .setText(member.getAsMention() + " is not verified!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            verification.delete();
            new CustomEmbedBuilder("Unlink")
                    .success()
                    .setText("Successfully removed " + member.getAsMention() + "'s verification!")
                    .send(channel);
        }
    }

    public void process(String member, TextChannel channel) {
        Verification verification = bot.getStorage().retrieveVerificationWithDiscord(member);
        if(verification == null) {
            new CustomEmbedBuilder("Unlink")
                    .error()
                    .setText(member + " is not verified!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            verification.delete();
            new CustomEmbedBuilder("Unlink")
                    .success()
                    .setText("Successfully removed " + member + "'s verification!")
                    .send(channel);
        }
    }
}
