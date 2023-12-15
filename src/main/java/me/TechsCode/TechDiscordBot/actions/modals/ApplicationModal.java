package me.techscode.techdiscordbot.actions.modals;

import com.greazi.discordbotfoundation.handlers.modals.SimpleModal;
import com.greazi.discordbotfoundation.handlers.modals.SimpleTextInput;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.actions.buttons.ApplyButton;
import me.techscode.techdiscordbot.actions.menus.ApplyMenu;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.model.enums.Application;
import me.techscode.techdiscordbot.modules.ApplyModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ApplicationModal {

    public static class GeneralQuestions extends SimpleModal {

        public GeneralQuestions(@NotNull Member member) {
            super("AGQ-" + member.getIdLong());
            mainGuildOnly();
            title("General Application Questions");

            final SimpleTextInput nameQuestion = new SimpleTextInput("nameage", "What is your name and age?");
            nameQuestion.setRequired();
            nameQuestion.setParagraph();

            final SimpleTextInput timezoneQuestion = new SimpleTextInput("timezone", "What is your timezone?");
            timezoneQuestion.setRequired();
            timezoneQuestion.setParagraph();

            final SimpleTextInput availableTimeQuestion = new SimpleTextInput("time", "Available time per week?");
            availableTimeQuestion.setRequired();
            availableTimeQuestion.setParagraph();

            final SimpleTextInput whyQuestion = new SimpleTextInput("why", "Why do you want to join?");
            whyQuestion.setRequired();
            whyQuestion.setParagraph();

            final SimpleTextInput proConQuestion = new SimpleTextInput("procon", "Pro's and Con's");
            proConQuestion.setRequired();
            proConQuestion.setParagraph();

            textInputs(nameQuestion, timezoneQuestion, availableTimeQuestion, whyQuestion, proConQuestion);
        }

        @Override
        protected void onModalInteract(@NotNull ModalInteractionEvent event) {

            event.getMessage().delete().queue();

            final String nameage = event.getValue("nameage").getAsString();
            final String timezone = event.getValue("timezone").getAsString();
            final String time = event.getValue("time").getAsString();
            final String why = event.getValue("why").getAsString();
            final String procon = event.getValue("procon").getAsString();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("General Application Questions")
                    .field("What is your name and age?", nameage, false)
                    .field("What is your timezone?", timezone, false)
                    .field("Available time per week?", time, false)
                    .field("Why do you want to join?", why, false)
                    .field("Pro's and Con's", procon, false)
                    .build()).queue();

            event.replyEmbeds(new SimpleEmbedBuilder("Which position?")
                    .text("Now that we know a bit more about you, which position would you like to apply for?")
                    .field("Support", "Help people with their problems", false)
                    .field("Developer", "Help our developer team", false)
                    .field("Marketing", "Help our marketing team, maintain our socials and more", false)
                    .field("Translator", "Help our team by translating our plugins", false)
                    .build()
            ).addActionRow(new ApplyMenu(getMember()).build()).queue();

        }
    }

    public static class Support extends SimpleModal {

        public Support(@NotNull Member member) {
            super("ASQ-" + member.getIdLong());
            mainGuildOnly();
            title("Support Application Questions");

            final SimpleTextInput pastQuestion = new SimpleTextInput("past", "Did you help people in the past?");
            pastQuestion.setRequired();
            pastQuestion.setParagraph();

            final SimpleTextInput skillQuestion = new SimpleTextInput("skill", "What plugin are you most skilled in?");
            skillQuestion.setRequired();
            skillQuestion.setParagraph();

            final SimpleTextInput micQuestion = new SimpleTextInput("microphone", "Do you have a microphone?");
            micQuestion.setRequired();
            micQuestion.setParagraph();

            final SimpleTextInput whyQuestion = new SimpleTextInput("why", "Why should we choose you?");
            whyQuestion.setRequired();
            whyQuestion.setParagraph();

            final SimpleTextInput otherQuestion = new SimpleTextInput("other", "Other");
            otherQuestion.setRequired();
            otherQuestion.setParagraph();

            textInputs(pastQuestion, skillQuestion, micQuestion, whyQuestion, otherQuestion);

            this.remove();
        }

        @Override
        protected void onModalInteract(@NotNull ModalInteractionEvent event) {

            event.getMessage().delete().queue();
            event.reply("Success!").setEphemeral(true).queue(message -> message.deleteOriginal().queue());

            final String past = event.getValue("past").getAsString();
            final String skill = event.getValue("skill").getAsString();
            final String mic = event.getValue("microphone").getAsString();
            final String why = event.getValue("why").getAsString();
            final String other = event.getValue("other").getAsString();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Support Application Questions")
                    .field("Did you help people in the past?", past, false)
                    .field("What plugin are you most skilled in?", skill, false)
                    .field("Do you have a microphone?", mic, false)
                    .field("Why should we choose you?", why, false)
                    .field("Other", other, false)
                    .build()).queue();

            ApplyModule.finishApplicationCreation(event.getChannel().asTextChannel(), getMember());

            this.remove();
        }
    }

    public static class Developer extends SimpleModal {

        public Developer(@NotNull Member member) {
            super("ADQ-" + member.getIdLong());
            mainGuildOnly();
            title("Developer Application Questions");

            final SimpleTextInput langQuestion = new SimpleTextInput("lang", "What program languages do you know?");
            langQuestion.setRequired();
            langQuestion.setParagraph();

            final SimpleTextInput timeQuestion = new SimpleTextInput("time", "For how long have you been developing?");
            timeQuestion.setRequired();
            timeQuestion.setParagraph();

            final SimpleTextInput projectQuestion = new SimpleTextInput("projectrun", "Do you know how to run a project?");
            projectQuestion.setRequired();
            projectQuestion.setParagraph();

            final SimpleTextInput gitQuestion = new SimpleTextInput("git", "Do you have a GitHub profile?");
            gitQuestion.setRequired();
            gitQuestion.setParagraph();

            final SimpleTextInput ndaQuestion = new SimpleTextInput("nda", "Would you sign an NDA?");
            ndaQuestion.setRequired();
            ndaQuestion.setParagraph();

            textInputs(langQuestion, timeQuestion, projectQuestion, gitQuestion, ndaQuestion);
        }

        @Override
        protected void onModalInteract(@NotNull ModalInteractionEvent event) {

            event.getMessage().delete().queue();
            event.reply("Success!").setEphemeral(true).queue(message -> message.deleteOriginal().queue());

            final String lang = event.getValue("lang").getAsString();
            final String time = event.getValue("time").getAsString();
            final String project = event.getValue("projectrun").getAsString();
            final String git = event.getValue("git").getAsString();
            final String nda = event.getValue("nda").getAsString();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Developer Application Questions")
                    .field("What program languages do you know?", lang, false)
                    .field("For how long have you been developing?", time, false)
                    .field("Do you know how to run a project?", project, false)
                    .field("Do you have a GitHub profile?", git, false)
                    .field("Would you sign an NDA?", nda, false)
                    .build()).queue();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Final Questions")
                    .text("These are the final questions for your application. Please answer them as good as possible.")
                    .field("Do you have a microphone?", "Do you have a microphone? If so, are you comfortable to get in a voice chat?", false)
                    .field("Why should we choose you?", "Why should we choose you over other applicants?", false)
                    .field("Other", "Feel free to enter any other information you want to share with us.", false)
                    .build()
            ).addActionRow(new ApplyButton.ExitQuestions(getMember()).build()).queue();

            this.remove();
        }
    }

    public static class Marketing extends SimpleModal {

        public Marketing(@NotNull Member member) {
            super("AMQ-" + member.getIdLong());
            mainGuildOnly();
            title("Marketing Application Questions");

            final SimpleTextInput expectation = new SimpleTextInput("expectation", "What do you expect?");
            expectation.setRequired();
            expectation.setParagraph();

            final SimpleTextInput section = new SimpleTextInput("section", "Which section do you prefer?");
            section.setRequired();
            section.setParagraph();

            final SimpleTextInput experience = new SimpleTextInput("experience", "What is your experience?");
            experience.setRequired();
            experience.setParagraph();

            final SimpleTextInput graduation = new SimpleTextInput("graduation", "Do you have a graduation?");
            graduation.setRequired();
            graduation.setParagraph();

            final SimpleTextInput design = new SimpleTextInput("design", "Can you use designer programs?");
            design.setRequired();
            design.setParagraph();

            textInputs(expectation, section, experience, graduation, design);
        }

        @Override
        protected void onModalInteract(@NotNull ModalInteractionEvent event) {

            event.getMessage().delete().queue();
            event.reply("Success!").setEphemeral(true).queue(message -> message.deleteOriginal().queue());

            final String expectation = event.getValue("expectation").getAsString();
            final String section = event.getValue("section").getAsString();
            final String experience = event.getValue("experience").getAsString();
            final String graduation = event.getValue("graduation").getAsString();
            final String design = event.getValue("design").getAsString();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Marketing Application Questions")
                    .field("What do you expect?", expectation, false)
                    .field("Which section do you prefer?", section, false)
                    .field("What is your experience?", experience, false)
                    .field("Do you have a graduation?", graduation, false)
                    .field("Can you use designer programs?", design, false)
                    .build()).queue();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Final Questions")
                    .text("These are the final questions for your application. Please answer them as good as possible.")
                    .field("Do you have a microphone?", "Do you have a microphone? If so, are you comfortable to get in a voice chat?", false)
                    .field("Why should we choose you?", "Why should we choose you over other applicants?", false)
                    .field("Other", "Feel free to enter any other information you want to share with us.", false)
                    .build()
            ).addActionRow(new ApplyButton.ExitQuestions(getMember()).build()).queue();

            this.remove();
        }
    }

    public static class CommunityHelper extends SimpleModal {

        public CommunityHelper(@NotNull Member member) {
            super("ACHQ-" + member.getIdLong());
            mainGuildOnly();
            title("Community Helper Application Questions");

            final SimpleTextInput language = new SimpleTextInput("lang", "What languages do you speak/write?");
            language.setRequired();
            language.setParagraph();

            final SimpleTextInput git = new SimpleTextInput("git", "Do you have a GitHub profile?");
            git.setRequired();
            git.setParagraph();

            final SimpleTextInput microphone = new SimpleTextInput("microphone", "Do you have a microphone?");
            microphone.setRequired();
            microphone.setParagraph();

            final SimpleTextInput why = new SimpleTextInput("why", "Why should we choose you?");
            why.setRequired();
            why.setParagraph();

            final SimpleTextInput other = new SimpleTextInput("other", "Other");
            other.setRequired();
            other.setParagraph();

            textInputs(language, git, microphone, why, other);
        }

        @Override
        protected void onModalInteract(@NotNull ModalInteractionEvent event) {

            event.getMessage().delete().queue();
            event.reply("Success!").setEphemeral(true).queue(message -> message.deleteOriginal().queue());

            final String language = event.getValue("lang").getAsString();
            final String git = event.getValue("git").getAsString();
            final String microphone = event.getValue("microphone").getAsString();
            final String why = event.getValue("why").getAsString();
            final String other = event.getValue("other").getAsString();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Community Helper Application Questions")
                    .field("What languages do you speak/write?", language, false)
                    .field("Do you have a GitHub profile?", git, false)
                    .field("Do you have a microphone?", microphone, false)
                    .field("Why should we choose you?", why, false)
                    .field("Other", other, false)
                    .build()).queue();

            ApplyModule.finishApplicationCreation(event.getChannel().asTextChannel(), getMember());

            this.remove();
        }
    }

    public static class ExitQuestions extends SimpleModal {

        public ExitQuestions(@NotNull Member member) {
            super("AEQ-" + member.getIdLong());
            mainGuildOnly();
            title("Final Application Questions");

            final SimpleTextInput microphone = new SimpleTextInput("microphone", "Do you have a microphone?");
            microphone.setRequired();
            microphone.setParagraph();

            final SimpleTextInput why = new SimpleTextInput("why", "Why should we choose you?");
            why.setRequired();
            why.setParagraph();

            final SimpleTextInput other = new SimpleTextInput("other", "Other");
            other.setRequired();
            other.setParagraph();

            textInputs(microphone, why, other);
        }

        @Override
        protected void onModalInteract(@NotNull ModalInteractionEvent event) {

            event.getMessage().delete().queue();
            event.reply("Success!").setEphemeral(true).queue(message -> message.deleteOriginal().queue());

            final String microphone = event.getValue("microphone").getAsString();
            final String why = event.getValue("why").getAsString();
            final String other = event.getValue("other").getAsString();

            event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Community Helper Application Questions")
                    .field("Do you have a microphone?", microphone, false)
                    .field("Why should we choose you?", why, false)
                    .field("Other", other, false)
                    .build()).queue();

            if (Database.APPLICATIONSTable.get(event.getChannel().getIdLong()).get(0).getCategory() == Application.Position.MARKETING) {
                event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Making an announcement")
                        .text("Now that you have filled in the application, we would like to see how you would make an announcement with the following information.",
                                "",
                                "We are going to release a new plugin in the up coming week called Ultra Economy. There has been asked to make an announcement about this plugin.",
                                "",
                                "The plugin will be released next week and will be available on SpigotMC. The plugin will be €10,-- and will be available for preorder first for a week. After that, the plugin will be released for everyone."
                        )
                        .build()
                ).queue();
            }

            ApplyModule.finishApplicationCreation(event.getChannel().asTextChannel(), getMember());

            this.remove();
        }
    }
}
