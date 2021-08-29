package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

public class HowtoCommand extends CommandModule {

    public HowtoCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "howto";
    }

    @Override
    public String getDescription() {
        return "Howto about popular topics.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "howto", "Select Topic.")
                .addChoice("example", "example"),
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String howto = e.getOption("howto") == null ? null : e.getOption("howto").getAsString();

        if(howto.equalsIgnoreCase("example")) {
            e.replyEmbeds(
                    new TechEmbedBuilder("This is Zombis Title")
                            .success()
                            .text("EXAMPLE TEXT")
                            .field("EXAMPLE FIELD", "EXAMPLE FIELD CONTENT", true)
                            .build()
            ).queue();
        }
    }
}

