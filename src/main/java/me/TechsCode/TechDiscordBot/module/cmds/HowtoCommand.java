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
                        .addChoice("Hex & Gradient", "Hex & Gradient"),

        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String topic = e.getOption("topic").getAsString();

        if(topic.equalsIgnoreCase("Hex & Gradient")) {
            e.replyEmbeds(
                    new TechEmbedBuilder("How To Use Hex & Gradient")
                            .success()
                            .text("In order to use **Hex** and **Gradients** in Ultra and Insane plugins, you will have to use two different formats.\n" +
                                    "\n" +
                                    "__For Hex Colors__ >\n" +
                                    "```\n" +
                                    "{#RRGGBB}Some Text{#RRGGBB}```\n" +
                                    "__For Gradients Colors__ >\n" +
                                    "```{#RRGGBB>}Some Text{#RRGGBB<}```\n" +
                                    "To be able to create gradient colors in an easier way and having a preview of them you can use this [site](https://rgb.fedee.tk/), you have to select as `Type` **Techscode {#rrggbb>}** then write the text you want to use in `Message`, after modify the colors and finally copy the text where it will be written `Output`.")
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

