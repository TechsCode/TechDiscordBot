package me.TechsCode.TechDiscordBot.module.cmds;
 
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
 
public class GuidesCommand extends CommandModule {
 
    public GuidesCommand(TechDiscordBot bot) {
        super(bot);
    }
 
    @Override
    public String getName() { return "guides"; }
 
    @Override
    public String getDescription() {
        return "Get other websites related to techscode.";
    }
 
    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }
 
    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }
 
    @Override
    public int getCooldown() {
        return 5;
    }
 
    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        e.reply("https://guides.ultracustomizer.com/").queue();
    }
}
