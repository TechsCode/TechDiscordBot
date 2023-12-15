package me.techscode.techdiscordbot.actions.menus;

import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import com.greazi.discordbotfoundation.handlers.selectmenu.string.SimpleStringSelectMenu;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.model.enums.Plugin;
import me.techscode.techdiscordbot.model.enums.Ticket;
import me.techscode.techdiscordbot.modules.TicketModule;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TicketMenus {

	public static class TicketMenu extends SimpleStringSelectMenu {
		public TicketMenu(@NotNull final Member creator) {
			super("Ticket:" + creator.getId());
			placeholder("Select your ticket type");
			minMax(1, 1);
			options(
					SelectOption.of(Ticket.Category.PLUGIN.getName(), Ticket.Category.PLUGIN.getId())
							.withEmoji(Ticket.Category.PLUGIN.getEmoji())
							.withDescription(Ticket.Category.PLUGIN.getDescription()),

					SelectOption.of(Ticket.Category.PAYMENTS.getName(), Ticket.Category.PAYMENTS.getId())
							.withEmoji(Ticket.Category.PAYMENTS.getEmoji())
							.withDescription(Ticket.Category.PAYMENTS.getDescription()),

					SelectOption.of(Ticket.Category.DEVELOPER.getName(), Ticket.Category.DEVELOPER.getId())
							.withEmoji(Ticket.Category.DEVELOPER.getEmoji())
							.withDescription(Ticket.Category.DEVELOPER.getDescription()),

					SelectOption.of(Ticket.Category.GIVEAWAY.getName(), Ticket.Category.GIVEAWAY.getId())
							.withEmoji(Ticket.Category.GIVEAWAY.getEmoji())
							.withDescription(Ticket.Category.GIVEAWAY.getDescription()),

					SelectOption.of(Ticket.Category.PATREON.getName(), Ticket.Category.PATREON.getId())
							.withEmoji(Ticket.Category.PATREON.getEmoji())
							.withDescription(Ticket.Category.PATREON.getDescription()),

					SelectOption.of(Ticket.Category.OTHER.getName(), Ticket.Category.OTHER.getId())
							.withEmoji(Ticket.Category.OTHER.getEmoji())
							.withDescription(Ticket.Category.OTHER.getDescription()),

					SelectOption.of("Close", "close")
							.withEmoji(Emoji.fromUnicode("❌"))
							.withDescription("Close the ticket")
			);
		}

		@Override
		protected void onMenuInteract(final StringSelectInteraction event) {
			// Get the ID from the modal ID
			final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

			// Check if the member is the person who wants to verify
			if (!this.getMember().getId().equals(target_id)) {
				event.reply("You cannot utilize someone else's menu.").setEphemeral(true).queue();
				return;
			}

			final List<SelectOption> options = event.getSelectedOptions();

			event.getMessage().delete().queue();

			for (final SelectOption option : options) {
				switch (option.getValue().toLowerCase()) {
					case "plugin" -> {
						StringBuilder plugins = new StringBuilder(
								"## You haven't bought any plugins yet. You can verify your purchases in <#907349490556616745>\n" +
								"This means that you **can't** create tickets for premium plugins."
						);
						boolean boughtPlugins = false;
						for (Plugin plugin : Plugin.getOwnedPlugins(getMember())) {
							if (!boughtPlugins) {
								plugins.delete(0, plugins.length());
								boughtPlugins = true;
							}
							plugins.append(plugin.getEmojiRaw()).append(" ").append(plugin.getName()).append("\n");
						}
						event.replyEmbeds(new SimpleEmbedBuilder("📨 Plugin Support")
								.text(
										"Please select the plugin you need support for.",
										"",
										"**Plugins you own:**",
										plugins.length() == 0 ? "You haven't verified any premium plugins. You can verify your plugins in <#907349490556616745>\n" : plugins.toString(),
										"**Free plugins**",
										Plugin.INSANE_ANNOUNCER.getEmojiRaw() + " " + Plugin.INSANE_ANNOUNCER.getName()
								)
								.build()).setActionRow(new PluginMenu(this.getMember()).build()).queue();
						Debugger.debug("Ticket", "Plugin menu opened");
						Database.TICKETS.setCategory(event.getChannel().getIdLong(), Ticket.Category.PLUGIN);
					}
					case "payments" -> {
						event.replyEmbeds(new SimpleEmbedBuilder("📨 Payments")
								.text(
										"Please select a sub category.",
										"",
										"**Sub categories:**",
										Ticket.Payment.PAYPAL.getEmojiRaw() + " " + Ticket.Payment.PAYPAL.getName(),
										Ticket.Payment.MARKETPLACE.getEmojiRaw() + " " + Ticket.Payment.MARKETPLACE.getName(),
										Ticket.Payment.OTHER.getEmojiRaw() + " " + Ticket.Payment.OTHER.getName()
								)
								.build()).setActionRow(new PaymentMenu(this.getMember()).build()).queue();
						Debugger.debug("Ticket", "Payment menu opened");
						Database.TICKETS.setCategory(event.getChannel().getIdLong(), Ticket.Category.PAYMENTS);
					}
					case "developer" -> {
						event.replyEmbeds(new SimpleEmbedBuilder("📨 Developer")
								.text(
										"Please select a sub category.",
										"",
										"**Sub categories:**",
										Ticket.Developer.API.getEmojiRaw() + " " + Ticket.Developer.API.getName(),
										Ticket.Developer.PLUGIN.getEmojiRaw() + " " + Ticket.Developer.PLUGIN.getName(),
										Ticket.Developer.OTHER.getEmojiRaw() + " " + Ticket.Developer.OTHER.getName()
								)
								.build()).setActionRow(new DeveloperMenu(this.getMember()).build()).queue();
						Debugger.debug("Ticket", "Developer menu opened");
						Database.TICKETS.setCategory(event.getChannel().getIdLong(), Ticket.Category.DEVELOPER);
					}
					case "giveaway" -> {
						event.replyEmbeds(new SimpleEmbedBuilder("📨 Giveaways")
								.text(
										"Please select a sub category.",
										"",
										"**Sub categories:**",
										Ticket.Giveaway.CLAIM.getEmojiRaw() + " " + Ticket.Giveaway.CLAIM.getName(),
										Ticket.Giveaway.HOST.getEmojiRaw() + " " + Ticket.Giveaway.HOST.getName()
								)
								.build()).setActionRow(new GiveawayMenu(this.getMember()).build()).queue();
						Debugger.debug("Ticket", "Giveaway menu opened");
						Database.TICKETS.setCategory(event.getChannel().getIdLong(), Ticket.Category.GIVEAWAY);
					}
					case "patreon" -> {
						event.replyEmbeds(new SimpleEmbedBuilder("📨 Patreon")
								.text(
										"Please select a sub category.",
										"",
										"**Sub categories:**",
										Ticket.Patreon.PERKS.getEmojiRaw() + " " + Ticket.Patreon.PERKS.getName(),
										Ticket.Patreon.REWARDS.getEmojiRaw() + " " + Ticket.Patreon.REWARDS.getName(),
										Ticket.Patreon.OTHER.getEmojiRaw() + " " + Ticket.Patreon.OTHER.getName()
								)
								.build()).setActionRow(new PatreonMenu(this.getMember()).build()).queue();
						Debugger.debug("Ticket", "Patreon menu opened");
						Database.TICKETS.setCategory(event.getChannel().getIdLong(), Ticket.Category.PATREON);
					}
					case "other" -> {
						event.replyEmbeds(new SimpleEmbedBuilder("📨 Priority")
								.text(
										"Now that you have created a ticket you will need to set a priority.",
										"The priority only shows the staff team how important the ticket is for you.",
										"",
										"Note that staff members can change the priority at any time, and without any reason.",
										"Please select the priority you want to set for your ticket.",
										"",
										Ticket.Priority.HIGH.getEmojiRaw() + " **High Priority** - Only be used for critical issues",
										Ticket.Priority.MEDIUM.getEmojiRaw() + " **Medium Priority** - For non-critical but important issues",
										Ticket.Priority.LOW.getEmojiRaw() + " **Low Priority** - For non-critical issues",
										Ticket.Priority.NONE.getEmojiRaw() + " **No Priority** - For matters that are neither essential nor important"
								)
								.build()).setActionRow(new PriorityMenu(this.getMember()).build()).queue();
						Debugger.debug("Ticket", "Priority menu opened");
						Database.TICKETS.setCategory(event.getChannel().getIdLong(), Ticket.Category.OTHER);
					}
					case "close" -> {
						TicketModule.ticketClose(event.getMember(), Database.TICKETS.get(event.getChannel().getIdLong()).get(0), event.getTimeCreated().toEpochSecond(), "Ticket creation has been canceled", 30000);
					}
					default -> {
						event.replyEmbeds(new SimpleEmbedBuilder("Ticket ERROR")
								.text(
										"An error occurred while creating your ticket.",
										"Please contact staff for further assistance."
								)
								.error().build()).setEphemeral(true).queue();
						Debugger.debug("Ticket", "Unknown option: " + option.getValue());
					}
				}
			}
			this.remove();
		}
	}

	public static class PluginMenu extends SimpleStringSelectMenu {

		public PluginMenu(@NotNull final Member member) {
			super("TicketPlugin:" + member.getId());
			placeholder("Select the plugin you need support for");
			minMax(1, 1);

			final List<SelectOption> selectOptionsList = new ArrayList<>();
			final List<Role> memberRoles = member.getRoles();

			if (memberRoles.contains(Plugin.ULTRA_PERMISSIONS.getRole())) {
				selectOptionsList.add(SelectOption.of("Ultra Permissions", Plugin.ULTRA_PERMISSIONS.getId() + "")
						.withEmoji(Plugin.ULTRA_PERMISSIONS.getEmoji())
						.withDescription("Create a ticket for Ultra Permissions")
				);
			}

			if (memberRoles.contains(Plugin.ULTRA_CUSTOMIZER.getRole())) {
				selectOptionsList.add(SelectOption.of("Ultra Customizer", Plugin.ULTRA_CUSTOMIZER.getId() + "")
						.withEmoji(Plugin.ULTRA_CUSTOMIZER.getEmoji())
						.withDescription("Create a ticket for Ultra Customizer")
				);
			}

			if (memberRoles.contains(Plugin.ULTRA_ECONOMY.getRole())) {
				selectOptionsList.add(SelectOption.of("Ultra Economy", Plugin.ULTRA_ECONOMY.getId() + "")
						.withEmoji(Plugin.ULTRA_ECONOMY.getEmoji())
						.withDescription("Create a ticket for Ultra Economy")
				);
			}

			if (memberRoles.contains(Plugin.ULTRA_PUNISHMENTS.getRole())) {
				selectOptionsList.add(SelectOption.of("Ultra Punishments", Plugin.ULTRA_PUNISHMENTS.getId() + "")
						.withEmoji(Plugin.ULTRA_PUNISHMENTS.getEmoji())
						.withDescription("Create a ticket for Ultra Punishments")
				);
			}

			if (memberRoles.contains(Plugin.ULTRA_REGIONS.getRole())) {
				selectOptionsList.add(SelectOption.of("Ultra Regions", Plugin.ULTRA_REGIONS.getId() + "")
						.withEmoji(Plugin.ULTRA_REGIONS.getEmoji())
						.withDescription("Create a ticket for Ultra Regions")
				);
			}

			if (memberRoles.contains(Plugin.ULTRA_SCOREBOARDS.getRole())) {
				selectOptionsList.add(SelectOption.of("Ultra Scoreboards", Plugin.ULTRA_SCOREBOARDS.getId() + "")
						.withEmoji(Plugin.ULTRA_SCOREBOARDS.getEmoji())
						.withDescription("Create a ticket for Ultra Scoreboards")
				);
			}

			if (memberRoles.contains(Plugin.ULTRA_MOTD.getRole())) {
				selectOptionsList.add(SelectOption.of("Ultra Motd", Plugin.ULTRA_MOTD.getId() + "")
						.withEmoji(Plugin.ULTRA_MOTD.getEmoji())
						.withDescription("Create a ticket for Ultra Motd")
				);
			}

			if (memberRoles.contains(Plugin.INSANE_SHOPS.getRole())) {
				selectOptionsList.add(SelectOption.of("Insane Shops", Plugin.INSANE_SHOPS.getId() + "")
						.withEmoji(Plugin.INSANE_SHOPS.getEmoji())
						.withDescription("Create a ticket for Insane Shops")
				);
			}

			if (memberRoles.contains(Plugin.INSANE_VAULTS.getRole())) {
				selectOptionsList.add(SelectOption.of("Insane Vaults", Plugin.INSANE_VAULTS.getId() + "")
						.withEmoji(Plugin.INSANE_VAULTS.getEmoji())
						.withDescription("Create a ticket for Insane Vaults")
				);
			}

			selectOptionsList.add(SelectOption.of("Insane Announcer", Plugin.INSANE_ANNOUNCER.getId() + "")
					.withEmoji(Plugin.INSANE_ANNOUNCER.getEmoji())
					.withDescription("Create a ticket for Insane Announcer")
			);
			selectOptionsList.add(SelectOption.of("Back", "back")
					.withEmoji(Emoji.fromUnicode("⏮️"))
					.withDescription("Go back to the previous menu")
			);

			options(selectOptionsList);
		}

		@Override
		protected void onMenuInteract(final StringSelectInteraction event) {
			// Get the ID from the modal ID
			final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

			// Check if the member is the person who wants to verify
			if (!this.getMember().getId().equals(target_id)) {
				event.reply("You cannot utilize someone else's menu.").setEphemeral(true).queue();
				return;
			}

			final List<SelectOption> options = event.getSelectedOptions();

			event.getMessage().delete().queue();

			for (final SelectOption option : options) {
				if (option.getValue().equals("back")) {
					back(event, getMember());
					this.remove();
					return;
				} else {

					Plugin plugin = Plugin.getPluginById(Integer.parseInt(option.getValue()));
					assert plugin != null;
					Database.TICKETS.setType(event.getChannel().getIdLong(), plugin.toString());

					if (SimpleRoles.hasRole(getMember(), Settings.Roles.Patreon.patreon)) {

						// Add the priority to the database
						Database.TICKETS.setPriority(event.getChannel().getIdLong(), Ticket.Priority.PATREON.toString());

						// Send the ticket creation message
						TicketModule.finishTicketCreation(event.getChannel().asTextChannel(), getMember());

						return;
					}

					event.replyEmbeds(new SimpleEmbedBuilder("Ticket creation (3/3)")
							.text(
									"Now that you have created a ticket you will need to set a priority.",
									"The priority only shows the staff team how important the ticket is for you.",
									"",
									"Note that staff members can change the priority at any time, and without any reason.",
									"Please select the priority you want to set for your ticket.",
									"",
									Ticket.Priority.HIGH.getEmojiRaw() + " **High Priority** - Only be used for critical issues",
									Ticket.Priority.MEDIUM.getEmojiRaw() + " **Medium Priority** - For non-critical but important issues",
									Ticket.Priority.LOW.getEmojiRaw() + " **Low Priority** - For non-critical issues",
									Ticket.Priority.NONE.getEmojiRaw() + " **No Priority** - For matters that are neither essential nor important"
							)
							.build()).setActionRow(new PriorityMenu(this.getMember()).build()).queue();
				}
				this.remove();
			}

		}
	}

	public static class PaymentMenu extends SimpleStringSelectMenu {

		public PaymentMenu(@NotNull final Member member) {
			super("TicketPay:" + member.getId());
			placeholder("Select the payment method you need help with");
			minMax(1, 1);

			final List<SelectOption> selectOptionsList = new ArrayList<>();

			selectOptionsList.add(SelectOption.of("Paypal", Ticket.Payment.PAYPAL.getId().toLowerCase())
					.withEmoji(Ticket.Payment.PAYPAL.getEmoji())
					.withDescription(Ticket.Payment.PAYPAL.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Marketplace", Ticket.Payment.MARKETPLACE.getId().toLowerCase())
					.withEmoji(Ticket.Payment.MARKETPLACE.getEmoji())
					.withDescription(Ticket.Payment.MARKETPLACE.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Other Payment", Ticket.Payment.OTHER.getId().toLowerCase())
					.withEmoji(Ticket.Payment.OTHER.getEmoji())
					.withDescription(Ticket.Payment.OTHER.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Back", "back")
					.withEmoji(Emoji.fromUnicode("⏮️"))
					.withDescription("Go back to the previous menu")
			);

			options(selectOptionsList);
		}

		@Override
		protected void onMenuInteract(final StringSelectInteraction event) {
			// Get the ID from the modal ID
			final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

			// Check if the member is the person who wants to verify
			if (!this.getMember().getId().equals(target_id)) {
				event.reply("You cannot utilize someone else's menu.").setEphemeral(true).queue();
				return;
			}

			final List<SelectOption> options = event.getSelectedOptions();

			event.getMessage().delete().queue();

			for (final SelectOption option : options) {
				if (option.getValue().equals("back")) {
					back(event, getMember());
					this.remove();
					return;
				} else {

					event.replyEmbeds(new SimpleEmbedBuilder("Ticket creation (3/3)")
							.text(
									"Now that you have created a ticket you will need to set a priority.",
									"The priority only shows the staff team how important the ticket is for you.",
									"",
									"Note that staff members can change the priority at any time, and without any reason.",
									"Please select the priority you want to set for your ticket.",
									"",
									Ticket.Priority.HIGH.getEmojiRaw() + " **High Priority** - Only be used for critical issues",
									Ticket.Priority.MEDIUM.getEmojiRaw() + " **Medium Priority** - For non-critical but important issues",
									Ticket.Priority.LOW.getEmojiRaw() + " **Low Priority** - For non-critical issues",
									Ticket.Priority.NONE.getEmojiRaw() + " **No Priority** - For matters that are neither essential nor important"
							)
							.build()).setActionRow(new PriorityMenu(this.getMember()).build()).queue();

					Database.TICKETS.setType(event.getChannel().getIdLong(), Ticket.Payment.valueOf(option.getValue().toUpperCase()).getId());
				}
				this.remove();
			}

		}
	}

	public static class DeveloperMenu extends SimpleStringSelectMenu {

		public DeveloperMenu(@NotNull final Member member) {
			super("TicketDev:" + member.getId());
			placeholder("Select what kind of help you need");
			minMax(1, 1);

			final List<SelectOption> selectOptionsList = new ArrayList<>();

			selectOptionsList.add(SelectOption.of("API", Ticket.Developer.API.getId())
					.withEmoji(Ticket.Developer.API.getEmoji())
					.withDescription(Ticket.Developer.API.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Plugin", Ticket.Developer.PLUGIN.getId())
					.withEmoji(Ticket.Developer.PLUGIN.getEmoji())
					.withDescription(Ticket.Developer.PLUGIN.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Other", Ticket.Developer.OTHER.getId())
					.withEmoji(Ticket.Developer.OTHER.getEmoji())
					.withDescription(Ticket.Developer.OTHER.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Back", "back")
					.withEmoji(Emoji.fromUnicode("⏮️"))
					.withDescription("Go back to the previous menu")
			);

			options(selectOptionsList);
		}

		@Override
		protected void onMenuInteract(final StringSelectInteraction event) {
			// Get the ID from the modal ID
			final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

			// Check if the member is the person who wants to verify
			if (!this.getMember().getId().equals(target_id)) {
				event.reply("You cannot utilize someone else's menu.").setEphemeral(true).queue();
				return;
			}

			final List<SelectOption> options = event.getSelectedOptions();

			event.getMessage().delete().queue();

			for (final SelectOption option : options) {
				if (option.getValue().equals("back")) {
					back(event, getMember());
					this.remove();
					return;
				} else {

					event.replyEmbeds(new SimpleEmbedBuilder("Ticket creation (3/3)")
							.text(
									"Now that you have created a ticket you will need to set a priority.",
									"The priority only shows the staff team how important the ticket is for you.",
									"",
									"Note that staff members can change the priority at any time, and without any reason.",
									"Please select the priority you want to set for your ticket.",
									"",
									Ticket.Priority.HIGH.getEmojiRaw() + " **High Priority** - Only be used for critical issues",
									Ticket.Priority.MEDIUM.getEmojiRaw() + " **Medium Priority** - For non-critical but important issues",
									Ticket.Priority.LOW.getEmojiRaw() + " **Low Priority** - For non-critical issues",
									Ticket.Priority.NONE.getEmojiRaw() + " **No Priority** - For matters that are neither essential nor important"
							)
							.build()).setActionRow(new PriorityMenu(this.getMember()).build()).queue();

					Database.TICKETS.setType(event.getChannel().getIdLong(), Ticket.Developer.valueOf(option.getValue().toUpperCase()).getId());
				}
				this.remove();
			}

		}
	}

	public static class GiveawayMenu extends SimpleStringSelectMenu {

		public GiveawayMenu(@NotNull final Member member) {
			super("TicketGiveaway:" + member.getId());
			placeholder("Claim or host a giveaway");
			minMax(1, 1);

			final List<SelectOption> selectOptionsList = new ArrayList<>();

			selectOptionsList.add(SelectOption.of("Claim", Ticket.Giveaway.CLAIM.getId())
					.withEmoji(Ticket.Giveaway.CLAIM.getEmoji())
					.withDescription(Ticket.Giveaway.CLAIM.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Host", Ticket.Giveaway.HOST.getId())
					.withEmoji(Ticket.Giveaway.HOST.getEmoji())
					.withDescription(Ticket.Giveaway.HOST.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Back", "back")
					.withEmoji(Emoji.fromUnicode("⏮️"))
					.withDescription("Go back to the previous menu")
			);

			options(selectOptionsList);
		}

		@Override
		protected void onMenuInteract(final StringSelectInteraction event) {
			// Get the ID from the modal ID
			final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

			// Check if the member is the person who wants to verify
			if (!this.getMember().getId().equals(target_id)) {
				event.reply("You cannot utilize someone else's menu.").setEphemeral(true).queue();
				return;
			}

			final List<SelectOption> options = event.getSelectedOptions();

			event.getMessage().delete().queue();

			for (final SelectOption option : options) {
				if (option.getValue().equals("back")) {
					back(event, getMember());
					this.remove();
					return;
				} else {

					event.replyEmbeds(new SimpleEmbedBuilder("Ticket creation (3/3)")
							.text(
									"Now that you have created a ticket you will need to set a priority.",
									"The priority only shows the staff team how important the ticket is for you.",
									"",
									"Note that staff members can change the priority at any time, and without any reason.",
									"Please select the priority you want to set for your ticket.",
									"",
									Ticket.Priority.HIGH.getEmojiRaw() + " **High Priority** - Only be used for critical issues",
									Ticket.Priority.MEDIUM.getEmojiRaw() + " **Medium Priority** - For non-critical but important issues",
									Ticket.Priority.LOW.getEmojiRaw() + " **Low Priority** - For non-critical issues",
									Ticket.Priority.NONE.getEmojiRaw() + " **No Priority** - For matters that are neither essential nor important"
							)
							.build()).setActionRow(new PriorityMenu(this.getMember()).build()).queue();

					Database.TICKETS.setType(event.getChannel().getIdLong(), Ticket.Giveaway.valueOf(option.getValue().toUpperCase()).getId());
				}
				this.remove();
			}

		}
	}

	public static class PatreonMenu extends SimpleStringSelectMenu {

		public PatreonMenu(@NotNull final Member member) {
			super("TicketPatreon:" + member.getId());
			placeholder("Help for our Patreon program");
			minMax(1, 1);

			final List<SelectOption> selectOptionsList = new ArrayList<>();

			selectOptionsList.add(SelectOption.of("Perks", Ticket.Patreon.PERKS.getId())
					.withEmoji(Ticket.Patreon.PERKS.getEmoji())
					.withDescription(Ticket.Patreon.PERKS.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Rewards", Ticket.Patreon.REWARDS.getId())
					.withEmoji(Ticket.Patreon.REWARDS.getEmoji())
					.withDescription(Ticket.Patreon.REWARDS.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Other", Ticket.Patreon.OTHER.getId())
					.withEmoji(Ticket.Patreon.OTHER.getEmoji())
					.withDescription(Ticket.Patreon.OTHER.getDescription())
			);
			selectOptionsList.add(SelectOption.of("Back", "back")
					.withEmoji(Emoji.fromUnicode("⏮️"))
					.withDescription("Go back to the previous menu")
			);

			options(selectOptionsList);
		}

		@Override
		protected void onMenuInteract(final StringSelectInteraction event) {
			// Get the ID from the modal ID
			final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

			// Check if the member is the person who wants to verify
			if (!this.getMember().getId().equals(target_id)) {
				event.reply("You cannot utilize someone else's menu.").setEphemeral(true).queue();
				return;
			}

			final List<SelectOption> options = event.getSelectedOptions();

			event.getMessage().delete().queue();

			for (final SelectOption option : options) {
				if (option.getValue().equals("back")) {
					back(event, getMember());
					this.remove();
					return;
				} else {

					event.replyEmbeds(new SimpleEmbedBuilder("Ticket creation (3/3)")
							.text(
									"Now that you have created a ticket you will need to set a priority.",
									"The priority only shows the staff team how important the ticket is for you.",
									"",
									"Note that staff members can change the priority at any time, and without any reason.",
									"Please select the priority you want to set for your ticket.",
									"",
									Ticket.Priority.HIGH.getEmojiRaw() + " **High Priority** - Only be used for critical issues",
									Ticket.Priority.MEDIUM.getEmojiRaw() + " **Medium Priority** - For non-critical but important issues",
									Ticket.Priority.LOW.getEmojiRaw() + " **Low Priority** - For non-critical issues",
									Ticket.Priority.NONE.getEmojiRaw() + " **No Priority** - For matters that are neither essential nor important"
							)
							.build()).setActionRow(new PriorityMenu(this.getMember()).build()).queue();

					Database.TICKETS.setType(event.getChannel().getIdLong(), Ticket.Patreon.valueOf(option.getValue().toUpperCase()).getId());
				}
				this.remove();
			}

		}
	}

	public static class PriorityMenu extends SimpleStringSelectMenu {
		public PriorityMenu(@NotNull final Member member) {
			super("TicketPriority:" + member.getId());
			placeholder("Select a priority");
			minMax(1, 1);
			options(
					SelectOption.of("High Priority", "high")
							.withEmoji(Emoji.fromFormatted("<:high_priority:694648884332331008>"))
							.withDescription("Only be used for critical issues"),
					SelectOption.of("Medium Priority", "medium")
							.withEmoji(Emoji.fromFormatted("<:medium_priority:694648883980009593>"))
							.withDescription("For non-critical but important issues"),
					SelectOption.of("Low Priority", "low")
							.withEmoji(Emoji.fromFormatted("<:low_priority:694648884353433601>"))
							.withDescription("For non-critical issues"),
					SelectOption.of("No Priority", "none")
							.withEmoji(Emoji.fromFormatted("<:offline:496493395187990538>"))
							.withDescription("For matters that are neither essential nor important")
			);
		}

		@Override
		protected void onMenuInteract(final StringSelectInteraction event) {
			// Get the ID from the modal ID
			final String target_id = Objects.requireNonNull(event.getSelectMenu().getId()).split(":")[1];

			// Check if the member is the person who wants to verify
			if (!this.getMember().getId().equals(target_id)) {
				event.reply("You cannot utilize someone else's menu.").setEphemeral(true).queue();
				return;
			}

			// Get the options that have been selected
			final List<SelectOption> options = event.getSelectedOptions();

			// Delete the old message
			event.getMessage().delete().queue();

			// Set the priority to default none
			Ticket.Priority priority = Ticket.Priority.NONE;

			// Get the priority
			for (final SelectOption option : options) {
				switch (option.getValue()) {
					case "high":
						priority = Ticket.Priority.HIGH;
						break;
					case "medium":
						priority = Ticket.Priority.MEDIUM;
						break;
					case "low":
						priority = Ticket.Priority.LOW;
						break;
					case "no":
						priority = Ticket.Priority.NONE;
						break;
				}
			}

			// Add the priority to the database
			Database.TICKETS.setPriority(event.getChannel().getIdLong(), priority.toString());

			// Send the ticket creation message
			TicketModule.finishTicketCreation(event.getChannel().asTextChannel(), getMember());

			this.remove();
		}
	}

	private static void back(StringSelectInteraction event, Member member) {
		event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket")
				.text(
						"Please select what kind of ticket you want to create.",
						"",
						"**Available categories:**",
						Ticket.Category.PLUGIN.getEmojiRaw() + " " + Ticket.Category.PLUGIN.getName() + " - " + Ticket.Category.PLUGIN.getDescription(),
						Ticket.Category.PAYMENTS.getEmojiRaw() + " " + Ticket.Category.PAYMENTS.getName() + " - " + Ticket.Category.PAYMENTS.getDescription(),
						Ticket.Category.DEVELOPER.getEmojiRaw() + " " + Ticket.Category.DEVELOPER.getName() + " - " + Ticket.Category.DEVELOPER.getDescription(),
						Ticket.Category.GIVEAWAY.getEmojiRaw() + " " + Ticket.Category.GIVEAWAY.getName() + " - " + Ticket.Category.GIVEAWAY.getDescription(),
						Ticket.Category.PATREON.getEmojiRaw() + " " + Ticket.Category.PATREON.getName() + " - " + Ticket.Category.PATREON.getDescription(),
						Ticket.Category.OTHER.getEmojiRaw() + " " + Ticket.Category.OTHER.getName() + " - " + Ticket.Category.OTHER.getDescription(),
						"",
						"*Please keep in mind that in order to access support for any premium resource, you must first verify your purchase.*"
				)
				.build()
		).setActionRow(new TicketMenu(member).build()).queue();

	}
}
