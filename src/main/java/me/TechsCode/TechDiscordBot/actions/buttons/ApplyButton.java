package me.techscode.techdiscordbot.actions.buttons;

import com.greazi.discordbotfoundation.handlers.buttons.SimpleButton;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.actions.modals.ApplicationModal;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static me.techscode.techdiscordbot.modules.ApplyModule.applicationCreate;

public class ApplyButton {

	public static class Button extends SimpleButton {

		public Button() {
			super("Apply_Create");
			label("Create Application");
			emoji(Emoji.fromUnicode("📝"));
			buttonStyle(ButtonStyle.SUCCESS);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {

			CompletableFuture<TextChannel> future = applicationCreate(getMember(), getGuild());
			future.thenAccept(channel -> {
				event.replyEmbeds(new SimpleEmbedBuilder("Application Created")
						.text(
								"A new application has been created.",
								"Please follow the steps in " + channel.getAsMention()
						)
						.success().build()).setEphemeral(true).queue();
			});
		}
	}

	public static class GeneralQuestionButton extends SimpleButton {

		public GeneralQuestionButton(@NotNull Member member) {
			super("AGQ_" + member.getIdLong());
			label("Answer Questions");
			emoji(Emoji.fromUnicode("❓"));
			buttonStyle(ButtonStyle.PRIMARY);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {
			event.replyModal(new ApplicationModal.GeneralQuestions(getMember()).build()).queue();
			this.remove();
		}
	}

	public static class SupportQuestions extends SimpleButton {

		public SupportQuestions(@NotNull Member member) {
			super("AGQS_" + member.getIdLong());
			label("Answer Questions");
			emoji(Emoji.fromUnicode("❓"));
			buttonStyle(ButtonStyle.PRIMARY);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {
			event.replyModal(new ApplicationModal.Support(getMember()).build()).queue();
			this.remove();
		}
	}

	public static class DeveloperQuestions extends SimpleButton {

		public DeveloperQuestions(@NotNull Member member) {
			super("AGQS_" + member.getIdLong());
			label("Answer Questions");
			emoji(Emoji.fromUnicode("❓"));
			buttonStyle(ButtonStyle.PRIMARY);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {
			event.replyModal(new ApplicationModal.Developer(getMember()).build()).queue();
			this.remove();
		}
	}

	public static class MarketingQuestions extends SimpleButton {

		public MarketingQuestions(@NotNull Member member) {
			super("AGQS_" + member.getIdLong());
			label("Answer Questions");
			emoji(Emoji.fromUnicode("❓"));
			buttonStyle(ButtonStyle.PRIMARY);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {
			event.replyModal(new ApplicationModal.Marketing(getMember()).build()).queue();
			this.remove();
		}
	}

	public static class CommunityHelperQuestions extends SimpleButton {

		public CommunityHelperQuestions(@NotNull Member member) {
			super("AGQS_" + member.getIdLong());
			label("Answer Questions");
			emoji(Emoji.fromUnicode("❓"));
			buttonStyle(ButtonStyle.PRIMARY);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {
			event.replyModal(new ApplicationModal.CommunityHelper(getMember()).build()).queue();
			this.remove();
		}
	}

	public static class ExitQuestions extends SimpleButton {

		public ExitQuestions(@NotNull Member member) {
			super("AGQE_" + member.getIdLong());
			label("Answer Questions");
			emoji(Emoji.fromUnicode("❓"));
			buttonStyle(ButtonStyle.PRIMARY);
			disabled(false);
			mainGuildOnly();
		}

		@Override
		protected void onButtonInteract(final @NotNull ButtonInteractionEvent event) {
			event.replyModal(new ApplicationModal.ExitQuestions(getMember()).build()).queue();
			this.remove();
		}
	}

}
