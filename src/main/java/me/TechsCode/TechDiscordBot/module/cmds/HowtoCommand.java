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
                new OptionData(OptionType.STRING, "topic", "Select Topic.", true)
                        .addChoice("MySQL", "MySQL"),

        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String topic = e.getOption("topic").getAsString();

        if(topic.equalsIgnoreCase("MySQL")) {
            e.replyEmbeds(
                    new TechEmbedBuilder("How To Setup MySQL")
                            .success()
                            .text("To connect your server to MySQL, open the GUI of the desired plugin and\n" +
                                    "click on, Settings >> MySQL Database >> Setup MySQL >> Credentials```\n" +
                                    "\n" +
                                    "Here you will need to fill in the credentials of your database.\n" +
                                    "\n" +
                                    "Once you have filled in all the credentials, you can now test the connection and save it.\n" +
                                    "\n" +
                                    "When you have saved your credentials, you need to restart your server for the plugin to connect.\n" +
                                    "\n" +
                                    "When you have repeated this process for all of your servers, your plugin is now proxy-ready.\n" +
                                    "\n" +
                                    "**NOTE: If your database has not connected or the test takes too long, you have entered the wrong credentials.**")
                            //.field("EXAMPLE FIELD", "EXAMPLE FIELD CONTENT", true)
                            .build()
            ).queue();
        }
    }
}
// Example
//       if(topic.equalsIgnoreCase("Test")) {
//           e.replyEmbeds(
//                   new TechEmbedBuilder("EXAMPLE TITLE")
//                        .success()
//                        .text("EXAMPLE TEXT")
//                        .field("EXAMPLE FIELD", "EXAMPLE FIELD CONTENT", true)
//                        .build()
//           ).queue();
//       }

