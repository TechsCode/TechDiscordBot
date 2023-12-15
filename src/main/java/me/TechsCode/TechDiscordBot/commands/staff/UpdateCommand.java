package me.techscode.techdiscordbot.commands.staff;

import com.greazi.discordbotfoundation.handlers.commands.SimpleSlashCommand;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import me.techscode.techdiscordbot.utils.Changelog;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UpdateCommand extends SimpleSlashCommand {

    public UpdateCommand() {
        super("update");
        description("Send an update embed to an channel");

        OptionData optionData = new OptionData(OptionType.STRING, "version", "The version of the update", true);

        for (Changelog changelog : Changelog.values()) {
            optionData.addChoice(changelog.getVersion(), changelog.name());
        }

        options(
                optionData,
                new OptionData(OptionType.CHANNEL, "channel", "The channel to send the update to", false)

        );

        mainGuildOnly();
    }

    @Override
    protected void onCommand(@NotNull SlashCommandInteractionEvent event) {
        String version = Objects.requireNonNull(event.getOption("version")).getAsString();
        OptionMapping channelOptionMapping = event.getOption("channel");

        Changelog changelog = Changelog.valueOf(version);

        SimpleEmbedBuilder embed = new SimpleEmbedBuilder("Update " + changelog.getVersion())
                .field("Changes:", "- " + String.join("\n- ", changelog.getChanges()), false);

        if (channelOptionMapping == null) {
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        } else {
            channelOptionMapping.getAsChannel().asTextChannel().sendMessageEmbeds(embed.build()).queue();
        }


    }
}
