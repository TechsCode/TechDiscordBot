package me.techscode.techdiscordbot.actions.buttons;

import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.actions.menus.TicketMenus;
import me.techscode.techdiscordbot.database.entities.SqlTicket;
import me.techscode.techdiscordbot.modules.TicketModule;
import me.techscode.techdiscordbot.settings.Settings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class TicketButtons {

	public static class TicketButton extends SimpleButton {

		public TicketButton() {
			super("Ticket_Create");
			emoji(Emoji.fromUnicode("📨"));
			label("Create a ticket");
			buttonStyle(ButtonStyle.SUCCESS);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {

			CompletableFuture<TextChannel> future = TicketModule.ticketCreate(getMember(), getGuild(), getMember());

			future.thenAccept(channel -> {

				if (channel == null) {
					event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Creation Failed")
							.text(
									"Your ticket could not be created.",
									"You have reached the maximum amount of tickets you can create."
							)
							.error().build()).setEphemeral(true).queue();
					return;
				}

				// Send a message to the user
				event.replyEmbeds(new SimpleEmbedBuilder("📨 Ticket Created")
						.text(
								"Your ticket has been created.",
								"To complete the ticket creation process, please follow the steps in " + channel.getAsMention()
						)
						.success().build()).setEphemeral(true).queue();
			});
		}
	}
}
