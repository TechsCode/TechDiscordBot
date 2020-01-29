package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.command.CommandCategory;
import me.TechsCode.TechDiscordBot.command.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
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
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(args.length == 0) {
            listCommands(channel,"", canSeeStaffCommands(member), false);
        } else {
            CommandModule cmdM = TechDiscordBot.getBot().getCommandModules().stream().filter(cmd -> cmd != null && cmd.getCommand() != null && cmd.getCommand().replace("!", "").equals(args[0])).findFirst().orElse(null);
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
        new CustomEmbedBuilder("Help - " + cmd.getCommand() + " Command")
                .addField("Command", cmd.getCommand(), true)
                .addField("Aliases", (cmd.getAliases().length == 0 ? "None" : String.join(", ", cmd.getAliases()) + "."), true)
                .addField("Category", WordUtils.capitalizeFully(cmd.getCategory().name()), true)
                .addField("Requirements", (cmd.getRequirements().length == 0 ? "None" : "Staff"), true)
                .send(channel);
    }

    public void listCommands(TextChannel channel,  String cmdNotFound, boolean seeStaff, boolean notFound) {
        List<CommandModule> commands = TechDiscordBot.getBot().getCommandModules();
        if(!seeStaff) commands = TechDiscordBot.getBot().getCommandModules().stream().filter(cmd -> cmd.getRequirements() == null).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        if(notFound) sb.append("**!").append(cmdNotFound).append(" command not found!**\n\n");
        commands.forEach(cmd -> sb.append("`").append(cmd.getCommand()).append("`").append(", "));
        sb.delete(sb.length() -2, sb.length() - 1);
        sb.append(".");
        sb.append("\n\n*Execute* `!help <command>` *to view more information about that command!*");
        new CustomEmbedBuilder("Help - Commands")
                .setText(sb.toString())
                .send(channel);
    }
}
