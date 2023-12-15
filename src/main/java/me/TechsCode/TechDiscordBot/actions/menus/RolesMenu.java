package me.techscode.techdiscordbot.actions.menus;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.handlers.selectmenu.entity.SimpleEntitySelectMenu;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RolesMenu {

    public static class Add extends SimpleEntitySelectMenu {

        public Add(@NotNull Member member) {
            super("Add:" + member.getIdLong());
            placeholder("Select the roles");
            minMax(1, 10);
            targetType(EntitySelectMenu.SelectTarget.ROLE);
        }

        @Override
        protected void onMenuInteract(EntitySelectInteraction event) {
            final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

            Member targetMember = event.getGuild().getMemberById(target_id);
            List<Role> selectedRoles = event.getMentions().getRoles();
            StringBuilder roleMentionBuilder = new StringBuilder();


            for (Role role : selectedRoles) {
                assert targetMember != null;
                if (canUse(event.getMember(), role)) {
                    SimpleRoles.addRole(targetMember, role);
                    roleMentionBuilder.append(role.getAsMention()).append(" ");
                }
            }

            event.editMessageEmbeds(new SimpleEmbedBuilder("Added Roles")
                    .text("Successfully added the roles " + roleMentionBuilder.toString() + " to " + targetMember.getAsMention())
                    .success()
                    .build()
            ).setComponents().queue();
        }
    }

    public static class Remove extends SimpleEntitySelectMenu {

        public Remove(@NotNull Member member) {
            super("Remove:" + member.getIdLong());
            placeholder("Select the roles");
            minMax(1, 10);
            targetType(EntitySelectMenu.SelectTarget.ROLE);
        }

        @Override
        protected void onMenuInteract(EntitySelectInteraction event) {
            final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

            Member targetMember = event.getGuild().getMemberById(target_id);
            List<Role> selectedRoles = event.getMentions().getRoles();
            StringBuilder roleMentionBuilder = new StringBuilder();


            for (Role role : selectedRoles) {
                assert targetMember != null;
                if (canUse(event.getMember(), role)) {
                    SimpleRoles.removeRole(targetMember, role);
                    roleMentionBuilder.append(role.getAsMention()).append(" ");
                }
            }

            event.editMessageEmbeds(new SimpleEmbedBuilder("Remove Roles")
                    .text("Successfully removed the roles " + roleMentionBuilder.toString() + " from " + targetMember.getAsMention())
                    .error()
                    .build()
            ).setComponents().queue();
        }
    }

    private static boolean canUse(Member member, Role role) {
        List<Long> allowedSupporterRoles = new ArrayList<Long>();
        allowedSupporterRoles.add(Settings.Roles.verified);
        allowedSupporterRoles.add(Settings.Roles.spigot);
        allowedSupporterRoles.add(Settings.Roles.builtByBit);
        allowedSupporterRoles.add(Settings.Roles.songoda);
        allowedSupporterRoles.add(Settings.Roles.polymart);
        allowedSupporterRoles.add(Settings.Roles.ultraPermissions);
        allowedSupporterRoles.add(Settings.Roles.ultraPunishments);
        allowedSupporterRoles.add(Settings.Roles.ultraCustomizer);
        allowedSupporterRoles.add(Settings.Roles.ultraRegions);
        allowedSupporterRoles.add(Settings.Roles.ultraEconomy);
        allowedSupporterRoles.add(Settings.Roles.ultraScoreboards);
        allowedSupporterRoles.add(Settings.Roles.ultraMotd);
        allowedSupporterRoles.add(Settings.Roles.insaneShops);
        allowedSupporterRoles.add(Settings.Roles.insaneVaults);

        if (member.getRoles().contains(SimpleRoles.getRoleById(member.getGuild(), Settings.Roles.supporter))) {
            return allowedSupporterRoles.contains(role.getIdLong());
        }

        return member.getPermissions().contains(Permission.ADMINISTRATOR);
    }
}
