package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

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

    public OverviewCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "overview";
    }

    @Override
    public String getDescription() {
        return "Resend the #overview messages.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        if(!TechDiscordBot.getBot().getStatus().isUsable()) {
            new TechEmbedBuilder("API").text("The API has to be usable to execute this command!").error().sendTemporary(channel, 5);
            return;
        }

//        OVERVIEW_CHANNEL.query().first().getIterableHistory()
//                .takeAsync(200)
//                .thenAccept(channel::purgeMessages);

        e.deferReply().queue();
        e.reply("Sending messages...").queue();

        showAll();
    }

    public void showAll() {
        showInfo();
        showFeedback();
        //showRules();
        //showPlugins();
        showInvite();
        //showVerify();
        showUltraPermissions();
        showUltraCustomizer();
        showUltraRegions();
        showUltraPunishments();
        showInsaneShops();
        showUltraEconomy();
        showUltraScoreboard();
    }

    public void showVerify() {
        new TechEmbedBuilder("Verify Yourself")
            .text("Due to the recent bot attack, you're now required to verify yourself, to do so, all you have to do is react to the message below!")
            .thumbnail("https://www.groovypost.com/wp-content/uploads/2016/11/500px-Icon_robot.svg_.png")
            .queue(OVERVIEW_CHANNEL.query().first(), msg -> msg.addReaction(bot.getEmotes("TechSupport").first()).complete());
    }

    @SubscribeEvent
    public void onReactAdd(GuildMessageReactionAddEvent e) {
        if(e.getChannel() != OVERVIEW_CHANNEL.query().first() || e.getUser().isBot())
            return;

        Emote emote = bot.getEmotes("TechSupport").first();

        if(e.getReaction().getReactionEmote().isEmote() && e.getReaction().getReactionEmote().getEmote() == emote) {
            if(e.getMember().getRoles().stream().anyMatch(r -> r.getName().equals("Member")))
                return;

            e.getGuild().addRoleToMember(e.getMember(), bot.getRoles("Member").first()).queue();
        }
    }

    public void showInfo() {
        new TechEmbedBuilder("Tech's Plugin Support")
                .text("Welcome to **Tech's Plugin Support**. Here, not only can you get support for Tech's Plugins. You can talk and socialize with people too! You can also get help with other plugins!\n\nIf you're new here and need help with one or more of Tech's Plugins, you can verify in <#695493411117072425> to get support. Once you do, you will get access to the specified support channels.\n\nIf you are already verified and you have bought another plugin, simply wait for the bot to give you the role *(could take up to 15 minutes, possibly longer)*.")
                .thumbnail("https://i.imgur.com/SfFEnoU.png")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showFeedback() {
        new TechEmbedBuilder("Feedback")
                .text("Would you like to suggest a feature or report a bug for Tech's Plugins? You can do so by clicking here: https://feedback.techscode.de")
                .thumbnail("https://i.imgur.com/nzfiUTy.png")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showInvite() {
        new TechEmbedBuilder()
                .text("**Oh, look!** There is an invite: https://discord.gg/3JuHDm8")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showRules() {
        new TechEmbedBuilder("Rules")
                .text("Just use common sense. Also, do not mention people for absolutely no reason.\n\nIf a staff member gives you a warning, kick, or ban, do not argue about it, and simply don't do the thing you were warned about again! Staff decide what they think is right or wrong.\n\n**The staff's decisions are final.**")
                .thumbnail("https://static.thenounproject.com/png/358077-200.png")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showUltraPermissions() {
        new TechEmbedBuilder("Ultra Permissions")
                .text("The Ultimate GUI based Permissions Plugin for Spigot & Bungee.\n\n**Download Links:**\n:spigot: **-** [SpigotMC](https://www.spigotmc.org/resources/ultra-permissions.42678/)\n:mc-market: **-** [MC-Market]https://www.mc-market.org/resources/19028/)\n:songoda: **-** [Songoda](https://songoda.com/marketplace/product/ultra-permissions-the-ultimate-gui-based-permissions-plugin-for-spigot-bungee.150)\n\n**Wiki:**\n:UltraPermissions: **-** https://www.ultrapermissions.com")
                .thumbnail("https://www.spigotmc.org/data/resource_icons/42/42678.jpg?75455775")
                .queue(OVERVIEW_CHANNEL.query().first());

    }

    public void showUltraCustomizer() {
        new TechEmbedBuilder("Ultra Customizer")
                .text("The Ultimate Customization Plugin fully controllable via GUI's w/ Script System.\n\n**Download Links:**\n:spigot: **-** [SpigotMC](https://www.spigotmc.org/resources/ultra-customizer.49330/)\n:mc-market: **-** [MC-Market](https://www.mc-market.org/resources/19029/)\n:songoda: **-** [Songoda](https://songoda.com/marketplace/product/ultra-customizer-the-ultimate-customization-plugin-fully-controllable-via-guis.151)\n\n**Wiki:**\n:UltraPermissions: **-** https://www.ultracustomizer.com")
                .thumbnail("https://www.spigotmc.org/data/resource_icons/49/49330.jpg?575757457475")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showUltraRegions() {
        new TechEmbedBuilder("Ultra Regions")
                .text("The Ultimate replacement for Multiverse and World Guard featuring a modern GUI.\n\n**Download Links:**\n:spigot: **-** [SpigotMC](https://www.spigotmc.org/resources/ultra-regions.58317/)\n:mc-market: **-** [MC-Market](https://www.mc-market.org/resources/19031/)\n:songoda: **-** [Songoda](https://songoda.com/marketplace/product/ultra-regions-the-ultimate-replacement-for-multiverse-and-worldguard-featuring-a-gui.152)\n\n**Wiki:**\n:UltraPermissions: **-** https://www.ultraregions.com")
                .thumbnail("https://www.spigotmc.org/data/resource_icons/58/58317.jpg?5775757457")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showUltraPunishments() {
        new TechEmbedBuilder("Ultra Punishments")
                .text("Next Generation Punishment Plugin fully controllable via GUI's w/ Report System.\n\n**Download Links:**\n:spigot: **-** [SpigotMC](https://www.spigotmc.org/resources/ultra-punishments.63511/)\n:mc-market: **-** [MC-Market](https://www.mc-market.org/resources/19030/)\n:songoda: **-** [Songoda](https://songoda.com/marketplace/product/ultra-punishments-next-generation-punishment-plugin-fully-controllable-via-guis.154)\n\n**Wiki:**\n:UltraPermissions: **-** https://www.ultrapunishments.com")
                .thumbnail("https://www.spigotmc.org/data/resource_icons/63/63511.jpg?1597561836")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showInsaneShops() {
        new TechEmbedBuilder("Insane Shops")
                .text("Fully interactive Chest Shop Plugin with Holograms fully controlled with GUI's.\n\n**Download Links:**\n:spigot: **-** [SpigotMC](https://www.spigotmc.org/resources/insaneshops.67352/)\n:mc-market: **-** [MC-Market](https://www.mc-market.org/resources/19032/)\n:songoda: **-** [Songoda](https://songoda.com/marketplace/product/insane-shops-fully-interactive-chest-shop-plugin-that-is-fully-controlled-with-guis.153)\n\n**Wiki:**\n:UltraPermissions: **-** https://www.insaneshops.com")
                .thumbnail("https://www.spigotmc.org/data/resource_icons/67/67352.jpg?1597561788")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showUltraEconomy() {
        new TechEmbedBuilder("Ultra Economy")
                .text("The Ultimate GUI based Economy Plugin which adds Currencies, Trading System and more.\n\n**Download Links:**\n:spigot: **-** [SpigotMC](https://www.spigotmc.org/resources/ultra-economy.83374/)\n:mc-market: **-** [MC-Market](https://www.mc-market.org/resources/19033/)\n:songoda: **-** [Songoda](https://songoda.com/marketplace/product/ultra-economy-the-ultimate-gui-based-economy-plugin-for-spigot-bungee.639)\n\n**Wiki:**\n:UltraPermissions: **-** https://www.ultraeconomy.com")
                .thumbnail("https://www.spigotmc.org/data/resource_icons/83/83374.jpg?1598896895")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showUltraScoreboard() {
        new TechEmbedBuilder("Ultra Scoreboard")
                .text("The Ultimate Scoreboards Plugin fully controllable via GUI for Spigot & Bungee.\n\n\n**Download Links:**\n\n**Download Links:**\n:spigot: **-** [SpigotMC](https://www.spigotmc.org/resources/ultra-scoreboards.93726/)\n\n**Wiki:**\n:UltraPermissions: **-** https://www.ultrascoreboard.com")
                .thumbnail("https://www.spigotmc.org/data/resource_icons/93/93726.jpg?1624925787")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

//    public void showPlugins() {
//        Arrays.stream(Plugin.values()).forEach(plugin -> new TechEmbedBuilder(plugin.getRoleName())
//                .text(plugin.getDescription().replace(" (", ". (") + (plugin.getDescription().endsWith(".") || plugin.getDescription().endsWith(")") ? "" : "."))
//                .thumbnail(plugin.getResourceLogo())
//                .color(plugin.getColor())
//                .queue(OVERVIEW_CHANNEL.query().first()));
    }
}
