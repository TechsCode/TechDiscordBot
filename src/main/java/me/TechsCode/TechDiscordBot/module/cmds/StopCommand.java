package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

public class StopCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public StopCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stop the bot";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "confirm", "Confirm to stop the bot.", false)
        };
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, InteractionHook hook, SlashCommandEvent e) {
        OptionMapping confirmOption = e.getOption("confirm");
        String confirm = confirmOption == null ? "" : confirmOption.getAsString();

        if(confirm.equals("cOnFirM")) {
            e.replyEmbeds(new TechEmbedBuilder("Stop")
                    .setText("The bot will now stop!")
                    .build()
            ).queue();

            TechDiscordBot.getJDA().shutdownNow();
            System.exit(0);
        } else {
            e.replyEmbeds(new TechEmbedBuilder("Stop")
                    .setText("Hello, " + m.getAsMention() + "! I've detected that you're trying to stop me!\n\nI do not like that, especially that if I do stop, I will not be restarted!\nIf you **REALLY** wish to stop me, type the following command:\n`!stop cOnFirM`")
                    .build()
            ).queue();
        }
    }
}
