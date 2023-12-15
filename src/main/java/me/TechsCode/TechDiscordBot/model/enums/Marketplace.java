package me.techscode.techdiscordbot.model.enums;

import com.greazi.discordbotfoundation.utils.SimpleRoles;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Role;

public enum Marketplace {

	SPIGOT(1, "Spigot", "https://spigotmc.org", SimpleRoles.getRoleById(TechDiscordBot.getMainGuild(), Settings.Roles.spigot), "spigotmc"),
	BUILTBYBIT(2, "BuiltByBit", "https://builtbybit.com", SimpleRoles.getRoleById(TechDiscordBot.getMainGuild(), Settings.Roles.builtByBit), "mcmarket"),
	SONGODA(3, "Songoda", "https://marketplace.songoda.com", SimpleRoles.getRoleById(TechDiscordBot.getMainGuild(), Settings.Roles.songoda)),
	POLYMART(4, "Polymart", "https://polymart.org", SimpleRoles.getRoleById(TechDiscordBot.getMainGuild(), Settings.Roles.polymart), "polymart");

	final int id;
	final String name;
	final String url;
	final Role role;
	final String paypalApi;

	Marketplace(final int id, final String name, final String url, Role role) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.role = role;
		this.paypalApi = null;
	}

	Marketplace(final int id, final String name, final String url, Role role, String paypalApi) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.role = role;
		this.paypalApi = paypalApi;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public Role getRole() {
		return role;
	}

	public String getPaypalApi() {
		return paypalApi;
	}

	public static Marketplace getFromId(final int id) {
		for (final Marketplace marketplace : values()) {
			if (marketplace.getId() == id) {
				return marketplace;
			}
		}
		return null;
	}

	public static Marketplace getFromId(final String id) {
		int idInt = Integer.parseInt(id);
		return getFromId(idInt);
	}
}
