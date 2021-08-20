package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.spigotmc.data.APIStatus;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class APICommand extends CommandModule {

    public APICommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "api";
    }

    @Override
    public String getDescription() {
        return "Fetches the API's status.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    @Override
    public int getCooldown() {
        return 2;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        APIStatus spigotStatus = bot.getSpigotStatus();
        APIStatus marketStatus = bot.getSpigotStatus();
        APIStatus songodaStatus = bot.getSongodaStatus();

        StringBuilder sb = new StringBuilder();
        appendStatus("Spigot", spigotStatus, sb, m);
        appendStatus("Market", spigotStatus, sb, m);
        appendStatus("Songoda", songodaStatus, sb, m);

        e.replyEmbeds(
            new TechEmbedBuilder("API Status")
                .text(sb.toString())
                .build()
        ).queue();
    }

    private void appendStatus(String name, APIStatus status, StringBuilder sb, Member m) {
        if(!sb.toString().isEmpty())
            sb.append("\n\n");

        sb.append(status.getEmoji()).append(" **API Status - ").append(name).append("** (").append(status.getStatus()).append(")\n").append(status.getDescription());
        sb.append("\n\n");

        if(status.isUsable() && m.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"))) { //Make sure the member is staff.
            if(name.equals("Spigot")) {
                int purchases = TechDiscordBot.getSpigotAPI().getSpigotPurchases().size();
                int reviews = TechDiscordBot.getSpigotAPI().getSpigotReviews().size();
                int updates = TechDiscordBot.getSpigotAPI().getSpigotUpdates().size();
                int resources = TechDiscordBot.getSpigotAPI().getSpigotResources().size();

                sb.append("**Purchases:** ").append(purchases).append("\n");
                sb.append("**Reviews:** ").append(reviews).append("\n");
                sb.append("**Updates:** ").append(updates).append("\n");
                sb.append("**Resources:** ").append(resources).append("\n\n");
            }else if(name.equals("Market")) {
                int purchases = TechDiscordBot.getSpigotAPI().getMarketPurchases().size();
                int reviews = TechDiscordBot.getSpigotAPI().getMarketReviews().size();
                int updates = TechDiscordBot.getSpigotAPI().getMarketUpdates().size();
                int resources = TechDiscordBot.getSpigotAPI().getMarketResources().size();

                sb.append("**Purchases:** ").append(purchases).append("\n");
                sb.append("**Reviews:** ").append(reviews).append("\n");
                sb.append("**Updates:** ").append(updates).append("\n");
                sb.append("**Resources:** ").append(resources).append("\n\n");
            } else {
                sb.append("**Purchases:** ").append(TechDiscordBot.getSongodaAPI().getSpigotPurchases().size()).append("\n\n");
            }
        }

        String lastUpdatedFormatted = "Never";
        String botLastParsed = "Never";
        DateFormat dateTimeInstanceRT = new SimpleDateFormat("MMMM dd, hh:mm:ss a z");

        switch (name) {
            case "Songoda":
                if (status.isUsable()) {
                    lastUpdatedFormatted = dateTimeInstanceRT.format(new Date(TechDiscordBot.getSongodaAPI().getRefreshTime()));
                }

                sb.append("**Last Fetched**: ").append(lastUpdatedFormatted);
                break;
            case "Spigot":
                if (status.isUsable()) {
                    lastUpdatedFormatted = dateTimeInstanceRT.format(new Date(TechDiscordBot.getSpigotAPI().getStatus().getLastSpigotFetch()));
                    botLastParsed = dateTimeInstanceRT.format(new Date(TechDiscordBot.getSpigotAPI().getLastBotFetch()));
                }

                sb.append("**Last Fetched**: ").append(lastUpdatedFormatted);
                sb.append("\n**Last Parsed**: ").append(botLastParsed);
                break;
            case "Market":
                if (status.isUsable()) {
                    lastUpdatedFormatted = dateTimeInstanceRT.format(new Date(TechDiscordBot.getSpigotAPI().getStatus().getLastMarketFetch()));
                    botLastParsed = dateTimeInstanceRT.format(new Date(TechDiscordBot.getSpigotAPI().getLastBotFetch()));
                }

                sb.append("**Last Fetched**: ").append(lastUpdatedFormatted);
                sb.append("\n**Last Parsed**: ").append(botLastParsed);
                break;
        }
    }
}
