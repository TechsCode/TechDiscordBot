package me.techscode.techdiscordbot.actions.buttons;

import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.utils.SimpleRoles;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.Objects;

public class DeleteButton extends SimpleButton {

    public DeleteButton(String memberId) {
        super("x:" + memberId);
        label("");
        emoji(Emoji.fromFormatted("❌"));
        buttonStyle(ButtonStyle.SECONDARY);
    }

    @Override
    protected void onButtonInteract(ButtonInteractionEvent event) {
        String MemberId = event.getButton().getId().split(":")[1];

        Member member = event.getMember();

        if (member.getId().equals(MemberId) || member.getRoles().contains(Objects.requireNonNull(event.getGuild()).getRoleById(Settings.Roles.staff))) {
            event.getMessage().delete().queue();
            remove();
        } else {
            // Send a message for a quick seconds to show that the button was pressed
            event.reply("You can't delete this message!").setEphemeral(true).queue(message -> message.deleteOriginal().queue());
        }
    }
}
