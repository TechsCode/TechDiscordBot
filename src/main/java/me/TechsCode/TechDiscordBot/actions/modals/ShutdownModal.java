package me.techscode.techdiscordbot.actions.modals;

import com.greazi.discordbotfoundation.handlers.modals.SimpleModal;
import com.greazi.discordbotfoundation.handlers.modals.SimpleTextInput;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ShutdownModal extends SimpleModal {

    public ShutdownModal(final String discord_id) {
        super("Shutdown:" + discord_id);
        mainGuildOnly();
        title("Confirm Shutdown");

        final SimpleTextInput confirmQuestion = new SimpleTextInput("confirm", "Type in \"Confirm\" to confirm the shutdown.");
        confirmQuestion.setRequired();
        confirmQuestion.setMinMaxLength(7, 7);

        textInputs(confirmQuestion);
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

        // Get the target ID of the event before doing anything else
        final String target_id = event.getModalId().split(":")[1];

        // Check if the member is the person who requested the ticket
        if (!user.getId().equals(target_id)) {
            event.reply("You can't fill in a modal that is meant for someone else").setEphemeral(true).queue();
            return;
        }

        if (!Objects.requireNonNull(event.getValue("confirm")).getAsString().equals("CONFIRM")) {
            event.reply("You didn't type in \"Confirm\"!").setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(new SimpleEmbedBuilder("Shutdown")
                .text(
                        "Shutting down...",
                        "Thank you for using Tech's Code Discord Bot!"
                        )
                .build()).setEphemeral(true).queue();
    }
}
