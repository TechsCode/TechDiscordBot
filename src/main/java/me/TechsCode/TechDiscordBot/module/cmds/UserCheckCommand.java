package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.SpigotAPI.client.collections.PurchaseCollection;
import me.TechsCode.SpigotAPI.client.objects.Purchase;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class UserCheckCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };
    private final DefinedQuery<TextChannel> STAFF_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("staff-chat");
        }
    };

    public UserCheckCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!check"; }

    @Override
    public String[] getAliases() { return new String[]{"!verified"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return STAFF_CHANNEL; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.ADMIN; }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member m, String[] args) {
        if(args.length == 0) {
            new TechEmbedBuilder("Check")
                    .error()
                    .setText("Please specify a user (id) to check!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            Member member = TechDiscordBot.getMemberFromString(message, args[0]);

            if (!TechDiscordBot.getSpigotAPI().isAvailable()) {
                new TechEmbedBuilder("API Offline")
                        .error()
                        .setText("The API is offline! I cannot check a user if it's offline!")
                        .sendTemporary(channel, 10, TimeUnit.SECONDS);
                return;
            }

            Verification verification = null;
            if(member != null) verification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member.getUser().getId());

            PurchaseCollection purchases;
            if (verification != null) {
                purchases = TechDiscordBot.getSpigotAPI().getPurchases().userId(verification.getUserId());
            } else {
                purchases = TechDiscordBot.getSpigotAPI().getPurchases().userId(args[0]);
            }

            if (purchases == null || purchases.size() == 0) {
                new TechEmbedBuilder((member == null ? args[0] : member.getEffectiveName()) + "'s Purchases")
                        .success()
                        .setText((member == null ? args[0] : member.getAsMention()) + " has not bought of any Tech's Resources!")
                        .send(channel);
            } else {
                Purchase purchase = purchases.getStream().sorted(Comparator.comparingLong(p -> p.getTime().getUnixTime())).skip(purchases.size() - 1).findFirst().orElse(null);
                if (purchase == null) return;

                String date = purchase.getTime().getHumanTime();
                boolean hasBoughtAll = TechDiscordBot.getSpigotAPI().getResources().premium().size() == purchases.size();
                StringBuilder sb = new StringBuilder();
                for (Purchase p : purchases.get())
                    sb.append("- ").append(Plugin.fromId(p.getResourceId()).getEmoji().getAsMention()).append(" ").append(p.getResourceName()).append(" for ").append(p.getCost() == null || p.getCost().getValue() == 0d ? "Free" : p.getCost().getValue() + p.getCost().getCurrency()).append(" on ").append((p.getTime().getHumanTime() != null ? "on " + p.getTime().getHumanTime() : "*too early to calculate*")).append(",\n ");
                String purchasesString = sb.toString();
                new TechEmbedBuilder((member == null ? purchases.get()[0].getUsername() + " (" + purchases.get()[0].getUserId() + ")" : member.getEffectiveName()) + "'s Purchases")
                        .success()
                        .setText("Spigot URL: https://www.spigotmc.org/members/" + (verification == null ? args[0] : verification.getUserId()) + "\n\n" + (member == null ? purchases.get()[0].getUsername() + " (" + purchases.get()[0].getUserId() + ")" : member.getAsMention()) + " has bought " + (hasBoughtAll ? "**all** " : " ") + purchases.size() + " of Tech's Resources.\n\n" + (date != null ? "Their last purchase was on " + date : "Too early to calculate their last purchase") + ".\n\n**Their purchases include:**\n" + purchasesString.substring(0, purchasesString.length() - 3) + ".")
                        .send(channel);
            }
        }
    }
}