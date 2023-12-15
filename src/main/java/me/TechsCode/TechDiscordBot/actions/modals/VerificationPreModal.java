package me.techscode.techdiscordbot.actions.modals;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.handlers.modals.SimpleModal;
import com.greazi.discordbotfoundation.handlers.modals.SimpleTextInput;
import com.greazi.discordbotfoundation.utils.RandomGenerator;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.model.enums.Marketplace;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class VerificationPreModal extends SimpleModal {

	/**
	 * Set the modal details
	 */
	public VerificationPreModal(final String discord_id, final Marketplace marketplace) {
		super("VPR:" + discord_id + ":" + marketplace.getId());
		mainGuildOnly();
		title("Verification | " + marketplace.getName());

		final SimpleTextInput link = new SimpleTextInput("link", "Marketplace profile link");
		link.setRequired();

		final SimpleTextInput images = new SimpleTextInput("images", "Bought plugins list (images)");
		images.setRequired();
		images.setParagraph();
		images.setPlaceholder("Add your images to a system like Imgur and paste the links here");

		textInputs(link, images);

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
		// Get static methods
		final User user = event.getUser();
		final Member member = event.getMember();

		// Get the ID from the modal ID
		final String target_id = event.getModalId().split(":")[1];

		// Check if the member is the person who wants to verify
		if (!user.getId().equals(target_id)) {
			event.reply("Something is going honorably wrong! Please try to verify your purchase again.").setEphemeral(true).queue();
			return;
		}

		final String code = RandomGenerator.string(16);

		// Set up the loading embed.
		final MessageEmbed embed = new SimpleEmbedBuilder("Verification")
				.text(
						"Hi there " + member.getAsMention() + ",",
						"",
						"Before we can verify your purchase, we need to make sure you are the owner of that account.",
						"Please make a post on your marketplace profile with the following message:",
						"`TechVerification." + code + "`",
						"",
						"Thank you for filling in the form! We will review your purchase and get back to you as soon as possible",
						"When your purchase has been reviewed, you will receive a DM from the bot or a mention in <#" + Settings.Modules.Verification.pingChannel + ">"
				).build();

		// get the values from the modal ID
		String marketplace_id = event.getModalId().split(":")[2];
		final Marketplace marketplace = Marketplace.getFromId(marketplace_id);

		// Get the input values
		final String link = event.getValue("link").getAsString();
		final String images = event.getValue("images").getAsString();


		// Send the loading embed and verify the purchase
		event.replyEmbeds(embed).setEphemeral(true).queue();

		// Send the verification request in the manual verification channel
		assert marketplace != null;
		event.getGuild().getTextChannelById(Settings.Modules.Verification.manualVerification).sendMessageEmbeds(
				new SimpleEmbedBuilder("Verification")
						.text("A manual verification request has been made by " + member.getAsMention() + "!")
						.field("User", member.getNickname() + " (" + member.getEffectiveName() + " " + member.getId() + ")", true)
						.field("Marketplace", marketplace.getName(), true)
						.field("Profile Link", link, true)
						.field("Verification Code", code, true)
						.field("Images", images, false)
						.build()
		).queue();
	}
}
