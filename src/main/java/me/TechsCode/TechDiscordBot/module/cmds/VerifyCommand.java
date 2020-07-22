package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class VerifyCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public VerifyCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!verify"; }

    @Override
    public String[] getAliases() { return new String[]{"!link", "!update"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.ADMIN; }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length > 1) {
            Member mem;
            try {
                mem = TechDiscordBot.getMemberFromString(message, args[1]);
            } catch (NumberFormatException ex) {
                new TechEmbedBuilder("Verify Cmd - Error").error().setText("Please provide a valid discord id!").sendTemporary(channel, 10);
                return;
            }
            if(mem == null) {
                new TechEmbedBuilder("Verify Cmd - Error").error().setText(args[1] + " is not a valid member id!").sendTemporary(channel, 10);
                return;
            }
            if(TechDiscordBot.getStorage().retrieveVerificationWithDiscord(args[1]) != null || TechDiscordBot.getStorage().retrieveVerificationWithSpigot(args[0]) != null) {
                new TechEmbedBuilder("Verify Cmd - Error").error().setText(mem.getAsMention() + " (" + args[1] + ") is already verified!").sendTemporary(channel, 10);
                return;
            }
            if(TechDiscordBot.getSpigotAPI().getPurchases().userId(args[0]).size() == 0) {
                new TechEmbedBuilder("Verify Cmd - Error").error().setText(mem.getAsMention() + " (" + args[1] + ") does not own any of Tech's Plugins!").sendTemporary(channel, 10);
                return;
            }
            TechDiscordBot.getStorage().createVerification(args[0], args[1]);
            new TechEmbedBuilder("Verify Cmd - Success").success().setText("Successfully verified " + mem.getAsMention() + "! (" + args[1] + ")").send(channel);
        } else {
            new TechEmbedBuilder("Verify Cmd - Error").error().setText("!update <spigotId> <discordId>").sendTemporary(channel, 10);
        }
    }
}