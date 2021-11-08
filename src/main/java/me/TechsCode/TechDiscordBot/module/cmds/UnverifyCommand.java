//package me.TechsCode.TechDiscordBot.module.cmds;
//
//import me.TechsCode.TechDiscordBot.TechDiscordBot;
//import me.TechsCode.TechDiscordBot.module.CommandModule;
//import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
//import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
//import me.TechsCode.TechDiscordBot.objects.Query;
//import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Role;
//import net.dv8tion.jda.api.entities.TextChannel;
//import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
//import net.dv8tion.jda.api.interactions.commands.OptionType;
//import net.dv8tion.jda.api.interactions.commands.build.OptionData;
//import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
//
//public class UnverifyCommand extends CommandModule {
//
//    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
//        @Override
//        protected Query<Role> newQuery() {
//            return bot.getRoles("Staff");
//        }
//    };
//
//    public UnverifyCommand(TechDiscordBot bot) {
//        super(bot);
//    }
//
//    @Override
//    public String getName() {
//        return "unverify";
//    }
//
//    @Override
//    public String getDescription() {
//        return "Unverify a member.";
//    }
//
//    @Override
//    public CommandPrivilege[] getCommandPrivileges() {
//        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
//    }
//
//    @Override
//    public OptionData[] getOptions() {
//        return new OptionData[] {
//                new OptionData(OptionType.STRING, "type", "The type of value.", true)
//                    .addChoice("Discord ID", "discord")
//                    .addChoice("Spigot ID", "spigot"),
//                new OptionData(OptionType.STRING, "data", "The Discord ID or Spigot ID.", true),
//        };
//    }
//
//    @Override
//    public int getCooldown() {
//        return 4;
//    }
//
//    @Override
//    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
//        String type = e.getOption("type").getAsString();
//        String data = e.getOption("data").getAsString();
//
//        if(type.equals("discord")) {
//            process(e, data, channel);
//        } else if(type.equals("spigot")) {
//            processSpigotId(e, data, channel);
//        }
//    }
//
//    public void processSpigotId(SlashCommandEvent e, String spigotId, TextChannel channel) {
//        Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(spigotId);
//
//        if(verification == null) {
//            e.replyEmbeds(
//                new TechEmbedBuilder("Unverify Command - Error")
//                    .error()
//                    .text("The Spigot ID '" + spigotId + "' is not verified!")
//                    .build()
//            ).setEphemeral(true).queue();
//        } else {
//            boolean isUserOnline = TechDiscordBot.getGuild().getMemberById(verification.getDiscordId()) != null;
//
//            e.replyEmbeds(
//                new TechEmbedBuilder("Unverify Command - Success")
//                    .success()
//                    .text("Successfully removed " + (isUserOnline ? TechDiscordBot.getGuild().getMemberById(verification.getDiscordId()).getAsMention() : verification.getDiscordId()) + "'s verification!")
//                    .build()
//            ).queue();
//            verification.delete();
//        }
//    }
//
//    public void process(SlashCommandEvent e, String member, TextChannel channel) {
//        Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member);
//
//        if(verification == null) {
//            e.replyEmbeds(
//                new TechEmbedBuilder("Unverify Command - Error")
//                    .error()
//                    .text(member + " is not verified!")
//                    .build()
//            ).setEphemeral(true).queue();
//        } else {
//            verification.delete();
//
//            e.replyEmbeds(
//                new TechEmbedBuilder("Unverify Command - Success")
//                    .success()
//                    .text("Successfully removed " + member + "'s verification!")
//                    .build()
//            ).queue();
//        }
//    }
//}
