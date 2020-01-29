package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.command.CommandCategory;
import me.TechsCode.TechDiscordBot.command.CommandModule;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class UpdateCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public UpdateCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!update"; }

    @Override
    public String[] getAliases() { return new String[]{"!link", "!verify"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.ADMIN; }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length > 1) {
            Member mem;
            try {
                mem = TechDiscordBot.getBot().getMember(args[1]);
            } catch (NumberFormatException ex) {
                new CustomEmbedBuilder("Error").error().setText("Please provide a valid discord id!").sendTemporary(channel, 10);
                return;
            }
            if(mem == null) {
                new CustomEmbedBuilder("Error").error().setText(args[1] + " is not a valid member id!").sendTemporary(channel, 10);
                return;
            }
            if(TechDiscordBot.getBot().getStorage().retrieveVerificationWithDiscord(args[1]) != null) {
                new CustomEmbedBuilder("Error").error().setText(mem.getAsMention() + " (" + args[1] + ") is already verified!").sendTemporary(channel, 10);
                return;
            }
            if(bot.getSpigotAPI().getPurchases().userId(args[0]).size() == 0) {
                new CustomEmbedBuilder("Error").error().setText(mem.getAsMention() + " (" + args[1] + ") does not own any of Tech's Plugins!").sendTemporary(channel, 10);
                return;
            }
            TechDiscordBot.getBot().getStorage().createVerification(args[0], args[1]);
            new CustomEmbedBuilder("Success").success().setText("Successfully verified " + mem.getAsMention() + " (" + args[1] + ")").send(channel);
        } else {
            new CustomEmbedBuilder("Error").error().setText("!update <spigotId> <discordId>").sendTemporary(channel, 10);
        }
    }
}
