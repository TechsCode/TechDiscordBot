package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.Arrays;

public class OverviewCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Staff"); }
    };

    private final DefinedQuery<TextChannel> OVERVIEW_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("overview"); }
    };

    public OverviewCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!overview"; }

    @Override
    public String[] getAliases() { return new String[]{"!o"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.INFO; }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(!TechDiscordBot.getBot().getStatus().isUsable()) {
            new TechEmbedBuilder("API").setText("The API has to be usable to execute this command!").error().sendTemporary(channel, 5);
            return;
        }

        OVERVIEW_CHANNEL.query().first().getHistory().retrievePast(100).complete().forEach(msg -> msg.delete().queue());
        showAll();
    }

    public void showAll() {
        showInfo();
        showFeedback();
        //showRules();
        showPlugins();
        showInvite();
        //showVerify();
    }

    public void showVerify() {
        Message msg = new TechEmbedBuilder("Verify Yourself")
                .setText("Due to the recent bot attack, you're now required to verify yourself, to do so, all you have to do is react to the message below!")
                .setThumbnail("https://www.groovypost.com/wp-content/uploads/2016/11/500px-Icon_robot.svg_.png")
                .send(OVERVIEW_CHANNEL.query().first());
        msg.addReaction(bot.getEmotes("TechSupport").first()).complete();
    }

    @SubscribeEvent
    public void onReactAdd(GuildMessageReactionAddEvent e) {
        if(e.getChannel() != OVERVIEW_CHANNEL.query().first()) return;
        if(e.getUser().isBot()) return;

        Emote emote = bot.getEmotes("TechSupport").first();
        if(e.getReaction().getReactionEmote().isEmote() && e.getReaction().getReactionEmote().getEmote() == emote) {
            if(e.getMember().getRoles().stream().anyMatch(r -> r.getName().equals("Member"))) return;
            e.getGuild().addRoleToMember(e.getMember(), bot.getRoles("Member").first()).complete();
        }
    }

    public void showInfo() {
        new TechEmbedBuilder("Tech's Plugin Support")
                .setText("Welcome to **Tech's Plugin Support**. Here, not only can you get support for Tech's Plugins. You can talk and socialize with people too! You can also get help with other plugins!\n\nIf you're new here and need help with one or more of Tech's Plugins, you can verify in <#416186574078738432> to get support. Once you do, you will get access to the specified support channels.\nIf you are already verified and you have bought another plugin, simply wait for the bot to give you the role *(could take up to 15 minutes, possibly longer)*.")
                .setThumbnail("https://i.imgur.com/SfFEnoU.png")
                .send(OVERVIEW_CHANNEL.query().first());
    }

    public void showFeedback() {
        new TechEmbedBuilder("Feedback")
                .setText("Would you like to suggest features for Tech's Plugins? You can do so by clicking here: https://feedback.techscode.de")
                .setThumbnail("https://i.imgur.com/nzfiUTy.png")
                .send(OVERVIEW_CHANNEL.query().first());
    }

    public void showInvite() {
        new TechEmbedBuilder()
                .setText("**Oh, look!** There is an invite: https://discord.gg/3JuHDm8")
                .send(OVERVIEW_CHANNEL.query().first());
    }

    public void showRules() {
        new TechEmbedBuilder("Rules")
                .setText("Just use common sense. Also, do not mention people for absolutely no reason.\n\nIf a staff member gives you a warning, kick, or ban, do not argue about it, and simply don't do the thing you were warned about again! Staff decide what they think is right or wrong.\n\n**The staff's decisions are final.**")
                .setThumbnail("https://static.thenounproject.com/png/358077-200.png")
                .send(OVERVIEW_CHANNEL.query().first());
    }

    public void showPlugins() {
        Arrays.stream(Plugin.values()).forEach(plugin -> new TechEmbedBuilder(plugin.getRoleName())
                .setText(plugin.getDescription().replace(" (", ". (") + (plugin.getDescription().endsWith(".") || plugin.getDescription().endsWith(")") ? "" : "."))
                .setThumbnail(plugin.getResourceLogo())
                .setColor(plugin.getColor())
                .send(OVERVIEW_CHANNEL.query().first()));
    }
}