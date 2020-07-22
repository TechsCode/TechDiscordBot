package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends CommandModule {

    public HelpCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!help"; }

    @Override
    public String[] getAliases() { return new String[]{"!h", "!commands", "!cmds"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return null; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.INFO; }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 0) {
            listCommands(channel,"", canSeeStaffCommands(member), false);
        } else {
            CommandModule cmdM = TechDiscordBot.getModulesManager().getCommandModules().stream().filter(cmd -> cmd != null && cmd.getCommand() != null && cmd.getCommand().replace("!", "").equals(args[0])).findFirst().orElse(null);
            if(cmdM == null) {
                listCommands(channel, args[0], canSeeStaffCommands(member), true);
                return;
            }
            showCommand(cmdM, channel);
        }
    }

    public boolean canSeeStaffCommands(Member member) {
        return member.getRoles().stream().anyMatch(r -> r.getName().equals("Staff"));
    }

    public void showCommand(CommandModule cmd, TextChannel channel) {
        new TechEmbedBuilder("Help - " + cmd.getCommand() + " Command")
                .addField("Command", cmd.getCommand(), true)
                .addField("Aliases", (cmd.getAliases().length == 0 ? "None" : String.join(", ", cmd.getAliases()) + "."), true)
                .addField("Category", WordUtils.capitalizeFully(cmd.getCategory().name()), true)
                .addField("Requirements", (cmd.getRequirements().length == 0 ? "None" : "Staff"), true)
                .send(channel);
    }

    public void listCommands(TextChannel channel,  String cmdNotFound, boolean seeStaff, boolean notFound) {
        List<CommandModule> commands = TechDiscordBot.getModulesManager().getCommandModules();
        if(!seeStaff) commands = TechDiscordBot.getModulesManager().getCommandModules().stream().filter(cmd -> cmd.getRestrictedRoles() == null).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        if(notFound) sb.append("**!").append(cmdNotFound).append(" command not found!**\n\n");
        commands.forEach(cmd -> sb.append("`").append(cmd.getCommand()).append("`").append(", "));
        sb.delete(sb.length() -2, sb.length() - 1);
        sb.append(".");
        sb.append("\n\n*Execute* `!help <command>` *to view more information about that command!*");
        new TechEmbedBuilder("Help - Commands")
                .setText(sb.toString())
                .send(channel);
    }
}
