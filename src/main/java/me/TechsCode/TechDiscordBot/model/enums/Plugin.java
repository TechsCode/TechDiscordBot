package me.techscode.techdiscordbot.model.enums;

import com.greazi.discordbotfoundation.utils.SimpleRoles;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum Plugin {

	ULTRA_PERMISSIONS(1, "Ultra Permissions", "ultrapermissions", "Uperms", "<:UltraPermissions:576937960176353293>", Settings.Roles.ultraPermissions),
	ULTRA_CUSTOMIZER(2, "Ultra Customizer", "ultracustomizer", "UC", "<:UltraCustomizer:576873108506411018>", Settings.Roles.ultraCustomizer),
	ULTRA_PUNISHMENTS(3, "Ultra Punishments", "ultrapunishments", "UPun", "<:UltraPunishments:576873108422524954>", Settings.Roles.ultraPunishments),
	ULTRA_REGIONS(4, "Ultra Regions", "ultraregions", "UR", "<:UltraRegions:639288786236473354>", Settings.Roles.ultraRegions),
	ULTRA_ECONOMY(5, "Ultra Economy", "ultraeconomy", "UE", "<:UltraEconomy:748004373971337216>", Settings.Roles.ultraEconomy),
	ULTRA_SCOREBOARDS(6, "Ultra Scoreboards", "ultrascoreboards", "UBoards", "<:UltraScoreboards:843611772481044520>", Settings.Roles.ultraScoreboards),
	ULTRA_MOTD(7, "Ultra Motd", "ultramotd", "UMotd", "<:UltraMotd:947812638509912105>", Settings.Roles.ultraMotd),
	INSANE_SHOPS(8, "Insane Shops", "insaneshops", "IShops", "<:InsaneShops:576871756816449536>", Settings.Roles.insaneShops),
	INSANE_VAULTS(9, "Insane Vaults", "insanevaults", "IVaults", "<:InsaneVaults:1059878327839621243>", Settings.Roles.insaneVaults),
	INSANE_ANNOUNCER(10, "Insane Announcer", "insaneannouncer", "IAnnouncer", "<:InsaneAnnouncer:741465636231970886>");

	final int id;
	final String name;
	final String lowerName;
	final String shortName;
	final String emoji;
	final long roleId;

	Plugin(final int id, final String name, final String lowerName, final String shortName, final String emoji) {
		this(id, name, lowerName, shortName, emoji, 0);
	}

	Plugin(final int id, final String name, final String lowerName, final String shortName, final String emoji, final long roleId) {
		this.id = id;
		this.name = name;
		this.lowerName = lowerName;
		this.shortName = shortName;
		this.emoji = emoji;
		this.roleId = roleId;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getLowerName() {
		return this.lowerName;
	}

	public String getShortName() {
		return this.shortName;
	}

	@NotNull
	public Emoji getEmoji() {
		return Emoji.fromFormatted(this.emoji);
	}

	public String getEmojiRaw() {
		return this.emoji;
	}

	public Role getRole() {
		return SimpleRoles.getRoleById(TechDiscordBot.getMainGuild(), this.roleId);
	}

	@Nullable
	public static Plugin getPluginById(final int id) {
		for (final Plugin plugin : values()) {
			if (plugin.getId() == id) return plugin;
		}

		return null;
	}

	@Nullable
	public static Plugin getPluginByName(final String name) {
		for (final Plugin plugin : values()) {
			if (plugin.getName().equalsIgnoreCase(name)) return plugin;
		}

		return null;
	}

	public static Plugin getPluginByLowerName(final String lowerName) {
		for (final Plugin plugin : values()) {
			if (plugin.getLowerName().equalsIgnoreCase(lowerName)) return plugin;
		}

		return null;
	}

	@Nullable
	public static Plugin getPluginByShortName(final String shortName) {
		for (final Plugin plugin : values()) {
			if (plugin.getShortName().equalsIgnoreCase(shortName)) return plugin;
		}

		return null;
	}

	@Nullable
	public static Plugin getPluginByRole(final Role role) {
		for (final Plugin plugin : values()) {
			if (plugin.getRole() != null && plugin.getRole().equals(role)) return plugin;
		}

		return null;
	}

	@Nullable
	public static Plugin getPluginByEmoji(final String emoji) {
		for (final Plugin plugin : values()) {
			if (plugin.getEmojiRaw().equalsIgnoreCase(emoji)) return plugin;
		}

		return null;
	}

	@Nullable
	public static Plugin getPluginByEmoji(final Emoji emoji) {
		for (final Plugin plugin : values()) {
			if (plugin.getEmoji().equals(emoji)) return plugin;
		}

		return null;
	}

	public static List<Plugin> getOwnedPlugins(Member member) {
		List<Plugin> ownedPlugins = new ArrayList<>();

		SimpleRoles.getAllMemberRoles(member).stream().filter(role -> getPluginByRole(role) != null).forEach(role -> {
			Plugin plugin = getPluginByRole(role);
			if (plugin != null) {
				ownedPlugins.add(plugin);
			}
		});

		return ownedPlugins;
	}
}
