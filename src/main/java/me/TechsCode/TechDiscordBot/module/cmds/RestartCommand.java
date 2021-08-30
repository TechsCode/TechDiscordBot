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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.awt.*;

public class RestartCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    private final DefinedQuery<Role> ADMIN_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Senior Supporter", "Assistant", "Developer", "\uD83D\uDCBB Coding Wizard");
        }
    };

    public RestartCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public String getDescription() {
        return "Restart the Bot or the API.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "service", "The Service to restart (API or Bot)", true)
                        .addChoice("Bot", "Bot")
                        .addChoice("API", "API")
        };
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, SlashCommandEvent e) {
        String service = e.getOption("service").getAsString();

        if(service.equalsIgnoreCase("Bot")) {
            e.replyEmbeds(new TechEmbedBuilder("Restart Status Loading...")
                    .text("Restarting Bot.....")
                    .color(Color.ORANGE)
                    .build()
            ).queue(q ->{
                TechDiscordBot.getPterodactylAPI().restartServer("dd4ae440");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                TechDiscordBot.getPterodactylAPI().killServer("dd4ae440");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                q.editOriginalEmbeds(new TechEmbedBuilder("Restarted!")
                        .text("The bot has been restarted!")
                        .success()
                        .build()).queue();
            });
        }
        if(service.equalsIgnoreCase("API")) {
            if(member.getRoles().contains(ADMIN_ROLE.query().first())) {
                e.replyEmbeds(new TechEmbedBuilder("API Restart Status Loading...")
                        .text("Restarting API.....")
                        .color(Color.ORANGE)
                        .build()
                ).queue(q -> {
                    if (TechDiscordBot.getSpigotAPI().restartAPI()) {
                        q.editOriginalEmbeds(new TechEmbedBuilder("API Restarted!")
                                .text("The API has been restarted!")
                                .success()
                                .build()).queue();
                    } else {
                        q.editOriginalEmbeds(new TechEmbedBuilder("API Restart Failed!")
                                .text("The API has failed to restart!")
                                .error()
                                .build()).queue();
                    }
                });
            }else{
                e.replyEmbeds(new TechEmbedBuilder("Restart Failed")
                        .text("You have no permission to perform this command")
                        .error()
                        .build()).queue();
            }
        }
    }

}