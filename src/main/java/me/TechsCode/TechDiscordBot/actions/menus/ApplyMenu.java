package me.techscode.techdiscordbot.actions.menus;

import com.greazi.discordbotfoundation.handlers.selectmenu.string.SimpleStringSelectMenu;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.actions.buttons.ApplyButton;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.model.enums.Application;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ApplyMenu extends SimpleStringSelectMenu {
    public ApplyMenu(@NotNull Member member) {
        super("APM_" + member.getIdLong());
        placeholder("Select your position");
        minMax(1, 1);
        
        options(
                SelectOption.of(Application.Position.SUPPORT.getName(), Application.Position.SUPPORT.getId())
                        .withEmoji(Application.Position.SUPPORT.getEmoji())
                        .withDescription(Application.Position.SUPPORT.getDescription()),

                SelectOption.of(Application.Position.DEVELOPER.getName(), Application.Position.DEVELOPER.getId())
                        .withEmoji(Application.Position.DEVELOPER.getEmoji())
                        .withDescription(Application.Position.DEVELOPER.getDescription()),

                SelectOption.of(Application.Position.MARKETING.getName(), Application.Position.MARKETING.getId())
                        .withEmoji(Application.Position.MARKETING.getEmoji())
                        .withDescription(Application.Position.MARKETING.getDescription()),

                SelectOption.of(Application.Position.COMMUNITY_HELPER.getName(), Application.Position.COMMUNITY_HELPER.getId())
                        .withEmoji(Application.Position.COMMUNITY_HELPER.getEmoji())
                        .withDescription(Application.Position.COMMUNITY_HELPER.getDescription())
        );
    }

    @Override
    protected void onMenuInteract(@NotNull StringSelectInteraction event) {
        final List<SelectOption> options = event.getSelectedOptions();

       for (SelectOption option : options) {
           Application.Position category = Application.Position.getById(option.getValue());

           if (category == null) {
             event.reply("An error occurred while processing your request.").setEphemeral(true).queue();
             return;
           }

           event.getMessage().delete().queue();

           if (category == Application.Position.SUPPORT) {
               event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Support Application")
                       .text("Please fill out the following form to apply for the Support position.")
                       .field("Did you help people in the past?", "Have you helped people in the past? If so, please explain how you helped them and what you did to help them.", false)
                       .field("What plugin are you most skilled in?", "Please explain what plugin you are most skilled in and why you are most skilled in it.", false)
                       .field("Do you have a microphone?", "Do you have a microphone? If so, are you comfortable to get in a voice chat?", false)
                       .field("Why should we choose you?", "Why should we choose you over other applicants?", false)
                       .field("Other", "Feel free to enter any other information you want to share with us.", false)
                       .build()
               ).addActionRow(new ApplyButton.SupportQuestions(getMember()).build()).queue();
               Database.APPLICATIONSTable.setPosition(event.getChannel().getIdLong(), Application.Position.SUPPORT);

           } else if (category == Application.Position.DEVELOPER) {
               event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Developer Application")
                       .text("Please fill out the following form to apply for the Developer position.")
                       .field("What program languages do you know?", "Please explain what program languages you know and what you are most skilled in.", false)
                       .field("For how long have you been developing?", "How long have you been coding either plugins, websites or other programs?", false)
                       .field("Do you know how to run a project?", "Did you ever run a project in your past? If so, please explain how you ran it and what you did to run it.", false)
                       .field("Do you have a GitHub profile?", "Do you have a GitHub profile? If so, please provide a link to it.", false)
                       .field("Would you sign an NDA?", "Would you sign an NDA (Non-Disclosure Agreement) if we asked you to?", false)
                       .field("Do you have a microphone?", "Do you have a microphone? If so, are you comfortable to get in a voice chat?", false)
                       .field("Why should we choose you?", "Why should we choose you over other applicants?", false)
                       .field("Other", "Feel free to enter any other information you want to share with us.", false)
                       .build()
               ).addActionRow(new ApplyButton.DeveloperQuestions(getMember()).build()).queue();
               Database.APPLICATIONSTable.setPosition(event.getChannel().getIdLong(), Application.Position.DEVELOPER);

           } else if (category == Application.Position.MARKETING) {
               event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Marketing Application")
                       .text("Please fill out the following form to apply for the Marketing position.")
                       .field("What do you expect?", "What do you think you are going to do in our Marketing team?", false)
                       .field("Which section do you prefer?", "What section in the marketing branch do you prefer to work?", false)
                       .field("What is your experience?", "What have you done in the past for activities related to this position?", false)
                       .field("Do you have a graduation?", "Do you have a marketing related graduation? *(not required)*", false)
                       .field("Can you use designer programs?", "Can you use any tools for designing and such. If so, what are these? *(anything from video editing to photo editing)*", false)
                       .field("Do you have a microphone?", "Do you have a microphone? If so, are you comfortable to get in a voice chat?", false)
                       .field("Why should we choose you?", "Why should we choose you over other applicants?", false)
                       .field("Other", "Feel free to enter any other information you want to share with us.", false)
                       .build()
               ).addActionRow(new ApplyButton.MarketingQuestions(getMember()).build()).queue();
               Database.APPLICATIONSTable.setPosition(event.getChannel().getIdLong(), Application.Position.MARKETING);

           } else if (category == Application.Position.COMMUNITY_HELPER) {
               event.getChannel().sendMessageEmbeds(new SimpleEmbedBuilder("Developer Application")
                       .text("Please fill out the following form to apply for the Developer position.")
                       .field("What languages do you speak/write?", "Give a list of languages you speak/write", false)
                       .field("Do you have a GitHub profile?", "Do you have a GitHub profile? If so, please provide a link to it.", false)
                       .field("Do you have a microphone?", "Do you have a microphone? If so, are you comfortable to get in a voice chat?", false)
                       .field("Why should we choose you?", "Why should we choose you over other applicants?", false)
                       .field("Other", "Feel free to enter any other information you want to share with us.", false)
                       .build()
               ).addActionRow(new ApplyButton.CommunityHelperQuestions(getMember()).build()).queue();
               Database.APPLICATIONSTable.setPosition(event.getChannel().getIdLong(), Application.Position.COMMUNITY_HELPER);
           }
       }
    }
}
