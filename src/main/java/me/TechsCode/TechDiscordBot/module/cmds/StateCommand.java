package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class StateCommand extends CommandModule {
    public static Boolean SpigotBanned = false;
    private final DefinedQuery<net.dv8tion.jda.api.entities.Role> STAFF_ROLE = new DefinedQuery<net.dv8tion.jda.api.entities.Role>() {
        @Override
        protected Query<net.dv8tion.jda.api.entities.Role> newQuery() { return bot.getRoles("Staff"); }
    };

    public StateCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!state";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public DefinedQuery<net.dv8tion.jda.api.entities.Role> getRestrictedRoles() {
        return STAFF_ROLE;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADMIN;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        System.out.println(args.length);
        for (String a: args) {
            System.out.println(a);
        }
        if(args.length > 1) {
            if(args[1].contains("Spigotban")){
                if(SpigotBanned){
                    SpigotBanned = false;
                }else {
                    SpigotBanned = true;
                }
                new TechEmbedBuilder("Spigot Banned!")
                        .setText("Spigot banned is now set to " + SpigotBanned)
                        .success()
                        .sendTemporary(channel, 5);

            }


        }
    }

    @Override
    public int getCooldown() {
        return 0;
    }
}
