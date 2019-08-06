package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReleaseChannels extends Module {

    private final DefinedQuery<TextChannel> CHANNELS = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("releases");
        }
    };

    public ReleaseChannels(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return "Release Channels";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(CHANNELS, 1, "Requires at least one #releases Channel")
        };
    }

    @SubscribeEvent
    public void receive(MessageReceivedEvent e) {
        if(e.getAuthor().isBot()) return;

        if(!CHANNELS.query().all().contains(e.getTextChannel())){
            return;
        }

        if(e.getMessage().getAttachments().size() != 1){
            e.getMessage().delete().queue();
            return;
        }

        Message.Attachment attachment = e.getMessage().getAttachments().get(0);

        if(!attachment.getFileName().endsWith(".jar")){
            new CustomEmbedBuilder("File not accepted").setText("The file type must be a jar file.").sendTemporary(e.getTextChannel(), 5, TimeUnit.SECONDS);
            e.getMessage().delete().queue();
            return;
        }

        File file = new File(attachment.getFileName());
        file.delete();
        attachment.download(file);
        file.deleteOnExit();

        TextChannel feedbackChannel = bot.getChannels("feedback").inCategory(e.getMessage().getCategory()).first();
        String feedbackMention = feedbackChannel != null ? feedbackChannel.getAsMention() : "**#feedback**";

        CustomEmbedBuilder builder = new CustomEmbedBuilder("New Release")
                .setText("**Hello @ everyone!** \n" +
                        "A new File has just been submitted for testing.\n" +
                        "After testing please give us feedback in "+feedbackMention)
                .addField("Changes", e.getMessage().getContentDisplay(), true);

        Message message = builder.send(e.getTextChannel());
        e.getChannel().sendFile(file, message).queue();

        e.getMessage().delete().queue();
    }
}
