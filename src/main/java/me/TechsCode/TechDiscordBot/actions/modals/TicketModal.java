package me.techscode.techdiscordbot.actions.modals;

import com.greazi.discordbotfoundation.handlers.modals.SimpleModal;
import com.greazi.discordbotfoundation.handlers.modals.SimpleTextInput;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class TicketModal extends SimpleModal {

	/**
	 * Set the modal details
	 */
	public TicketModal(final String discord_id, final String plugin) {
		super("Ticket:" + discord_id + ":" + plugin);
		mainGuildOnly();
		title("Ticket | " + plugin);

		final SimpleTextInput supportQuestion = new SimpleTextInput("question", "Your support question");
		supportQuestion.setRequired();
		supportQuestion.setParagraph();

		textInputs(supportQuestion);
	}

	/**
	 * The execution once the modal has been submitted
	 *
	 * @param event ModalInteractionEvent
	 */
	@Override
	protected void onModalInteract(@NotNull final ModalInteractionEvent event) {
		// Get the user and member from the event
		final User user = event.getUser();
		final Member member = event.getMember();

		// Get the target ID of the event before doing anything else
		final String target_id = event.getModalId().split(":")[1];

		// Check if the member is the person who requested the ticket
		if (!user.getId().equals(target_id)) {
			event.reply("You can't fill in a modal that is meant for someone else").setEphemeral(true).queue();
			return;
		}

		// Create the loading embed
		final MessageEmbed embed = new SimpleEmbedBuilder("Ticket")
				.text("Creating a new ticket for you...")
				.thumbnail("https://i.imgur.com/9TETTf5.gif")
				.build();

		// Message method, so we can edit the message later on
		event.replyEmbeds(embed).setEphemeral(true).queue(message -> {

			// Get the rest of the information plugin and support question
			final int plugin = Integer.parseInt(event.getModalId().split(":")[2]);
			final String supportQuestion = event.getValue("question").getAsString();

		});
	}
}
