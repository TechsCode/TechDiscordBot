package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.Objects;

public class PluginCommand extends CommandModule {

    public PluginCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "howto";
    }

    @Override
    public String getDescription() { return "Get info/links about a plugin."; }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "plugin", "Select Plugin", true)
                        .addChoice("Vault", "Vault")
                        .addChoice("PlaceholderAPI", "PlaceholderAPI")
                        .addChoice("ProtocolLib", "ProtocolLib")
                        .addChoice("TAB", "TAB")
                        .addChoice("NametagEdit", "NametagEdit"),

        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String topic = Objects.requireNonNull(e.getOption("plugin")).getAsString();

        if (topic.equalsIgnoreCase("Vault")) { e.reply("https://www.spigotmc.org/resources/vault.34315/").queue(); }
        if (topic.equalsIgnoreCase("PlaceholderAPI")) { e.reply("https://www.spigotmc.org/resources/placeholderapi.6245/").queue(); }
        if (topic.equalsIgnoreCase("ProtocolLib")) { e.reply("https://www.spigotmc.org/resources/protocollib.1997/").queue(); }
        if (topic.equalsIgnoreCase("TAB")) { e.reply("https://github.com/NEZNAMY/TAB").queue(); }
        if (topic.equalsIgnoreCase("NametagEdit")) { e.reply("https://github.com/NEZNAMY/TAB").queue(); }}}