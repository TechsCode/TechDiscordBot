package me.techscode.techdiscordbot.actions.modals;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.handlers.modals.SimpleModal;
import com.greazi.discordbotfoundation.handlers.modals.SimpleTextInput;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.database.entities.SqlTicket;
import me.techscode.techdiscordbot.model.enums.Marketplace;
import me.techscode.techdiscordbot.model.enums.Ticket;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The Verification Modal
 */
public class VerificationModal extends SimpleModal {

	/**
	 * Set the modal details
	 */
	public VerificationModal(final String discord_id, final Marketplace marketplace) {
		super("Verification:" + discord_id + ":" + marketplace.getId());
		mainGuildOnly();
		title("Verification | " + marketplace.getName());

		final SimpleTextInput emailInput = new SimpleTextInput("email", "Paypal Email address");
		emailInput.setRequired();

		final SimpleTextInput transactionInput = new SimpleTextInput("transaction", "Transaction ID (Only 1)");
		transactionInput.setRequired();
		transactionInput.setMinMaxLength(17, 19);

		final SimpleTextInput marketplaceInput = new SimpleTextInput("link", "Marketplace profile link");
		marketplaceInput.setRequired();
		marketplaceInput.setMinMaxLength(35, 100);

		textInputs(emailInput, transactionInput, marketplaceInput);

		// Remove the button with this.remove() after 10 minutes of creation of the button
		new Thread(() -> {
			try {
				Thread.sleep(600000);
				this.remove();
			} catch (InterruptedException e) {
				Common.throwError(e, "Error while removing button");
			}
		}).start();
	}

	/**
	 * The execution once the modal has been submitted
	 *
	 * @param event ModalInteractionEvent
	 */
	@Override
	protected void onModalInteract(@NotNull final ModalInteractionEvent event) {

		// Get the ID from the modal ID
		final String target_id = event.getModalId().split(":")[1];

		// Check if the member is the person who wants to verify
		if (!this.getMember().getId().equals(target_id)) {
			event.reply("Something is going honorably wrong! Please try to verify your purchase again.").setEphemeral(true).queue();
			return;
		}

		// Get the input values
		final String email = event.getValue("email").getAsString();
		final String transaction = event.getValue("transaction").getAsString();
		final String link = event.getValue("link").getAsString();

		Guild guild = event.getGuild();
		Member owner = event.getMember();

		// Permissions list for the ticket channel
		final List<Permission> permissions = new ArrayList<>(
				Arrays.asList(
						Permission.VIEW_CHANNEL,
						Permission.MESSAGE_HISTORY,
						Permission.MESSAGE_SEND,
						Permission.MESSAGE_ATTACH_FILES,
						Permission.MESSAGE_EMBED_LINKS,
						Permission.MESSAGE_ADD_REACTION,
						Permission.MANAGE_EMOJIS_AND_STICKERS
				)
		);

		// Get the members name if the members name can't be gathered it will use the user's ID
		final String memberName;
		if (Objects.equals(owner.getNickname(), "") || owner.getNickname() == null) {
			if (owner.getEffectiveName().equals("")) {
				memberName = owner.getUser().getId();
			} else {
				owner.getEffectiveName();
				memberName = owner.getEffectiveName();
			}
		} else {
			memberName = owner.getNickname();
		}

		// Get some settings from the settings file
		final Category category = guild.getCategoryById("1168185109543931964");
		final long staffRole = Settings.Roles.support;

		// Create the application channel for the member
		guild.createTextChannel("verification-" + memberName, category)
				.addPermissionOverride(guild.getPublicRole(), null, permissions)
				// Add staff permissions
				.addRolePermissionOverride(staffRole, permissions, null)
				// Add member permissions
				.addMemberPermissionOverride(owner.getIdLong(), permissions, Collections.singleton(Permission.MESSAGE_SEND))
				.queue(channel -> {

					channel.sendMessageEmbeds(new SimpleEmbedBuilder("📨 Ticket")
							.text(
									"Thank you for your verification request!",
									"",
									"Please wait for a staff member to respond to your ticket.",
									"",
									"**Please note that this process can take up to 24 hours.**",
									"",
									"Email: `" + email + "`",
									"Transaction ID: `" + transaction + "`",
									"Marketplace: `" + link + "`"
							)
							.build()
					).queue();

					new SqlTicket(this.getUser().getIdLong(), channel.getIdLong(), channel.getTimeCreated().toEpochSecond(), Ticket.Category.PAYMENTS.getId(), Ticket.Payment.VERIFICATION.getId(), Ticket.Priority.HIGH.getId()).save();

					event.replyEmbeds(new SimpleEmbedBuilder("Verification Created")
							.text("Your verification ticket has been created! Please wait for a staff member to respond to your ticket.",
									"",
									"Your verification request: " + channel.getAsMention()
							)
							.build()).setEphemeral(true).queue();

					channel.sendMessage("<@&" + Settings.Roles.support + ">").queue();
				});

		/*
		// Set up the loading embed.
		final MessageEmbed embed = new SimpleEmbedBuilder("Verification")
				.text("Verifying your details...")
				.thumbnail("https://i.imgur.com/jcuD6gb.gif")
				.build();

		// get the values from the modal ID
		final Marketplace marketplace = Marketplace.getFromId(Integer.parseInt(event.getModalId().split(":")[2]));

		/*event.replyEmbeds(new SimpleEmbedBuilder("Verification")
				.text(
						"Thank you for filling in the form.",
						"",
						"We will verify your purchase as soon as possible.",
						"",
						"*Please note that this process can take up to 24 hours.*",
						"*Also note that if the transaction ID does not match the email address, the verification will be denied.*"
				).success().build()).setEphemeral(true).queue();

		event.getGuild().getTextChannelById(Settings.Modules.Verification.manualVerification).sendMessageEmbeds(
				new SimpleEmbedBuilder("Verification")
						.text("A manual verification request has been made by " + getMember().getAsMention() + "!")
						.field("User", getMember().getAsMention() + " (" + getMember().getId() + ")", true)
						.field("Marketplace", Marketplace.getNamed(marketplace), true)
						.field("Profile link", link, true)
						.field("Transaction ID", transaction, true)
						.field("E-mail", email, false)
						.build()
		).queue();*/

		/*final List<String> linkComponents = List.of(link.split("/"));
		final int marketplaceId = Integer.parseInt(linkComponents.get(linkComponents.size() - 1).split("\\.")[1]);

		Debugger.debug("Verification", "Marketplace ID: " + marketplaceId);

		// !!! DISABLED !!! due to the fact that the API is not yet ready
		// Send the loading embed and verify the purchase
		event.replyEmbeds(embed).setEphemeral(true).queue(message -> {
			if (Paypal.verify(this.getMember(), message, email, marketplaceId, transaction, marketplace)) {
				Paypal.getPurchases(message, this.getMember(), email, marketplace, marketplaceId);
			}
		});*/
	}
}