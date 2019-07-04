package me.TechsCode.TechDiscordBot;

import me.TechsCode.TechDiscordBot.requirements.ChannelRequirement;
import me.TechsCode.TechDiscordBot.requirements.RoleRequirement;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CommandModule extends Module {

    public CommandModule(TechDiscordBot bot) {
        super(bot);
    }

    @SubscribeEvent
    public void onCommand(GuildMessageReceivedEvent e){
        String first = e.getMessage().getContentDisplay().split(" ")[0];

        if(!first.equalsIgnoreCase(getCommand())) return;

        Set<Role> restrictedRoles = Arrays.stream(getRestrictedRoles()).map(bot::getRole).collect(Collectors.toSet());
        Set<TextChannel> restrictedChannels = Arrays.stream(getRestrictedChannels()).map(bot::getChannel).collect(Collectors.toSet());

        // Check if the player has at least one of the restricted roles
        if(!restrictedRoles.isEmpty() && Collections.disjoint(e.getMember().getRoles(), restrictedRoles)){
            return;
        }

        // Check if the message was sent in one of the restricted channels (if there are any)
        if(!restrictedChannels.isEmpty() && !restrictedChannels.contains(e.getChannel())){
            return;
        }

        String message = e.getMessage().getContentDisplay();
        String[] args = Arrays.copyOfRange(message.split(" "), 1, message.length());

        e.getMessage().delete().complete();

        onCommand(e.getChannel(), e.getMember(), args);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public String getName() {
        return "["+getCommand()+"] Command";
    }

    @Override
    public Requirement[] getRequirements() {
        return ArrayUtils.addAll(
                Arrays.stream(getRestrictedRoles()).map(RoleRequirement::new).toArray(RoleRequirement[]::new),
                Arrays.stream(getRestrictedChannels()).map(ChannelRequirement::new).toArray(ChannelRequirement[]::new)
        );
    }

    public abstract String getCommand();

    public abstract String[] getRestrictedRoles();

    public abstract String[] getRestrictedChannels();

    public abstract void onCommand(TextChannel channel, Member member, String[] args);
}
