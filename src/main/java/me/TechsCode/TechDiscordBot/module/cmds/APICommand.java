package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.SpigotAPI.client.APIScanner;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

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
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        String online = channel.getGuild().getEmotesByName("low_priority", true).get(0).getAsMention();
        String waiting = channel.getGuild().getEmotesByName("medium_priority", true).get(0).getAsMention();
        String offline = channel.getGuild().getEmotesByName("high_priority", true).get(0).getAsMention();

        APIScanner.APIStatus status = TechDiscordBot.getSpigotAPI().getStatus();
        APIScanner.APIStatus cacheStatus = TechDiscordBot.getSpigotAPI().getCacheStatus();
        APIScanner.APIStatus songodaStatus = TechDiscordBot.getSongodaAPI().getStatus();
        APIScanner.APIStatus songodaCachedStatus = TechDiscordBot.getSongodaAPI().getCacheStatus();

        String statusEmoji = online;
        if(status == APIScanner.APIStatus.WAITING) statusEmoji = waiting;
        if(status == APIScanner.APIStatus.OFF) statusEmoji = offline;

        String cachedStatusEmoji = online;
        if(cacheStatus == APIScanner.APIStatus.WAITING) cachedStatusEmoji = waiting;
        if(cacheStatus == APIScanner.APIStatus.OFF) cachedStatusEmoji = offline;

        String songodaStatusEmoji = online;
        if(songodaStatus == APIScanner.APIStatus.OFF) songodaStatusEmoji = offline;

        String songodaCachedStatusEmoji = online;
        if(songodaCachedStatus == APIScanner.APIStatus.OFF) songodaCachedStatusEmoji = offline;

        StringBuilder sb = new StringBuilder();
        sb.append(statusEmoji).append(" **API Status** (").append(status.getName()).append(")\n").append(status.getDescription());
        sb.append("\n\n");

        sb.append(cachedStatusEmoji).append(" **Cached API Status** (").append(cacheStatus.getName()).append(")\n").append(cacheStatus.getDescription());
        sb.append("\n\n");

        sb.append(songodaStatusEmoji).append(" **Songoda API Status** (").append(songodaStatus.getName()).append(")\n").append(songodaStatus.getDescription());
        sb.append("\n\n");

        sb.append(songodaCachedStatusEmoji).append(" **Songoda Cached API Status** (").append(songodaCachedStatus.getName()).append(")\n").append(songodaCachedStatus.getDescription());
        sb.append("\n\n");

        if(cacheStatus == APIScanner.APIStatus.OK && songodaStatus == APIScanner.APIStatus.OK && member.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"))) { //Make sure the member is staff.
            int purchases = TechDiscordBot.getSpigotAPI().getPurchases().size();
            int songodaPurchases = TechDiscordBot.getSongodaAPI().getPurchases().size();
            int reviews = TechDiscordBot.getSpigotAPI().getReviews().size();
            int updates = TechDiscordBot.getSpigotAPI().getUpdates().size();
            int resources = TechDiscordBot.getSpigotAPI().getResources().size();

            sb.append("**Spigot/Songoda Purchases:** ").append(purchases).append(" / ").append(songodaPurchases).append("\n");
            sb.append("**Reviews:** ").append(reviews).append("\n");
            sb.append("**Updates:** ").append(updates).append("\n");
            sb.append("**Resources:** ").append(resources).append("\n\n");
            sb.append("*This information doesn't accurately depict if the API is currently running.*\n*This just shows if the bot has the information stored. (Cached)*");
        } else if(cacheStatus == APIScanner.APIStatus.OK && member.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"))) { //Make sure the member is staff.
            int purchases = TechDiscordBot.getSpigotAPI().getPurchases().size();
            int reviews = TechDiscordBot.getSpigotAPI().getReviews().size();
            int updates = TechDiscordBot.getSpigotAPI().getUpdates().size();
            int resources = TechDiscordBot.getSpigotAPI().getResources().size();

            sb.append("**Purchases:** ").append(purchases).append("\n");
            sb.append("**Reviews:** ").append(reviews).append("\n");
            sb.append("**Updates:** ").append(updates).append("\n");
            sb.append("**Resources:** ").append(resources).append("\n\n");
            sb.append("*This information doesn't accurately depict if the API is currently running.*\n*This just shows if the bot has the information stored. (Cached)*");
        } else if(songodaCachedStatus == APIScanner.APIStatus.OK && member.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"))) { //Make sure the member is staff.
            int purchases = TechDiscordBot.getSongodaAPI().getPurchases().size();

            sb.append("**Songoda Purchases:** ").append(purchases).append("\n\n");
            sb.append("*This information doesn't accurately depict if the API is currently running.*\n*This just shows if the bot has the information stored. (Cached)*");
        }

        new TechEmbedBuilder("API Status")
                .setText(sb.toString())
                .send(channel);
    }
}
