package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.database.TranscriptDatabase;
import me.techscode.techdiscordbot.database.entities.SqlPreorder;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PreorderModule {

	/*public static void CheckPreorders() {
		if (Settings.Modules.Preorder.enabled) {
			int preorders = 0;
			int newAdded = 0;

			for (final SqlPreorder sqlPreorder : TranscriptDatabase.PREORDERS.getAll()) {
				final long discordId = sqlPreorder.getDiscordId();
				final Member member = TechDiscordBot.getMainGuild().getMember(UserSnowflake.fromId(discordId));

				if (member == null) {
					Debugger.debug("PreorderModule", "Member with ID " + discordId + " is null.");
					preorders++;
					continue;
				}

				final List<Role> roles = member.getRoles();
				final List<Long> roleIds = roles.stream().map(Role::getIdLong).collect(Collectors.toList());

				if (roleIds.contains(Settings.Roles.preorder) && sqlPreorder.getTransactionId().equalsIgnoreCase("none")) {
					Debugger.debug("PreorderModule", "Member with ID " + discordId + " has the preorder role but no transaction ID. Removing role.");
					SimpleRoles.removeRole(member, Settings.Roles.Patreon.patreon);
					preorders++;
				} else if (!sqlPreorder.getTransactionId().equalsIgnoreCase("none")) {
					if (!member.getRoles().contains(SimpleRoles.getRoleById(member.getGuild(), Settings.Roles.preorder))) {
						preorders++;
						newAdded++;

						SimpleRoles.addRole(member, Settings.Roles.preorder);

						Debugger.debug("PreorderModule", "Member with ID " + discordId + " has a transaction ID but no preorder role. Adding role.");
						final TextChannel preorderChannel = TechDiscordBot.getJDA().getTextChannelById(Settings.Modules.Preorder.channel);

						assert preorderChannel != null;
						preorderChannel.sendMessageEmbeds(new SimpleEmbedBuilder("Insane Vaults Preorder")
								.text(
										"**" + member.getAsMention() + " thank you for pre-ordering Insane Vaults!**",
										"",
										"Your pre-order has been verified and you have been given the " + SimpleRoles.getRoleById(member.getGuild(), Settings.Roles.preorder).getAsMention() + " role.",
										"",
										"You can now access <#1058612057576054864> for your support questions.",
										"To download the latest build of Insane Vaults go to the preorder page **[HERE](https://preorder.insanevaults.com/)**"
								)
								.color(Color.getColor("#e74c3c"))
								.thumbnail("https://cloud.techscode.com/s/J6SaqeQRrErQwft/preview")
								.build()
						).queue();
						Common.log("Added Preorder role to " + member.getUser().getAsTag() + " and sent them a message.");

					} else {
						Debugger.debug("PreorderModule", "Member with ID " + discordId + " has a transaction ID and the preorder role. Skipping.");
						member.getGuild().addRoleToMember(member, SimpleRoles.getRoleById(member.getGuild(), Settings.Roles.preorder)).queue();
						preorders++;
					}
				}
			}
			Common.log("Preorder system finished checking preorders. " + preorders + " preorders found and " + newAdded + " new preorders added.");
		}
	}*/
}
