package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Comparator;

public class MemberCheckCommand extends CommandModule {

    public MemberCheckCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getDescription() {
        return "Check a member's Purchases & Spigot Info.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.MENTIONABLE, "member", "Member to check."),
                new OptionData(OptionType.STRING, "spigotId", "Member's spigot id.")
        };
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, InteractionHook hook, SlashCommandEvent e) {
        Member member = e.getOption("member") == null ? null : e.getOption("member").getAsMember();
        String spigotId = e.getOption("spigotId") == null ? null : e.getOption("spigotId").getAsString();

        if(member == null && spigotId == null)
            member = m;

        if (!TechDiscordBot.getBot().getStatus().isUsable()) {
            e.replyEmbeds(
                new TechEmbedBuilder("API Not Usable")
                    .error()
                    .setText("The API does not contain any information! I cannot check a user if it's offline!")
                    .build()
            ).queue();
            return;
        }

        boolean canView = m.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"));

        Verification verification = spigotId == null ? TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member.getUser().getId()) : TechDiscordBot.getStorage().retrieveVerificationWithSpigot(spigotId);

        if(verification == null) {
            new TechEmbedBuilder((spigotId != null ? spigotId : member.getEffectiveName()) + " Is Not Verified!")
                    .setText((spigotId != null ? spigotId : member.getAsMention()) + " has not verified themselves!")
                    .error().send(channel);
            return;
        }

        if(verification.getDiscordId().equals(m.getId()) && !canView)
            canView = true;

        if(member == null)
            member = bot.getMember(verification.getDiscordId());

        PurchasesList purchases = TechDiscordBot.getSpigotAPI().getPurchases().userId(verification.getUserId());

        if(!canView) {
            new TechEmbedBuilder("Not Enough Perms")
                    .setText("You have to either be Staff or be viewing your self to execute this command!")
                    .error().send(channel);
            return;
        }

        if (member == null || purchases == null || purchases.size() == 0) {
            new TechEmbedBuilder((spigotId != null ? spigotId : member.getEffectiveName()) + "'s Purchases")
                    .error()
                    .setText((member != null ? spigotId : member.getAsMention()) + " has not bought of any Tech's Resources!")
                    .send(channel);
            return;
        }

        Purchase purchase = purchases.stream().sorted(Comparator.comparingLong(p -> p.getTime().getUnixTime())).skip(purchases.size() - 1).findFirst().orElse(null);
        if (purchase == null)
            return;

        String date = purchase.getTime().getHumanTime();
        boolean hasBoughtAll = TechDiscordBot.getSpigotAPI().getResources().premium().size() == purchases.size();
        StringBuilder sb = new StringBuilder();

        for (Purchase p : purchases)
            sb.append("- ").append(Plugin.fromId(p.getResource().getId()).getEmoji().getAsMention()).append(" ").append(p.getResource().getName()).append(" ").append(!p.getCost().isPresent() ? "as a Gift/Free" : "for " + p.getCost().get().getValue() + p.getCost().get().getCurrency()).append(" on").append((p.getTime().getHumanTime() != null ? " " + p.getTime().getHumanTime() : " Unknown (*too early to calculate*)")).append(",\n ");

        String purchasesString = sb.toString();
        new TechEmbedBuilder(member.getEffectiveName())
                .success()
                .setThumbnail(purchase.getUser().getAvatar())
                .setText("Showing " + member.getAsMention() + "'s Spigot Information.")
                .addField("Username / ID", "[" + purchase.getUser().getUsername() + "." + purchase.getUser().getUserId() + "](https://www.spigotmc.org/members/" + purchase.getUser().getUsername().toLowerCase() + "." + purchase.getUser().getUserId() + ")", true)
                .addField("Purchases Amount", hasBoughtAll ? " **All** " + purchases.size() + " plugins purchased!" : purchases.size() + "**/**" + TechDiscordBot.getSpigotAPI().getResources().premium().size() + " purchased.", true)
                .addField("Last Purchase", Plugin.fromId(purchase.getResource().getId()).getEmoji().getAsMention() + " " + (date != null ? date + ".": "Unknown\n*or cannot calculate*."), true)
                .addField("Purchases", purchasesString.substring(0, purchasesString.length() - 3) + ".", false)
                .send(channel);
    }
}