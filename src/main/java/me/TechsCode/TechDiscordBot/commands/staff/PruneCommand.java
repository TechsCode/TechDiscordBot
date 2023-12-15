package me.techscode.techdiscordbot.commands.staff;

import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PruneCommand extends SimpleSlashCommand {

	public PruneCommand() {
		super("clear");
		description("Clear a certain amount of messages");

		options(
				new OptionData(OptionType.INTEGER, "amount", "The number of messages to clear", true),
				new OptionData(OptionType.CHANNEL, "channel", "The channel to delete messages from", false)
		);
	}

	@Override
	protected void onCommand(final SlashCommandInteractionEvent event) {
		// Get the command sender
		final User executor = event.getUser();

		// Get the guild from the event
		final Guild guild = event.getGuild();
		assert guild != null;

		final int amount = event.getOption("amount").getAsInt();
		final TextChannel channel = event.getOption("channel").getAsChannel().asTextChannel();


		final List<Message> messages = channel.getHistory().retrievePast(amount).complete();

		event.reply("**This will delete " + amount + " messages.**\nAre you sure?")
				.addActionRow(
						new ConfirmButton(executor.getId(), amount).build(),
						new CancelButton(executor.getId()).build()
				).queue();
	}

	/**
	 * The prune confirm button
	 */
	public static class ConfirmButton extends SimpleButton {

		/**
		 * Set the button details
		 */
		public ConfirmButton(final String userId, final int amount) {
			super(userId + ":prune:" + amount);
			label("Confirm");
			buttonStyle(ButtonStyle.SUCCESS);
			disabled(false);
		}

		/**
		 * The execution once the button has been pressed
		 *
		 * @param event ButtonInteractionEvent
		 */
		@Override
		protected void onButtonInteract(@NotNull final ButtonInteractionEvent event) {
			final String[] id = event.getComponentId().split(":");

			final String authorId = id[0];
			final int amount = Integer.parseInt(id[2]);

			if (!authorId.equals(event.getUser().getId()))
				return;

			final MessageChannel channel = event.getChannel();
			event.deferEdit().queue();

			event.getChannel().getIterableHistory()
					.skipTo(event.getMessageIdLong())
					.takeAsync(amount)
					.thenAccept(channel::purgeMessages);
		}
	}

	/**
	 * The prune cancel button
	 */
	public static class CancelButton extends SimpleButton {

		/**
		 * Set the button details
		 */
		public CancelButton(final String userId) {
			super(userId + ":cancelprune");
			label("Cancel");
			buttonStyle(ButtonStyle.DANGER);
			disabled(false);
		}

		/**
		 * The execution once the button has been pressed
		 *
		 * @param event ButtonInteractionEvent
		 */
		@Override
		protected void onButtonInteract(@NotNull final ButtonInteractionEvent event) {
			final String[] id = event.getComponentId().split(":");

			final String authorId = id[0];

			if (!authorId.equals(event.getUser().getId()))
				return;

			event.getHook().deleteOriginal().queue();
		}
	}
}
