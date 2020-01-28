package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.command.CommandModule;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.storage.Verification;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import com.techeazy.spigotapi.data.objects.*;
import com.techeazy.spigotapi.data.collections.*;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

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
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 0) {
            new CustomEmbedBuilder("Check")
                    .error()
                    .setText("Please specify a user to check!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
        } else {
            if(message.getMentionedMembers().size() > 0) {
                Member member1 = message.getMentionedMembers().get(0);
                process(member1, channel);
            } else if (bot.getGuild().getMembers().stream().anyMatch(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(args[0]) || mem.getUser().getId().equalsIgnoreCase(args[0]))) {
                Member member1 = bot.getGuild().getMembers().stream().filter(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(args[0]) || mem.getUser().getId().equalsIgnoreCase(args[0])).findFirst().orElse(null);
                process(member1, channel);
            }
        }
    }

    public void process(Member member, TextChannel channel) {
        if(!bot.getSpigotAPI().isAvailable()) {
            new CustomEmbedBuilder("API Offline")
                    .error()
                    .setText("The API is offline! I cannot check a user if it's offline!")
                    .sendTemporary(channel, 10, TimeUnit.SECONDS);
            return;
        }
        Verification verification = bot.getStorage().retrieveVerificationWithDiscord(member.getUser().getId());
        PurchaseCollection purchases = null;
        if(verification != null) purchases = bot.getSpigotAPI().getPurchases().userId(verification.getUserId());
        if(purchases == null || purchases.size() == 0) {
            new CustomEmbedBuilder(member.getEffectiveName() + "'s Purchases")
                    .success()
                    .setText(member.getAsMention() + " has not bought of any Tech's Resources!")
                    .send(channel);
        } else {
            Purchase purchase = purchases.getStream().sorted(Comparator.comparingLong(p  -> p.getTime().getUnixTime())).skip(purchases.size() - 1).findFirst().get();
            String date = purchase.getTime().getHumanTime();
            boolean hasBoughtAll = bot.getSpigotAPI().getResources().premium().size() == purchases.size();
            StringBuilder sb = new StringBuilder();
            for(Purchase p : purchases.get()) sb.append("- ").append(p.getResourceName()).append(" for ").append(p.getCost().getValue() == 0d ? "Free" : p.getCost().getValue() + p.getCost().getCurrency()).append(",\n ");
            String purchasesString = sb.toString();
            new CustomEmbedBuilder(member.getEffectiveName() + "'s Purchases")
                    .success()
                    .setText("Spigot URL: https://www.spigotmc.org/members/" + verification.getUserId() + "\n\n" + member.getAsMention() + " has bought " + (hasBoughtAll ? "**all** " : " ") + purchases.size() + " of Tech's Resources.\n\nTheir last purchase was on " + date + ".\n\n**Their purchases include:**\n" + purchasesString.substring(0, purchasesString.length() - 3) + ".")
                    .send(channel);
        }
    }
}