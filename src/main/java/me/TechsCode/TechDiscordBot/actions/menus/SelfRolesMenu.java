package me.techscode.techdiscordbot.actions.menus;

import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.handlers.selectmenu.string.SimpleStringSelectMenu;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelfRolesMenu extends SimpleStringSelectMenu {

	public SelfRolesMenu() {
		super("selfRolesMenu");
		placeholder("Select the roles you want to get pings for");

		minMax(0, 6);

		options(
				SelectOption.of("Plugin Updates", "pluginUpdates")
						.withEmoji(Emoji.fromFormatted("<:Updates:837724632739217418>"))
						.withDescription("Get notified when a plugin gets updated"),
				SelectOption.of("Announcements", "announcements")
						.withEmoji(Emoji.fromFormatted("<:Announcements:837724632655986718>"))
						.withDescription("Get notified when a new announcement is made"),
				SelectOption.of("Patreon News", "patreonNews")
						.withEmoji(Emoji.fromFormatted("<:PatreonNews:873383006520356964>"))
						.withDescription("Get notified when a new Patreon news is made"),
				SelectOption.of("PluginLab", "pluginLab")
						.withEmoji(Emoji.fromFormatted("<:PluginLab:1045068040615772221>"))
						.withDescription("Get notified when a new PluginLab is released"),
				SelectOption.of("Giveaways", "giveaways")
						.withEmoji(Emoji.fromFormatted("<:giveaways:837724632529895486>"))
						.withDescription("Get notified when a new giveaway is hosted"),
				SelectOption.of("Community", "community")
						.withEmoji(Emoji.fromFormatted("<:TechSupport:681682145823293447>"))
						.withDescription("Get access to the community channels"),
				SelectOption.of("Insane Announcer", "insaneAnnouncer")
						.withEmoji(Emoji.fromFormatted("<:InsaneAnnouncer:741465636231970886>"))
						.withDescription("Get access to the insane announcer support channel")
		);
	}

	@Override
	protected void onMenuInteract(final StringSelectInteraction event) {
		final List<SelectOption> options = event.getSelectedOptions();

		final boolean updates = false;
		boolean announcements = false;
		boolean patreonNews = false;
		boolean pluginLab = false;
		boolean giveaways = false;
		boolean community = false;
		boolean insaneAnnouncer = false;


		final Role updateRole = SimpleRoles.getRoleById(Objects.requireNonNull(event.getGuild()), Settings.Roles.updates);

		final List<Role> addedRoles = new ArrayList<>();
		final List<Role> removedRoles = new ArrayList<>();

		for (final SelectOption option : options) {
			if (option.getValue().equals("pluginUpdates") && SimpleRoles.hasRole(getMember(), updateRole)) {
				SimpleRoles.removeRole(getMember(), updateRole);
				removedRoles.add(updateRole);
			} else if (!SimpleRoles.hasRole(getMember(), updateRole)) {
				SimpleRoles.addRole(getMember(), updateRole);
				addedRoles.add(updateRole);
			}
			if (option.getValue().equals("announcements")) announcements = true;
			if (option.getValue().equals("patreonNews")) patreonNews = true;
			if (option.getValue().equals("pluginLab")) pluginLab = true;
			if (option.getValue().equals("giveaways")) giveaways = true;
			if (option.getValue().equals("community")) community = true;
			if (option.getValue().equals("insaneAnnouncer")) insaneAnnouncer = true;
		}

		final Role announcementsRole = SimpleRoles.getRoleById(Objects.requireNonNull(event.getGuild()), Settings.Roles.announcements);
		if (announcements && SimpleRoles.hasRole(getMember(), announcementsRole)) {
			SimpleRoles.removeRole(getMember(), announcementsRole);
			removedRoles.add(announcementsRole);
		} else if (announcements && !SimpleRoles.hasRole(getMember(), announcementsRole)) {
			SimpleRoles.addRole(getMember(), announcementsRole);
			addedRoles.add(announcementsRole);
		}


		if (patreonNews) {
			final Role role = SimpleRoles.getRoleById(Objects.requireNonNull(event.getGuild()), Settings.Roles.patreonNews);
			if (SimpleRoles.hasRole(getMember(), role)) {
				SimpleRoles.removeRole(getMember(), role);
				removedRoles.add(role);
			} else {
				SimpleRoles.addRole(getMember(), role);
				addedRoles.add(role);
			}
		}

		if (pluginLab) {
			final Role role = SimpleRoles.getRoleById(Objects.requireNonNull(event.getGuild()), Settings.Roles.pluginLab);
			if (SimpleRoles.hasRole(getMember(), role)) {
				SimpleRoles.removeRole(getMember(), role);
				removedRoles.add(role);
			} else {
				SimpleRoles.addRole(getMember(), role);
				addedRoles.add(role);
			}
		}

		if (giveaways) {
			final Role role = SimpleRoles.getRoleById(Objects.requireNonNull(event.getGuild()), Settings.Roles.giveaways);
			if (SimpleRoles.hasRole(getMember(), role)) {
				SimpleRoles.removeRole(getMember(), role);
				removedRoles.add(role);
			} else {
				SimpleRoles.addRole(getMember(), role);
				addedRoles.add(role);
			}
		}

		if (community) {
			if (SimpleRoles.hasRole(getMember(), Settings.Roles.community)) {
				SimpleRoles.removeRole(getMember(), Settings.Roles.community);
			} else {
				SimpleRoles.addRole(getMember(), Settings.Roles.community);
			}
		}

		if (insaneAnnouncer) {
			if (SimpleRoles.hasRole(getMember(), Settings.Roles.insaneAnnouncer)) {
				SimpleRoles.removeRole(getMember(), Settings.Roles.insaneAnnouncer);
			} else {
				SimpleRoles.addRole(getMember(), Settings.Roles.insaneAnnouncer);
			}
		}

		event.replyEmbeds(new SimpleEmbedBuilder("Roles Updated!")
				.text(
						"Your roles have successfully been updated!",
						"Your roles have either been added or removed upon your selection."
				)
				.success().build()
		).setEphemeral(true).queue();


		// Get the selected roles from the event and check if the user has these roles. If they do do nothing if they haven't add them. And than remove the roles that the user doesn't have selected.
        

	}
}
