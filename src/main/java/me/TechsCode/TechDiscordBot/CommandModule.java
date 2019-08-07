package me.TechsCode.TechDiscordBot;

import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandModule extends Module {

    public CommandModule(TechDiscordBot bot) {
        super(bot);
    }

    @SubscribeEvent
    public void onCommand(GuildMessageReceivedEvent e){
        if(!e.getChannelType().equals(ChannelType.TEXT)) return;
        String first = e.getMessage().getContentDisplay().split(" ")[0];

        if(!first.equalsIgnoreCase(getCommand())) return;

        List<Role> restrictedRoles = getRestrictedRoles().query().all();
        List<TextChannel> restrictedChannels = getRestrictedChannels().query().all();

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
        Set<Requirement> requirements = new HashSet<>();

        if(getRestrictedRoles() != null) requirements.add(new Requirement(getRestrictedRoles(), 1, "No Roles found which are suitable for running this Command (Missing Restricted Roles)"));
        if(getRestrictedChannels() != null) requirements.add(new Requirement(getRestrictedChannels(), 1, "No Channels found which are suitable for running this Command (Missing Restricted Channels)"));

        return requirements.toArray(new Requirement[0]);
    }

    public abstract String getCommand();

    public abstract DefinedQuery<Role> getRestrictedRoles();

    public abstract DefinedQuery<TextChannel> getRestrictedChannels();

    public abstract void onCommand(TextChannel channel, Member member, String[] args);
}
