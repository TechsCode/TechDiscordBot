package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.spigotmc.api.APIStatus;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class APICommand extends CommandModule {

    public APICommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!api";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"!status"};
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return null;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.INFO;
    }

    @Override
    public int getCooldown() {
        return 2;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        APIStatus status = bot.getStatus();

        StringBuilder sb = new StringBuilder();
        sb.append(status.getEmoji()).append(" **API Status** (").append(status.getStatus()).append(")\n").append(status.getDescription());
        sb.append("\n\n");

        if(status.isUsable() && member.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"))) { //Make sure the member is staff.
            int purchases = TechDiscordBot.getSpigotAPI().getPurchases().size();
            int reviews = TechDiscordBot.getSpigotAPI().getReviews().size();
            int updates = TechDiscordBot.getSpigotAPI().getUpdates().size();
            int resources = TechDiscordBot.getSpigotAPI().getResources().size();

            sb.append("**Purchases:** ").append(purchases).append("\n");
            sb.append("**Reviews:** ").append(reviews).append("\n");
            sb.append("**Updates:** ").append(updates).append("\n");
            sb.append("**Resources:** ").append(resources).append("\n\n");
        }

        String lastUpdatedFormatted;
        if(status.isUsable()) {
            DateFormat dateTimeInstance = new SimpleDateFormat("EEE MMM dd, hh:mm:ss a z");
            lastUpdatedFormatted = dateTimeInstance.format(new Date(TechDiscordBot.getSpigotAPI().getRefreshTime()));
        } else {
            lastUpdatedFormatted = "Never";
        }

        sb.append("**Last Fetched**: ").append(lastUpdatedFormatted);

        new TechEmbedBuilder("API Status")
                .setText(sb.toString())
                .send(channel);
    }
}
