package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.Preorder;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreorderCommand extends CommandModule {

    public PreorderCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!preorder";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"!preorders"};
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
        boolean preOrdersExist = getRoles().size() > 0;

        if(!preOrdersExist) {
            new TechEmbedBuilder("Preorder Cmd - Error")
                    .error()
                    .setText("Looks like there are currently no pre orders!")
                    .sendTemporary(channel, 10);
            return;
        }

        Member selectedMember = TechDiscordBot.getMemberFromString(message, args.length > 0 ? args[0] : "");
        if(selectedMember == null) selectedMember = member;

        Member finalSelectedMember = selectedMember;
        Preorder preorder = TechDiscordBot.getStorage().getPreorders(getRoles().get(0).replace(" Preorder", ""), false).stream().filter(po -> po.getDiscordId() == finalSelectedMember.getUser().getIdLong()).findFirst().orElse(null);

        if(preorder == null) {
            new TechEmbedBuilder("Preorder Cmd - Error")
                    .error()
                    .setText("Could not find a preorder that belongs to " + selectedMember.getAsMention() + "!")
                    .sendTemporary(channel, 10);
            return;
        }

        boolean showEmail = isArg(args, "showEmail") && (preorder.getDiscordId() == selectedMember.getUser().getIdLong() || isStaff(member));
        boolean showTransactionId = isArg(args, "showTransactionId") && (preorder.getDiscordId() == selectedMember.getUser().getIdLong() || isStaff(member));

        Query<Emote> query = bot.getEmotes(preorder.getPlugin().replace(" ", ""));

        new TechEmbedBuilder("Preorder - " + selectedMember.getEffectiveName() + "#" + selectedMember.getUser().getDiscriminator())
                .success()
                .addField("Email", (showEmail ? preorder.getEmail() : obfuscateEmail(preorder.getEmail())), true)
                .addField("Transaction ID", (showTransactionId ? preorder.getTransactionId() : obfuscateTransactionId(preorder.getTransactionId())), true)
                .addField("Plugin", (query.hasAny() ? query.first().getAsMention() + " " : "") + preorder.getPlugin(), true)
                .addField("Discord Name", preorder.getDiscordName() + " (" + selectedMember.getAsMention() + ")", true)
                .send(channel);
    }

    public String obfuscateEmail(String email) {
        if(email.equals("notAvailable") || email.equals("ManuallyAdded")) return "Unknown";

        int index = email.indexOf("@");
        if(index == -1) return email;

        StringBuilder length = new StringBuilder();
        for(int i = 0; i < index; i++) length.append("\\*");

        StringBuilder sb = new StringBuilder(email);
        sb.replace(0, index, length.toString());
        return sb.toString();
    }

    public String obfuscateTransactionId(String transactionId) {
        if(transactionId.equals("NONE") || transactionId.equals("something")) return "Unknown";
        StringBuilder sb = new StringBuilder(transactionId);

        StringBuilder length = new StringBuilder();
        for(int i = 0; i < (int)(transactionId.length() / 1.5d); i++) length.append("\\*");

        sb.replace(0, (int)(transactionId.length() / 1.5d), length.toString());
        return sb.toString();
    }

    public boolean isArg(String[] args, String arg) {
        return Arrays.stream(args).anyMatch(s -> s.equalsIgnoreCase("--" + arg));
    }

    public List<String> getRoles() {
        return TechDiscordBot.getJDA().getRoles().stream().filter(role -> role.getName().endsWith(" Preorder")).map(Role::getName).collect(Collectors.toList());
    }

    public boolean isStaff(Member member) {
        return member.getRoles().stream().anyMatch(role -> role.getName().equals("Staff"));
    }

    @Override
    public int getCooldown() {
        return 3;
    }
}
