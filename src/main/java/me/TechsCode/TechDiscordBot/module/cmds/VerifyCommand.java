package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

public class VerifyCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public VerifyCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "verify";
    }

    @Override
    public String getDescription() {
        return "Verify a member.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.MENTIONABLE, "member", "Member to verify.", true),
                new OptionData(OptionType.STRING, "spigot-id", "The member's spigot id.", true)
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String spigotId = e.getOption("spigot-id").getAsString();
        Member member = (Member) e.getOption("member").getAsMentionable();

        if(TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member) != null || TechDiscordBot.getStorage().retrieveVerificationWithSpigot(spigotId) != null) {
            e.reply(spigotId + " (" + member.getAsMention() + ") is already verified!").setEphemeral(true).queue();
            return;
        }

        if(TechDiscordBot.getSpigotAPI().getPurchases().userId(spigotId).size() == 0) {
            e.reply(spigotId + " (" + member.getAsMention() + ") does not own any of Tech's Plugins!").setEphemeral(true).queue();
            return;
        }

        TechDiscordBot.getStorage().createVerification(spigotId, member.getId());
        e.reply("Successfully verified " + spigotId + "! (" + member.getAsMention() + ")").queue();
    }
}
