package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import me.techscode.techdiscordbot.actions.buttons.DeleteButton;
import me.techscode.techdiscordbot.model.Pastebin;
import me.techscode.techdiscordbot.settings.Settings;
import me.techscode.techdiscordbot.utils.ProjectUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageReceive extends ListenerAdapter {

    @SubscribeEvent
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        this.log(event);

        this.bugReport(event);

        this.suggestion(event);

        //this.vacationGreazi(event);

        this.staffEmbed(event);

        this.messageAsBot(event);

        this.pastebinCheck(event);

        this.fileReceived(event);

        this.support(event);
    }

    private void support(@NotNull MessageReceivedEvent event) {
        long categoryId = event.getGuildChannel().getIdLong();
        if (categoryId == 457963200567181323L || categoryId == 696130901293400064L) {
            String message = event.getMessage().getContentRaw().toLowerCase();

            // Regular expressions for plugin abbreviations, full names, and keywords
            String pluginAbbreviations = "UC|UP|UPun|UE|UR|UMotd|US|UBoards|IShops|IV|ISpwaners";
            String fullNames = "Ultra Customizer|Ultra Punishments|Ultra Permissions|Ultra Economy|Ultra Regions|Ultra Motd|Ultra Scoreboards|Insane Shops|Insane Vaults|Insane Spawners";
            String supportKeywords = "help|support|assistance|assist|need";

            // Combine all expressions into one pattern
            String combinedPattern = String.format("(%s)|(%s)|(%s)", pluginAbbreviations, fullNames, supportKeywords);
            Pattern pattern = Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);

            // Create a Matcher to find matches in the message
            Matcher matcher = pattern.matcher(message);

            // Check if any plugin abbreviation, full name, or support keyword is found in the message
            if (matcher.matches()) {
                // Send a reply asking the user to verify their purchase
                event.getChannel().asTextChannel().sendMessageEmbeds(new SimpleEmbedBuilder()
                        .text("Hello, " + event.getMember().getAsMention() + "! I've detected that you might be trying to get help in this channel! Please verify in <#907349490556616745> in order to get help, thanks!\n\n*If you are not trying to get help, you can delete this message by reacting to it!*")
                        .error().build()
                ).addActionRow(new DeleteButton(event.getMember().getId()).build()).queue();
            }
        }
    }

    /**
     * Chat log to the console
     * @param event MessageReceivedEvent
     */
    private void log(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String primary = "";

        try {
            primary = event.getGuildChannel().asStandardGuildChannel().getParentCategory().getName();
        } catch (Exception e) {
            primary = event.getGuildChannel().asThreadChannel().getParentChannel().getName();
        }
        Common.logNoPrefix(ConsoleColor.PURPLE + "[" + (primary != null ? primary : "") + "/" + event.getChannel().getName() + "] " + ConsoleColor.CYAN + event.getAuthor().getName() + ConsoleColor.WHITE + ": " + event.getMessage().getContentRaw());
    }

    private void pastebinCheck(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase();

        if (message.contains("send in") && message.contains("pastebin")) {
            event.getChannel().asTextChannel().sendMessage("https://paste.techscode.com/").queue();
        }


    }

    /**
     * Staff embed system
     * @param event MessageReceivedEvent
     */
    private void staffEmbed(@NotNull MessageReceivedEvent event) {

        // Get the message
        String message = event.getMessage().getContentRaw();

        // Check if the message starts with the "^ " prefix
        if (message.startsWith("^ ")) {

            // Check if the member has the staff role
            if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Settings.Roles.staff))) {

                // Delete the original message
                event.getMessage().delete().queue();

                String text = message.substring(2);
                String[] arguments = text.split("\\^");

                if (arguments.length != 2 && arguments.length != 3 && arguments.length != 4) {
                    new SimpleEmbedBuilder("Invalid Arguments")
                            .text(
                                    "Example usages:",
                                    "- `^ <title> ^ <text> ^`",
                                    "- `^ <title> ^ <text> ^ <color> ^`",
                                    "- `^ <title> ^ <text> ^ <thumbnail> ^`",
                                    "- `^ <title> ^ <text> ^ <color> ^ <thumbnail> ^`",
                                    "",
                                    "Example: ^ Hello ^ This is a test ^ #ff0000 ^ https://www.google.com/someimage.png"

                            )
                            .error().sendTemporary(event.getChannel().asTextChannel(), 10);
                    return;
                }

                if (arguments.length == 4) {
                    new SimpleEmbedBuilder(arguments[0])
                            .footer("Posted by " + event.getAuthor().getName())
                            .text(arguments[1])
                            .color(Color.decode(arguments[2].trim()))
                            .thumbnail(arguments[3])
                            .queue(event.getChannel().asTextChannel());
                }

                if (arguments.length == 3) {
                    if (arguments[2].trim().startsWith("#")) {
                        new SimpleEmbedBuilder(arguments[0])
                                .footer("Posted by " + event.getAuthor().getName())
                                .text(arguments[1])
                                .color(Color.decode(arguments[2].trim()))
                                .queue(event.getChannel().asTextChannel());
                    } else {
                        new SimpleEmbedBuilder(arguments[0])
                                .footer("Posted by " + event.getAuthor().getName())
                                .text(arguments[1])
                                .thumbnail(arguments[2])
                                .queue(event.getChannel().asTextChannel());
                    }

                } else {
                    new SimpleEmbedBuilder(arguments[0])
                            .footer("Posted by " + event.getAuthor().getName())
                            .text(arguments[1])
                            .queue(event.getChannel().asTextChannel());
                }
            }
        }
    }

    /**
     * Send a message as the bot
     * @param event MessageReceivedEvent
     */
    private void messageAsBot(@NotNull MessageReceivedEvent event) {
        // Get the message
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("^^ ") && message.endsWith(" ^^")) {

        // Check if the member has the staff role
            if (event.getMember().getRoles().contains(event.getGuild().getRoleById(Settings.Roles.staff))) {
                event.getMessage().delete().queue();

                String text = message.substring(3, message.length() - 3);
                event.getChannel().sendMessage(text).queue();
                return;
            }
        }
    }

    /**
     * Reads the text from images.
     * @param event MessageReceivedEvent
     */
    private void fileReceived(@NotNull MessageReceivedEvent event) {
        // Check if the message contains a file and adds it to a pastebin.
        event.getMessage().getAttachments().forEach(attachment -> {

            if (attachment.isImage()) {
                /*// Check for unused channels
                switch (event.getChannel().getId()) {
                    case "430176899445161984", "859589285480235019", "608486215439613962", "764594873860227143", "828097631593955348", "907349490556616745", "837679014268895292", "608450789110710275", "738835343788474368", "346344529651040268", "1089093673771356170", "896839416176345110", "998167763791138816", "1086074047185571972", "522049992131608576", "1089865392371007520" -> {
                        return;
                    }
                }

                event.getMessage().addReaction(Emoji.fromFormatted("👀")).queue();

                if (attachment.getFileExtension() == null) return;
                switch (attachment.getFileExtension()) {
                    case "png", "jpg", "jpeg" -> {

                        String content;

                        try {
                            InputStream imageStream = attachment.getProxy().download().join();

                            File file = ProjectUtil.GrayScale(imageStream);

                            imageStream.close();

                            content = ProjectUtil.getTextFromImage(file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        event.getMessage().reply("```" + content + "```").queue();
                    }
                }*/
            } else if (attachment.isVideo() && Settings.Modules.MessageReceive.image) {
                // Check for unused channels
                switch (event.getChannel().getId()) {
                    case "430176899445161984", "859589285480235019", "608486215439613962", "764594873860227143", "828097631593955348", "907349490556616745", "837679014268895292", "608450789110710275", "738835343788474368", "346344529651040268", "1089093673771356170", "896839416176345110", "998167763791138816", "1086074047185571972", "522049992131608576L" -> {
                        return;
                    }
                }

                event.getMessage().addReaction(Emoji.fromFormatted("👀")).queue();
            } else if (Settings.Modules.MessageReceive.pastebin) {

                if (attachment.getFileExtension() == null) return;
                switch (attachment.getFileExtension()) {
                    case "zip", "rar", "7z", "tar", "gz", "jar", "mp3", "mp4", "ogg" -> {
                        return;
                    }
                }

                CompletableFuture<InputStream> future = attachment.getProxy().download();
                InputStream stream = null;
                try {
                    stream = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Common.throwError(e, "Failed to download attachment from message");
                }

                try {
                    String message = new String(stream.readAllBytes());

                    event.getMessage().replyEmbeds(new SimpleEmbedBuilder("Pastebin")
                            .text(
                                    "We would like to remind you that we have a pastebin for any message that is to big for Discord.",
                                    "",
                                    "We have been so kind to upload your message for you, you can find it here: **" + Pastebin.post(message, false) + "." + attachment.getFileExtension() + "**"
                            )
                            .build()
                    ).queue();

                    stream.close();

                } catch (IOException e) {
                    event.getMessage().replyEmbeds(new SimpleEmbedBuilder("Pastebin")
                            .text(
                                    "Hi there,",
                                    "We would like to remind you that we have a pastebin for any message that is to big for Discord.",
                                    "You can find it here: https://paste.techscode.com/",
                                    "Please upload your message/file there and send the link in this channel."
                            )
                            .build()
                    ).queue();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Vacation embed when Greazi is on vacation
     * @param event MessageReceivedEvent
     */
    private void vacationGreazi(MessageReceivedEvent event) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Define the start and end dates for the range
        LocalDate startDate = LocalDate.of(currentDate.getYear(), 8, 1);
        LocalDate endDate = LocalDate.of(currentDate.getYear(), 8, 27);

        // Check if the current date is within the range
        if (currentDate.isAfter(startDate) && currentDate.isBefore(endDate) || currentDate.isEqual(startDate) || currentDate.isEqual(endDate)) {
            List<Member> mentionedMembers = event.getMessage().getMentions().getMembers();
            for (Member mentionMember : mentionedMembers) {
                if (mentionMember.getUser().isBot()) continue;
                if (mentionMember.getUser().getId().equals(event.getAuthor().getId())) continue;



                if (mentionMember.getUser().getId().equals("619084935655063552")) {
                    event.getChannel().sendMessageEmbeds(
                            new SimpleEmbedBuilder("Keep in mind!")
                                    .text(
                                            "Hi 👋🏻, I am currently on vacation until <t:1693548000:D>.",
                                            "",
                                            "If there is something urgent, please contact a staff member, they will be able to help you out.",
                                            "When something is important for me (Greazi) to see than you can send me a DM. Once I am back I will read all my DM's.",
                                            "",
                                            "Thank you for your understanding!"
                                    )
                                    .color(new Color(0, 255, 206))
                                    .thumbnail("https://www.greazi.com/wp-content/uploads/2022/09/Greazi-758x1024-1.png")
                                    .build()
                    ).queue();
                }
            }
        }
    }

    private void bugReport(@NotNull MessageReceivedEvent event) {

        if (event.getChannelType().isThread()) {
            ThreadChannel threadChannel = event.getChannel().asThreadChannel();
            if (threadChannel.getParentChannel().getName().equals("bug-reports") && threadChannel.getMessageCount() < 2) {
                String message = event.getMessage().getContentRaw();

                if (!message.contains("Server Info") || !message.contains("Plugin Info") || !message.contains("Describe the bug") || !message.contains("To Reproduce") || !message.contains("Plugin List")) {
                    event.getChannel().asThreadChannel().sendMessage(
                            "Hello 👋, and thank you for creating this bug report " + event.getAuthor().getAsMention() + "! We're glad to see you here, but we'll definitely need a little more information from you before we can get your bug report solved.\n" +
                                    "\n" +
                                    "We need as much information as possible regarding your server and problem, therefore we've created a template for you to fill out. We do not accept files, therefore please submit your error(s) and logs in a pastebin.\n" +
                                    "\n" +
                                    "**Template:**\n" +
                                    "```" +
                                    "**Server Info (please complete the following information):**\n" +
                                    " - Server Type: [e.g. Paper]\n" +
                                    " - Server Version: [e.g. 1.16.5]\n" +
                                    " - Java Version [e.g. 17.0.1]\n" +
                                    " - Online-Mode: [e.g. True/False]\n" +
                                    " - BungeeCord: [e.g. True/False]\n" +
                                    " - Server Logs: [e.g. https://paste.techscode.com]\n" +
                                    "\n" +
                                    "**Plugin Info (please complete the following information):**\n" +
                                    " - Version: [e.g. 1.0.0]\n" +
                                    " - MySQL: [e.g. Yes/No]\n" +
                                    "\n" +
                                    "**Describe the bug**\n" +
                                    "A clear and concise description of what the bug is.\n" +
                                    "\n" +
                                    "**To Reproduce**\n" +
                                    "Steps to reproduce the behavior:\n" +
                                    "1. Go to '...'\n" +
                                    "2. Click on '....'\n" +
                                    "3. Scroll down to '....'\n" +
                                    "4. See error\n" +
                                    "\n" +
                                    "**Expected behavior**\n" +
                                    "A clear and concise description of what you expected to happen.\n" +
                                    "\n" +
                                    "**Screenshots/Videos**\n" +
                                    "If applicable, add screenshots or a video link to help explain your problem.\n" +
                                    "\n" +
                                    "**Plugin List (please provide us with the full list of your plugins)**\n" +
                                    "\n" +
                                    "**Additional context**\n" +
                                    "Add any other context about the problem here.```\n" +
                                    "\n" +
                                    "**Pastebin:**\n" +
                                    "https://paste.techscode.com/"
                    ).queue();
                }
            }
        }
    }

    private void suggestion(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType().isThread()) {
            ThreadChannel threadChannel = event.getChannel().asThreadChannel();
            if (threadChannel.getParentChannel().getName().equals("suggestions") || threadChannel.getParentChannel().getName().equals("priority-suggestions")) {
                if (threadChannel.getMessageCount() < 2) {
                    String message = event.getMessage().getContentRaw();

                    if (!message.contains("Suggestion Description") || !message.contains("Reason for your suggestion") || !message.contains("help others") || !message.contains("Additional Context")) {
                        event.getChannel().asThreadChannel().sendMessage(
                                "Hello 👋, and thank you for sharing your suggestion " + event.getAuthor().getAsMention() + "! We appreciate your input and would like to gather more details about your suggestion to better understand and evaluate it.\n" +
                                        "\n" +
                                        "To ensure we can consider your suggestion effectively, please follow the template provided below:\n" +
                                        "\n" +
                                        "**Template:**\n" +
                                        "```" +
                                        "**Suggestion Description:**\n" +
                                        "A clear and concise description of your suggestion.\n" +
                                        "\n" +
                                        "**Reason for your suggestion?:**\n" +
                                        "Explain why you believe this suggestion would be beneficial or address a specific issue.\n" +
                                        "\n" +
                                        "**How can this help others?:**\n" +
                                        "Explain why you believe this suggestion would be beneficial or address a specific issue.\n" +
                                        "\n" +
                                        "**Implementation Details (if any):**\n" +
                                        "If you have any technical or implementation details in mind, please share them here.\n" +
                                        "\n" +
                                        "**Additional Context:**\n" +
                                        "Any extra information or context that can help us understand your suggestion better.\n" +
                                        "```\n" +
                                        "\n" +
                                        "Your input is valuable to us, and we'll review your suggestion carefully. Thank you for taking the time to contribute to our project!"
                        ).queue();
                    }
                }
            }
        }
    }

}
