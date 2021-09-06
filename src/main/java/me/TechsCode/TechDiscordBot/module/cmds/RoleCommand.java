package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.RoleLogs;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.awt.*;
import java.util.ArrayList;

public class RoleCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Staff"); }
    };

    private final ArrayList<String> SENIOR_SUPPORTER_ROLES = new ArrayList<String>() {{
        add("Ultra Permissions");
        add("Ultra Customizer");
        add("Ultra Punishments");
        add("Ultra Regions");
        add("Insane Shops");
        add("Ultra Economy");
        add("Ultra Scoreboards");
        add("Keep Roles");
        add("MC-Market");
        add("Verified");
        add("Songoda Verified");
    }};

    private final ArrayList<String> ASSISTANT_ROLES = new ArrayList<String>() {{
        add("Ultra Permissions");
        add("Ultra Customizer");
        add("Ultra Punishments");
        add("Ultra Regions");
        add("Insane Shops");
        add("Ultra Economy");
        add("Ultra Scoreboards");
        add("Keep Roles");
        add("MC-Market");
        add("Verified");
        add("Songoda Verified");
        add("Junior Supporter");
        add("Supporter");
        add("Senior Supporter");
        add("Retired");
        add("Wiki Editor");
        add("Staff");
    }};

    private final ArrayList<String> DEVELOPER_ROLES = new ArrayList<String>() {{
        add("Ultra Permissions");
        add("Ultra Customizer");
        add("Ultra Punishments");
        add("Ultra Regions");
        add("Insane Shops");
        add("Ultra Economy");
        add("Ultra Scoreboards");
        add("Keep Roles");
        add("MC-Market");
        add("Verified");
        add("Junior Supporter");
        add("Supporter");
        add("Senior Supporter");
        add("Retired");
        add("Wiki Editor");
        add("Staff");
        add("Assistant");
        add("Team Manager");
        add("Songoda Verified");
    }};

    public RoleCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "role";
    }

    @Override
    public String getDescription() {
        return "Give a member a specific role.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] {
                CommandPrivilege.enable(STAFF_ROLE.query().first())
        };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "member", "The member to give a new role", true),
                new OptionData(OptionType.ROLE, "role", "The role which will be given to the member", true)
        };
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        Member member = e.getOption("member").getAsMember();
        Role role = e.getOption("role").getAsRole();

        if(!m.getRoles().contains(TechDiscordBot.getGuild().getRoleById(854044253885956136L)) && !m.getRoles().contains(TechDiscordBot.getGuild().getRoleById(608113993038561325L)) && !m.getRoles().contains(TechDiscordBot.getGuild().getRoleById(311178859171282944L))) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Role Management")
                            .color(Color.orange)
                            .text("**Senior Supporter**: Keep Roles, MC-Market, Verified and all plugin roles\n**Assistant**: Staff, Supporter Roles, Retired, Wiki Editor, Plugin Lab\n**Developer**: Assistant, Team Manager")
                            .build()
            ).queue();
            return;
        }

        if((m.getRoles().contains(TechDiscordBot.getGuild().getRoleById(854044253885956136L)) && !m.getRoles().contains(TechDiscordBot.getGuild().getRoleById(608113993038561325L)) && !SENIOR_SUPPORTER_ROLES.contains(role.getName())) || m.getRoles().contains(TechDiscordBot.getGuild().getRoleById(608113993038561325L)) && !ASSISTANT_ROLES.contains(role.getName())) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Role Management")
                            .color(Color.orange)
                            .text("**Senior Supporter**: Keep Roles, MC-Market, Verified and all plugin roles\n**Assistant**: Staff, Supporter Roles, Retired, Wiki Editor, Plugin Lab\n**Developer**: Assistant, Team Manager")
                            .build()
            ).queue();
            return;
        }

        if(m.equals(member)) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Role Management - Error")
                        .error()
                        .text("You can't edit your own roles!")
                        .build()
            ).queue();
            return;
        }

        if(member.getRoles().contains(role)) {
            TechDiscordBot.getGuild().removeRoleFromMember(member, role).complete();
            e.replyEmbeds(
                    new TechEmbedBuilder("Role Removed")
                            .error().text("Removed " + role.getAsMention() + " from " + member.getAsMention())
                            .build()
            ).queue();

            RoleLogs.log(
                new TechEmbedBuilder("Role Removed")
                        .error().text("Removed " + role.getAsMention() + " from " + member.getAsMention())
            );
        } else {
            TechDiscordBot.getGuild().addRoleToMember(member, role).complete();
            e.replyEmbeds(
                    new TechEmbedBuilder("Role Added")
                            .success().text("Added " + role.getAsMention() + " to " + member.getAsMention())
                            .build()
            ).queue();

            RoleLogs.log(
                    new TechEmbedBuilder("Role Added")
                            .success().text("Added " + role.getAsMention() + " to " + member.getAsMention())
            );
        }
    }
}
