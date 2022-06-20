package me.TechsCode.TechDiscordBot.util;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.spigotmc.data.Resource;
import me.TechsCode.TechDiscordBot.spigotmc.data.Update;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.PurchasesList;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public enum Plugin {

    ULTRA_PERMISSIONS("Ultra Permissions", PluginMarketplace.of("42678", "150", "19028", "1201"), "416194311080771596", "330053303050436608", new Color(0,235,229), "UltraPermissions", "https://www.spigotmc.org/data/resource_icons/42/42678.jpg?75455775"),
    ULTRA_CUSTOMIZER("Ultra Customizer", PluginMarketplace.of("49330", "151", "19029", "1203"), "416194287567372298", "380133603683860480", new Color(184, 103, 243), "UltraCustomizer", "https://www.spigotmc.org/data/resource_icons/49/49330.jpg?575757457475"),
    ULTRA_REGIONS("Ultra Regions", PluginMarketplace.of("58317", "152", "19031", "1205"), "465975554101739520", "465975795433734155", new Color(57, 135, 153), "UltraRegions", "https://www.spigotmc.org/data/resource_icons/58/58317.jpg?5775757457"),
    ULTRA_PUNISHMENTS("Ultra Punishments", PluginMarketplace.of("63511", "154", "19030", "1204"), "531255363505487872", "531251918291599401", new Color(247, 119, 39), "UltraPunishments", "https://www.spigotmc.org/data/resource_icons/63/63511.jpg?1597561836"),
    INSANE_SHOPS("Insane Shops", PluginMarketplace.of("67352", "153", "19032", "1202"), "531255363505487872", "576813543698202624", new Color(114, 185, 77), "InsaneShops", "https://www.spigotmc.org/data/resource_icons/67/67352.jpg?1597561788"),
    ULTRA_ECONOMY("Ultra Economy", PluginMarketplace.of("83374", "639", "19033", "1206"), "749034791936196649", "737773631198986240", new Color(255, 198, 10), "UltraEconomy", "https://www.spigotmc.org/data/resource_icons/83/83374.jpg?1598896895"),
    ULTRA_SCOREBOARDS("Ultra Scoreboards", PluginMarketplace.of("93726", "643", "20697", "1401"), "811397836616630352", "858052621574078474", new Color(131, 52, 235), "UltraScoreboards", "https://www.spigotmc.org/data/resource_icons/93/93726.jpg?1624925787"),
    ULTRA_MOTD("Ultra Motd", PluginMarketplace.of("100883", "", "", ""), "936284238519599104", "931264562995540038", new Color(85, 144, 217), "UltraMotd", "https://www.spigotmc.org/data/resource_icons/100/100883.jpg?1647948784");

    private final PluginMarketplace pluginMarketplace;
    private final String channelId, roleName, roleId, emojiName, logo;
    private final Color color;

    Plugin(String roleName, PluginMarketplace pluginMarketplace, String roleId, String channelId, Color color, String emojiName, String logo) {
        this.roleName = roleName;
        this.pluginMarketplace = pluginMarketplace;
        this.channelId = channelId;
        this.roleId = roleId;
        this.color = color;
        this.emojiName = emojiName;
        this.logo = logo;
    }

    public String getResourceId() {
        return pluginMarketplace.getSpigotResourceId();
    }

    public PluginMarketplace getPluginMarketplace() {
        return pluginMarketplace;
    }

    public String getChannelId() {
        return channelId;
    }

    public Optional<TextChannel> getChannel() {
        return Optional.ofNullable(TechDiscordBot.getJDA().getTextChannelById(getChannelId()));
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
        return TechDiscordBot.getSpigotAPI().getSpigotResources().id(getResourceId()).get().getTagLine();
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
        return "https://" + getRoleName().toLowerCase().replace(" ", "") + ".com/wiki";
    }

    public boolean hasWiki() {
        return true;
    }

    public String getBanner() {
        return "https://" + getRoleName().toLowerCase().replace(" ", "") + ".com/banner.png";
    }

    public File getBannerAsFile() {
        String resourceName = getResource().getName().replaceAll(" ", "");
        File bFile = new File("banners/"+resourceName+".png");

        if(bFile.exists())
            return bFile;

        try{
            URL url = new URL(getBanner());
            return Paths.get(url.toURI()).toFile();
        }catch (Exception ignored){}

        return null;
    }

    private Dimension getScaledDimension(Dimension imageSize, Dimension boundary) {
        double widthRatio = boundary.getWidth() / imageSize.getWidth();
        double heightRatio = boundary.getHeight() / imageSize.getHeight();
        double ratio = Math.min(widthRatio, heightRatio);

        return new Dimension((int) (imageSize.width  * ratio),
                (int) (imageSize.height * ratio));
    }

    public Resource getResource() {
        return TechDiscordBot.getSpigotAPI().getSpigotResources().id(getResourceId()).get();
    }

    public Update getLatestUpdate() {
        return TechDiscordBot.getSpigotAPI().getSpigotUpdates().resource(getResourceId()).stream().findFirst().orElse(null);
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

    public static Plugin byEmojiName(String emojiName) {
        return Arrays.stream(Plugin.values()).filter(p -> p.getEmojiName().equals(emojiName)).findFirst().orElse(null);
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

            PurchasesList pc = null;
            try {
                pc = TechDiscordBot.getSpigotAPI().getSpigotPurchases().userId(verification.getUserId());
            } catch (NullPointerException ignored) {
                TechDiscordBot.log(ConsoleColor.RED + "Could not find any SpigotMC plugins for " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator());
            }

            List<Plugin> plugins = new ArrayList<>();
            if(pc != null) plugins = pc.stream().map(purchase -> fromId(purchase.getResource().getId())).collect(Collectors.toList());

            return plugins;
        } catch (NullPointerException ex) {
            TechDiscordBot.log(ConsoleColor.RED + "Error:");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
