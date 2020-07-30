package me.TechsCode.TechDiscordBot.util;

import me.TechsCode.SpigotAPI.client.collections.PurchaseCollection;
import me.TechsCode.SpigotAPI.client.objects.Resource;
import me.TechsCode.SpigotAPI.client.objects.Update;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum Plugin {

    ULTRA_PERMISSIONS("Ultra Permissions","42678", "416194311080771596", "330053303050436608", new Color(0,235,229), "UltraPermissions", "https://ultrapermissions.com/wiki", "https://www.spigotmc.org/data/resource_icons/42/42678.jpg?1551053784"),
    //YOUTUBE_BRIDGE("Youtube Bridge", "35928", "416194347294392321", "311178282278322176", new Color(216, 56, 43),"YoutubeBridge", "", "https://www.spigotmc.org/data/resource_icons/35/35928.jpg?1551053840"),
    ULTRA_CUSTOMIZER("Ultra Customizer", "49330", "416194287567372298", "380133603683860480", new Color(184, 103, 243), "UltraCustomizer", "https://ultracustomizer.com/wiki", "https://www.spigotmc.org/data/resource_icons/49/49330.jpg?1552009867"),
    ULTRA_REGIONS("Ultra Regions", "58317", "465975554101739520", "465975795433734155", new Color(57, 135, 153), "UltraRegions", "https://ultraregions.com/wiki", "https://www.spigotmc.org/data/resource_icons/58/58317.jpg?1557800307"),
    ULTRA_PUNISHMENTS("Ultra Punishments", "63511", "531255363505487872", "531251918291599401", new Color(247, 119, 39), "UltraPunishments", "https://ultrapunishments.com/wiki", "https://www.spigotmc.org/data/resource_icons/63/63511.jpg?1551053820"),
    INSANE_SHOPS("Insane Shops", "67352", "531255363505487872", "576813543698202624", new Color(114, 185, 77), "InsaneShops", "https://insaneshops.com/wiki", "https://www.spigotmc.org/data/resource_icons/67/67352.jpg?1557794141");

    private final String resourceId, channelId, roleName, roleId, emojiName, wiki, logo;
    private final Color color;

    Plugin(String roleName, String resourceId, String roleId, String channelId, Color color, String emojiName, String wiki, String logo) {
        this.roleName = roleName;
        this.resourceId = resourceId;
        this.channelId = channelId;
        this.roleId = roleId;
        this.color = color;
        this.emojiName = emojiName;
        this.wiki = wiki;
        this.logo = logo;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getRoleId() {
        return roleId;
    }

    public Role getRole(Guild guild) {
        return guild.getRoleById(roleId);
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return TechDiscordBot.getSpigotAPI().getResources().id(resourceId).get().getTagLine();
    }

    public String getResourceLogo() {
        return logo;
    }

    public String getEmojiName() {
        return emojiName;
    }

    public Emote getEmoji() {
        return TechDiscordBot.getJDA().getEmotesByName(getEmojiName(), false).get(0);
    }

    public String getRoleName() {
        return roleName;
    }

    public String getWiki() {
        return wiki;
    }

    public boolean hasWiki() {
        return !wiki.isEmpty();
    }

    public Resource getResource() {
        return TechDiscordBot.getSpigotAPI().getResources().id(getResourceId()).get();
    }

    public Update getLatestUpdate() {
        return TechDiscordBot.getSpigotAPI().getUpdates().resourceId(getResourceId()).get()[TechDiscordBot.getSpigotAPI().getUpdates().resourceId(getResourceId()).size() - 1];
    }

    public static List<Plugin> allWithWiki() {
        return Arrays.stream(Plugin.values()).filter(Plugin::hasWiki).collect(Collectors.toList());
    }

    public static String getEmotesByList(List<String> plugins) {
        String sb = plugins.stream().map(Plugin::byRoleName).filter(Objects::nonNull).map(plugin -> plugin.getEmoji().getAsMention() + " ").collect(Collectors.joining());
        if(sb.length() < 4) return "";
        return sb.substring(0, sb.length() - 1);
    }

    public static boolean isPluginChannel(TextChannel channel) {
        return Arrays.stream(Plugin.values()).anyMatch(p -> channel.getId().equals(p.getChannelId()));
    }

    public static Plugin byChannel(TextChannel channel) {
        return Arrays.stream(Plugin.values()).filter(p -> channel.getId().equals(p.getChannelId())).findFirst().orElse(null);
    }

    public static Plugin byRoleName(String roleName) {
        return Arrays.stream(Plugin.values()).filter(p -> p.getRoleName().equals(roleName)).findFirst().orElse(null);
    }

    public static Plugin byEmote(Emote emote) {
        return Arrays.stream(values()).filter(e -> emote.getId().equals(e.getEmoji().getId())).findFirst().orElse(null);
    }

    public static List<Plugin> fromUserUsingRoles(Member member) {
        return member.getRoles().stream().filter(role -> Plugin.byRoleName(role.getName()) != null).map(role -> Plugin.byRoleName(role.getName())).collect(Collectors.toList());
    }

    public static Plugin fromId(String resourceId) {
        return Arrays.stream(values()).filter(all -> all.getResourceId().equals(resourceId)).findFirst().orElse(null);
    }

    public static String getMembersPluginsinEmojis(Member member) {
        return getMembersPluginsinEmojis(member, "None");
    }

    public static String getMembersPluginsinEmojis(Member member, String defaultResponse) {
        List<Plugin> plugins = Plugin.fromUser(member);
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Plugin p : plugins) {
            if (i != 0) sb.append(" ");
            sb.append(p.getEmoji().getAsMention());
            i++;
        }
        if(i == 0) sb.append(defaultResponse);
        return sb.toString();
    }

    public static List<Plugin> fromUser(Member member) {
        try {
            Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(member.getUser().getId());

            PurchaseCollection pc = null;
            try {
                pc = TechDiscordBot.getSpigotAPI().getPurchases().userId(verification.getUserId());
            } catch (NullPointerException ignored) {
                TechDiscordBot.log(ConsoleColor.RED + "Could not find any SpigotMC plugins for " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator());
            }

            List<Plugin> plugins = new ArrayList<>();
            if(pc != null) plugins = Arrays.stream(pc.get()).map(purchase -> fromId(purchase.getResourceId())).collect(Collectors.toList());

            return plugins;
        } catch (NullPointerException ex) {
            TechDiscordBot.log(ConsoleColor.RED + "Error:");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}