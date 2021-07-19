package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchase;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchaseList;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Comparator;

public class SongodaCheckCommand extends CommandModule {

    public SongodaCheckCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "songodacheck";
    }

    @Override
    public String getDescription() {
        return "Check a Member's Purchases & Songoda Info.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.USER, "member", "Member to check."),
                new OptionData(OptionType.INTEGER, "songoda-id", "Member's songoda id.")
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        Member member = e.getOption("member") == null ? null : e.getOption("member").getAsMember();
        String songodaId = e.getOption("songoda-id") == null ? null : e.getOption("songoda-id").getAsString();

        if(member == null && songodaId == null)
            member = m;

        if (!TechDiscordBot.getBot().getSongodaStatus().isUsable()) {
            e.replyEmbeds(
                    new TechEmbedBuilder("API Not Usable")
                            .error()
                            .text("I cannot check a user when the Songoda's API is offline!")
                            .build()
            ).queue();
            return;
        }

        boolean canView = m.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"));

        SongodaPurchaseList purchases = songodaId == null ? TechDiscordBot.getSongodaAPI().getPurchases().discord(m) : TechDiscordBot.getSongodaAPI().getPurchases().userId(songodaId);

        if(purchases.size() == 0) {
            e.replyEmbeds(
                    new TechEmbedBuilder((songodaId != null ? songodaId : member.getEffectiveName()) + "'s Purchases")
                            .error()
                            .text((member != null ? songodaId : member.getAsMention()) + " has not bought of any Tech's Resources!")
                            .build()
            ).queue();
            return;
        }

        if(purchases.get(0).getMember().getId().equals(m.getId()) && !canView)
            canView = true;

        if(member == null)
            member = purchases.get(0).getMember();

        if(!canView) {
            e.replyEmbeds(
                    new TechEmbedBuilder("Not Enough Perms")
                            .text("You have to either be Staff or be viewing yourself to execute this command!")
                            .error()
                            .build()
            ).setEphemeral(true).queue();
            return;
        }

        SongodaPurchase purchase = purchases.stream().sorted(Comparator.comparingLong(p -> p.getTime().getUnixTime())).skip(purchases.size() - 1).findFirst().orElse(null);
        if (purchase == null)
            return;

        String date = purchase.getTime().getHumanTime();
        boolean hasBoughtAll = TechDiscordBot.getSpigotAPI().getResources().premium().size() == purchases.size();
        StringBuilder sb = new StringBuilder();

        for (Purchase p : purchases)
            sb.append("- ").append(Plugin.fromId(p.getResource().getId()).getEmoji().getAsMention()).append(" ").append(p.getResource().getName()).append(" ").append(!p.getCost().isPresent() ? "as a Gift/Free" : "for " + p.getCost().get().getValue() + p.getCost().get().getCurrency()).append(" on").append((p.getTime().getHumanTime() != null ? " " + p.getTime().getHumanTime() : " Unknown (*too early to calculate*)")).append("\n ");

        String purchasesString = sb.toString();
        e.replyEmbeds(
                new TechEmbedBuilder(member.getEffectiveName())
                        .success()
                        .thumbnail(purchase.getUser().getAvatar())
                        .text("Showing " + member.getAsMention() + "'s Songoda Information.")
                        .field("Username / ID", "[" + purchase.getUser().getUsername() + "." + purchase.getUser().getUserId() + "](https://songoda.com/profile/" + purchase.getUser().getUsername().toLowerCase() + ")", true)
                        .field("Purchases Amount", hasBoughtAll ? " **All** " + purchases.size() + " plugins purchased!" : purchases.size() + "**/**" + TechDiscordBot.getSpigotAPI().getResources().premium().size() + " purchased.", true)
                        .field("Last Purchase", Plugin.fromId(purchase.getResource().getId()).getEmoji().getAsMention() + " " + (date != null ? date + ".": "Unknown\n*or cannot calculate*."), true)
                        .field("Purchases", purchasesString.substring(0, purchasesString.length() - 3) + ".", false)
                        .build()
        ).queue();
    }
}
