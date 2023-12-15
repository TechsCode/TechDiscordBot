package me.techscode.techdiscordbot.actions.buttons.verification;

import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import me.techscode.techdiscordbot.actions.modals.VerificationModal;
import me.techscode.techdiscordbot.model.enums.Marketplace;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class VerifyButton {

    public static class Button extends SimpleButton {

        public Button() {
            super("Verification");
            label("Verify Purchase");
            buttonStyle(ButtonStyle.PRIMARY);
            mainGuildOnly();
        }

        @Override
        protected void onButtonInteract(ButtonInteractionEvent event) {
            event.replyModal(new VerificationModal(this.getUser().getId(), Marketplace.SPIGOT).build()).queue();
        }
    }
}
