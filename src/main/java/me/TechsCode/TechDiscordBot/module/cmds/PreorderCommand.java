//package me.TechsCode.TechDiscordBot.module.cmds;
//
//import me.TechsCode.TechDiscordBot.TechDiscordBot;
//import me.TechsCode.TechDiscordBot.module.CommandModule;
//import me.TechsCode.TechDiscordBot.mysql.storage.Preorder;
//import me.TechsCode.TechDiscordBot.objects.Query;
//import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
//import net.dv8tion.jda.api.entities.Emote;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Role;
//import net.dv8tion.jda.api.entities.TextChannel;
//import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
//import net.dv8tion.jda.api.interactions.commands.OptionType;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
//import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//public class PreorderCommand extends CommandModule {
//
//    public PreorderCommand(TechDiscordBot bot) {
//        super(bot);
//    }
//
//    @Override
//    public String getName() {
//        return "preorder";
//    }
//
//    @Override
//    public String getDescription() {
//        return "Show a member's preorder.";
//    }
//
//    @Override
//    public CommandPrivilege[] getCommandPrivileges() {
//        return new CommandPrivilege[0];
//    }
//
//    @Override
//    public OptionData[] getOptions() {
//        return new OptionData[] {
//                new OptionData(OptionType.USER, "member", "View this member's preorder. (Default: You)"),
//                new OptionData(OptionType.BOOLEAN, "show-email", "Show the member's email? (Default: False)"),
//                new OptionData(OptionType.BOOLEAN, "show-transaction-id", "Show the transaction id? (Default: False)")
//        };
//    }
//
//    @Override
//    public int getCooldown() {
//        return 3;
//    }
//
//    @Override
//    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
//        boolean preOrdersExist = getRoles().size() > 0;
//
//        if(!preOrdersExist) {
//            e.replyEmbeds(
//                new TechEmbedBuilder("Preorder Command - Error")
//                    .error()
//                    .text("Looks like there are currently no pre orders!")
//                    .build()
//            ).setEphemeral(true).queue();
//            return;
//        }
//
//        Member member = e.getOption("member") == null ? null : e.getOption("member").getAsMember();
//        if(member == null) member = m;
//
//        Member finalSelectedMember = member;
//        Preorder preorder = TechDiscordBot.getStorage().getPreorders(getRoles().get(0).replace(" Preorder", ""), false).stream().filter(po -> po.getDiscordId() == finalSelectedMember.getUser().getIdLong()).findFirst().orElse(null);
//
//        if(preorder == null) {
//            e.replyEmbeds(
//                new TechEmbedBuilder("Preorder Command - Error")
//                    .error()
//                    .text("Could not find a preorder that belongs to " + member.getAsMention() + "!")
//                    .build()
//            ).setEphemeral(true).queue();
//            return;
//        }
//
//        boolean showEmail = (e.getOption("show-email") != null && e.getOption("show-email").getAsBoolean()) && (preorder.getDiscordId() == member.getUser().getIdLong() || isStaff(member));
//        boolean showTransactionId = (e.getOption("show-transaction-id") != null && e.getOption("show-transaction-id").getAsBoolean()) && (preorder.getDiscordId() == member.getUser().getIdLong() || isStaff(member));
//
//        Query<Emote> query = bot.getEmotes(preorder.getPlugin().replace(" ", ""));
//
//        e.replyEmbeds(
//            new TechEmbedBuilder("Preorder - " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator())
//                .success()
//                .field("Email", (showEmail ? preorder.getEmail() : obfuscateEmail(preorder.getEmail())), true)
//                .field("Transaction ID", (showTransactionId ? preorder.getTransactionId() : obfuscateTransactionId(preorder.getTransactionId())), true)
//                .field("Plugin", (query.hasAny() ? query.first().getAsMention() + " " : "") + preorder.getPlugin(), true)
//                .field("Discord Name", preorder.getDiscordName() + " (" + member.getAsMention() + ")", true)
//                .build()
//        ).queue();
//    }
//
//    public String obfuscateEmail(String email) {
//        if(email.equals("notAvailable") || email.equals("ManuallyAdded"))
//            return "Unknown";
//
//        int index = email.indexOf("@");
//        if(index == -1)
//            return email;
//
//        StringBuilder length = new StringBuilder();
//        for(int i = 0; i < index; i++)
//            length.append("\\*");
//
//        StringBuilder sb = new StringBuilder(email);
//        sb.replace(0, index, length.toString());
//
//        return sb.toString();
//    }
//
//    public String obfuscateTransactionId(String transactionId) {
//        if(transactionId.equals("NONE") || transactionId.equals("something"))
//            return "Unknown";
//
//        StringBuilder sb = new StringBuilder(transactionId);
//
//        String length = IntStream.range(0, (int) (transactionId.length() / 1.5d)).mapToObj(i -> "\\*").collect(Collectors.joining());
//
//        sb.replace(0, (int)(transactionId.length() / 1.5d), length);
//
//        return sb.toString();
//    }
//
//    public List<String> getRoles() {
//        return TechDiscordBot.getJDA().getRoles().stream().map(Role::getName).filter(name -> name.endsWith(" Preorder")).collect(Collectors.toList());
//    }
//
//    public boolean isStaff(Member member) {
//        return member.getRoles().stream().anyMatch(role -> role.getName().equals("Staff"));
//    }
//}
