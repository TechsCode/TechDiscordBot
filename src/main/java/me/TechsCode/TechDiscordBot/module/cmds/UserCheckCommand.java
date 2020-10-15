package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
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

    public UserCheckCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!check"; }

    @Override
    public String[] getAliases() { return new String[]{"!verified", "!suserinfo", "!sinfo"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return null; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

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

            if (!TechDiscordBot.getBot().getStatus().isUsable()) {
                new TechEmbedBuilder("API Not Usable")
                        .error()
                        .setText("The API does not contain any information! I cannot check a user if it's offline!")
                        .sendTemporary(channel, 10, TimeUnit.SECONDS);
                return;
            }

            boolean canView = m.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"));

            Verification verification;
            if(member != null) {
                verification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member.getUser().getId());
            } else {
                verification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(args[0]);
            }

            if(verification.getDiscordId().equals(m.getId()) && !canView)
                canView = true;

            member = bot.getMember(verification.getDiscordId());

            PurchasesList purchases = TechDiscordBot.getSpigotAPI().getPurchases().userId(verification.getUserId());

            if(!canView) {
                new TechEmbedBuilder("Not Enough Perms")
                        .setText("You have to either be Staff or be viewing your self to execute this command!")
                        .success().send(channel);
                return;
            }

            if (member == null || purchases == null || purchases.size() == 0) {
                new TechEmbedBuilder((member == null ? args[0] : member.getEffectiveName()) + "'s Purchases")
                        .success()
                        .setText((member == null ? args[0] : member.getAsMention()) + " has not bought of any Tech's Resources!")
                        .send(channel);
                return;
            }

            Purchase purchase = purchases.stream().sorted(Comparator.comparingLong(p -> p.getTime().getUnixTime())).skip(purchases.size() - 1).findFirst().orElse(null);
            if (purchase == null) return;

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
}