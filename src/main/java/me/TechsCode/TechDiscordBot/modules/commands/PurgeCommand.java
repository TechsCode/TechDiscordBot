package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.command.CommandCategory;
import me.TechsCode.TechDiscordBot.command.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class PurgeCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Staff"); }
    };

    public PurgeCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!purge"; }

    @Override
    public String[] getAliases() { return new String[]{"!p"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override public CommandCategory getCategory() { return null; }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 0) {
            new CustomEmbedBuilder("Error - Purge")
                    .setText("Please specify an amount to purge (2-100)")
                    .error()
                    .sendTemporary(channel, 5);
        } else {
            int amount;

            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                new CustomEmbedBuilder("Error - Purge")
                        .setText("Could not get a integer from the value '" + args[0] + "'")
                        .error()
                        .sendTemporary(channel, 5);
                return;
            }

            if(amount < 2 || amount > 100) {
                new CustomEmbedBuilder("Error - Purge")
                        .setText("The int has to be between (2-100), " + amount + " is not in that range!")
                        .error()
                        .sendTemporary(channel, 5);
                return;
            }

            channel.getHistory().retrievePast(amount).complete().forEach(msg -> msg.delete().queue());

            new CustomEmbedBuilder("Success - Purge")
                    .setText("Successfully purged " + amount + " messages!")
                    .success()
                    .sendTemporary(channel, 5);
        }
    }
}
