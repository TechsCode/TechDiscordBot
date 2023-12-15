package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlMember;
import me.techscode.techdiscordbot.database.entities.SqlPatreon;
import me.techscode.techdiscordbot.model.Logs;
import me.techscode.techdiscordbot.model.enums.Patreon;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * TODO: Fix all the comment and finish this file!
 */
public class RolesModule extends ListenerAdapter {

    /**
     * Listen for role add event
     * @param event GuildMemberRoleAddEvent
     */
    @SubscribeEvent
    public void onRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        if (event.getGuild() == TechDiscordBot.getMainGuild()) {
            log(event.getMember(), true, event.getRoles());
        }

        SqlMember sqlMember = Database.MEMBERSTable.getFromDiscordId(event.getMember().getIdLong()).get(0);

        for (Role role : event.getRoles()) {
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.patreon)) {
                if (Database.PATREONTable.getByDiscordId(event.getMember().getIdLong()).isEmpty()) {
                    Database.PATREONTable.add(new SqlPatreon(sqlMember.getId(), System.currentTimeMillis() / 1000L));
                }
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.traveler)) {
                Database.PATREONTable.updateTier(sqlMember.getId(), Patreon.TRAVELER);
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.advanturer)) {
                Database.PATREONTable.updateTier(sqlMember.getId(), Patreon.ADVANTURER);
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.pioneer)) {
                Database.PATREONTable.updateTier(sqlMember.getId(), Patreon.PIONEER);
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.wizzard)) {
                Database.PATREONTable.updateTier(sqlMember.getId(), Patreon.WIZARD);
            }
        }
    }

    @SubscribeEvent
    public void onRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        if (event.getGuild() == TechDiscordBot.getMainGuild()) {
            log(event.getMember(), false, event.getRoles());
        }

        for (Role role : event.getRoles()) {
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.patreon)) {
                Common.log("Patreon role removed from " + event.getMember().getUser().getAsTag());
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.traveler)) {
                Common.log("Patreon Traveler role removed from " + event.getMember().getUser().getAsTag());
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.advanturer)) {
                Common.log("Patreon advanturer role removed from " + event.getMember().getUser().getAsTag());
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.pioneer)) {
                Common.log("Patreon pioneer role removed from " + event.getMember().getUser().getAsTag());
            }
            if (role == SimpleRoles.getRoleById(event.getGuild(), Settings.Roles.Patreon.wizzard)) {
                Common.log("Patreon wizzard role removed from " + event.getMember().getUser().getAsTag());
            }
        }

    }

    private void log(@NotNull Member member, boolean added, @NotNull List<Role> roles) {

        StringBuilder roleMentionBuilder = new StringBuilder();
        StringBuilder roleBuilder = new StringBuilder();

        for (Role role : roles) {
            roleMentionBuilder.append(role.getAsMention()).append(" ");
            roleBuilder.append(role.getName()).append(" ");
        }

        SimpleEmbedBuilder embedBuilder = new SimpleEmbedBuilder(added ? "Roles added!" : "Roles removed!")
                .field("Member:", member.getAsMention(), true)
                .field("Roles:", roleMentionBuilder.toString(), true)
                .field("ID:", member.getId(), true)
                .thumbnail(member.getUser().getAvatarUrl());

        if (added) {
            Logs.RoleLogs.log(embedBuilder.success());
            Common.log("Roles added to " + ConsoleColor.CYAN + member.getUser().getAsTag() + ConsoleColor.RESET + "; "+ roleBuilder);
        } else {
            Logs.RoleLogs.log(embedBuilder.error());
            Common.log( "Roles removed from " + ConsoleColor.CYAN + member.getUser().getAsTag() + ConsoleColor.RESET + "; "+ roleBuilder);
        }
    }
}
