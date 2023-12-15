package me.techscode.techdiscordbot.actions.buttons;

import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.TechDiscordBot;
import me.techscode.techdiscordbot.actions.modals.ShutdownModal;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

public class BotActionButtons {

    public static class shutdown extends SimpleButton {
        public shutdown() {
            super("shutdown");
            label("CONFIRM SHUTDOWN");
            buttonStyle(ButtonStyle.DANGER);
            disabled(false);
            mainGuildOnly();

            new Thread(() -> {
                try {
                    Thread.sleep(300000);
                    this.remove();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        @Override
        protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {

            event.replyModal(new ShutdownModal(event.getMember().getId()).build()).queue();
        }
    }

    public static class restart extends SimpleButton {
        public restart() {
            super("restart");
            label("CONFIRM RESTART");
            buttonStyle(ButtonStyle.DANGER);
            disabled(false);
            mainGuildOnly();

            new Thread(() -> {
                try {
                    Thread.sleep(300000);
                    this.remove();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        @Override
        protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {

            event.replyEmbeds(new SimpleEmbedBuilder("Restarting...")
                            .text("The bot is now restarting.")
                    .success().build()).queue();
        }
    }
}
