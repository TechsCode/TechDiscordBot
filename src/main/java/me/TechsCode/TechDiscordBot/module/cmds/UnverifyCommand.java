package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.concurrent.TimeUnit;

public class UnverifyCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public UnverifyCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "unverify";
    }

    @Override
    public String getDescription() {
        return "Unverify a member.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "type", "The type of value.", true)
                    .addChoice("User ID", "offlineId")
                    .addChoice("Spigot Name", "spigot"),
                new OptionData(OptionType.STRING, "data", "The user id or spigot name.", true),
        };
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 4;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, InteractionHook hook, SlashCommandEvent e) {
        String type = e.getOption("type").getAsString();
        String data = e.getOption("data").getAsString();

        if(type.equals("offlineId")) {
            process(data, channel);
        } else if(type.equals("spigot")) {
            processSpigotId(data, channel);
        } else {
            e.reply("Invalid type!").queue();
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