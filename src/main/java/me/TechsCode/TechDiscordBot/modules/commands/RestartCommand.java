package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.command.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Module;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.Util;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class RestartCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public RestartCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!restart"; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        TechDiscordBot.getBot().getModules().forEach(Module::onDisable);
        TechDiscordBot.getBot().getCommandModules().forEach(CommandModule::onDisable);
        new CustomEmbedBuilder("Restart Command").setText("Attempting to restart the bot.").send(channel);
        Util.runBashCommandArgs(new String[]{"/bin/bash", "-c", "sh ./restart.sh"});
        System.exit(0);
    }
}
