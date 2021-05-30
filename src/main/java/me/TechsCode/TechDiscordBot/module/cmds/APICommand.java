package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.spigotmc.api.APIStatus;
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
        APIStatus status = bot.getStatus();

        StringBuilder sb = new StringBuilder();
        sb.append(status.getEmoji()).append(" **API Status** (").append(status.getStatus()).append(")\n").append(status.getDescription());
        sb.append("\n\n");

        if(status.isUsable() && m.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"))) { //Make sure the member is staff.
            int purchases = TechDiscordBot.getSpigotAPI().getPurchases().size();
            int reviews = TechDiscordBot.getSpigotAPI().getReviews().size();
            int updates = TechDiscordBot.getSpigotAPI().getUpdates().size();
            int resources = TechDiscordBot.getSpigotAPI().getResources().size();

            sb.append("**Purchases:** ").append(purchases).append("\n");
            sb.append("**Reviews:** ").append(reviews).append("\n");
            sb.append("**Updates:** ").append(updates).append("\n");
            sb.append("**Resources:** ").append(resources).append("\n\n");
        }

        String lastUpdatedFormatted = "Never";
        String botLastParsed = "Never";

        if(status.isUsable()) {
            DateFormat dateTimeInstanceRT = new SimpleDateFormat("EEE MMM dd, hh:mm:ss a z");
            lastUpdatedFormatted = dateTimeInstanceRT.format(new Date(TechDiscordBot.getSpigotAPI().getRefreshTime()));

            DateFormat dateTimeInstanceLP = new SimpleDateFormat("EEE MMM dd, hh:mm:ss a z");
            botLastParsed = dateTimeInstanceLP.format(new Date(TechDiscordBot.getSpigotAPI().getLastParsed()));
        }

        sb.append("**Last Fetched**: ").append(lastUpdatedFormatted);
        sb.append("\n**Bot Last Parsed**: ").append(botLastParsed);

        e.replyEmbeds(
            new TechEmbedBuilder("API Status")
                .setText(sb.toString())
                .build()
        ).queue();
    }
}
