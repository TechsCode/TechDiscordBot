package me.TechsCode.TechDiscordBot.util;

import com.techeazy.spigotapi.data.collections.PurchaseCollection;
import com.techeazy.spigotapi.data.objects.Update;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchase;
import me.TechsCode.TechDiscordBot.storage.Verification;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Plugin {

    ULTRA_PERMISSIONS("Ultra Permissions","42678", "416194311080771596", new Color(0,235,229), "UltraPermissions", "https://ultrapermissions.com/wiki"),
    YOUTUBE_BRIDGE("Youtube Bridge", "35928", "416194347294392321", new Color(241,90,0),"YoutubeBridge", ""),
    ULTRA_CUSTOMIZER("Ultra Customizer", "49330", "416194287567372298", new Color(48, 101, 240), "UltraCustomizer", "https://ultracustomizer.com/wiki"),
    ULTRA_REGIONS("Ultra Regions", "58317", "465975554101739520", new Color(180,200,59), "UltraRegions", "https://ultraregions.com/wiki"),
    ULTRA_PUNISHMENTS("Ultra Punishments", "63511", "531255363505487872", new Color(247, 119, 39), "UltraPunishments", "https://ultrapunishments.com/wiki"),
    INSANE_SHOPS("Insane Shops", "67352", "531255363505487872", new Color(61, 135, 152), "InsaneShops", "https://insaneshops.com/wiki");

    private String resourceId, roleName, roleId, emojiName, wiki;
    private Color color;

    Plugin(String roleName, String resourceId, String roleId, Color color, String emojiName, String wiki) {
        this.roleName = roleName;
        this.resourceId = resourceId;
        this.roleId = roleId;
        this.color = color;
        this.emojiName = emojiName;
        this.wiki = wiki;
    }

    public String getResourceId() { return resourceId; }

    public Role getRole(Guild guild) { return guild.getRoleById(roleId); }

    public Color getColor() { return color; }

    public String getResourceLogo() { return TechDiscordBot.getBot().getSpigotAPI().getResources().resourceId(resourceId).get()[0].getIcon(); }

    public String getEmojiName() { return emojiName; }

    public Emote getEmoji() { return TechDiscordBot.getJDA().getEmotesByName(getEmojiName(), false).get(0); }

    public String getRoleName() { return roleName; }

    public String getWiki() { return wiki; }

    public boolean hasWiki() { return !wiki.isEmpty(); }

    public String getResourceIcon() { return TechDiscordBot.getBot().getSpigotAPI().getResources().resourceId(getResourceId()).get()[0].getIcon(); }

    public Update getLatestUpdate() { return TechDiscordBot.getBot().getSpigotAPI().getUpdates().resourceId(getResourceId()).get()[TechDiscordBot.getBot().getSpigotAPI().getUpdates().resourceId(getResourceId()).size() - 1]; }

    public static List<Plugin> allWithWiki() { return Arrays.stream(Plugin.values()).filter(Plugin::hasWiki).collect(Collectors.toList()); }

    public static Plugin byRoleName(String roleName) { return Arrays.stream(Plugin.values()).filter(p -> p.getRoleName().equals(roleName)).findFirst().orElse(null); }

    public static Plugin byEmote(Emote emote) { return Arrays.stream(values()).filter(e -> emote.getId().equals(e.getEmoji().getId())).findFirst().orElse(null); }

    public static List<Plugin> fromUser(Member member) {
        try {
            Verification verification = TechDiscordBot.getBot().getStorage().retrieveVerificationWithDiscord(member.getUser().getId());
            PurchaseCollection pc = null;
            try {
                pc = TechDiscordBot.getBot().getSpigotAPI().getPurchases().userId(verification.getUserId());
            } catch (NullPointerException ignored) {
                TechDiscordBot.getBot().log(ConsoleColor.RED + "Could not find any SpigotMC plugins for " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator());
            }
            List<SongodaPurchase> purchases = null;
            try {
                purchases = TechDiscordBot.getBot().getSongodaAPIClient().getPurchases(member.getUser());
            } catch (NullPointerException ignored) {
                TechDiscordBot.getBot().log(ConsoleColor.RED + "Could not find any Songoda plugins for " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator());
            }
            List<Plugin> plugins = new ArrayList<>();
            if(pc != null) plugins = Arrays.stream(pc.get()).map(purchase -> fromId(purchase.getResourceId())).collect(Collectors.toList());
            if(purchases != null) {
                for (SongodaPurchase purchase : purchases) {
                    //TechDiscordBot.getBot().log(purchase.getName() + " bought by " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator());
                    Plugin plugin = Plugin.byRoleName(purchase.getName());
                    if (plugin != null && !plugins.contains(plugin)) plugins.add(plugin);
                }
            }
            return plugins;
        } catch (NullPointerException ex) {
            TechDiscordBot.getBot().log("Error:");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Plugin fromId(String resourceId) { return Arrays.stream(values()).filter(all -> all.getResourceId().equals(resourceId)).findFirst().orElse(null); }
}