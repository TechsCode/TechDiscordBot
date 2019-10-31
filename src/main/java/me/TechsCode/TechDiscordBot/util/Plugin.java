package me.TechsCode.TechDiscordBot.util;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.storage.Verification;
import me.TechsCode.TechsCodeAPICli.collections.PurchaseCollection;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Plugin {

    ULTRA_PERMISSIONS("42678", "416194311080771596", new Color(0,235,229), "", "UltraPermissions"),
    YOUTUBE_BRIDGE("35928", "416194347294392321", new Color(241,90,0), "","YoutubeBridge"),
    ULTRA_CUSTOMIZER("49330", "416194287567372298", new Color(48, 101, 240), "", "UltraCustomizer"),
    ULTRA_REGIONS("58317", "465975554101739520", new Color(180,200,59), "", "UltraRegions"),
    ULTRA_PUNISHMENTS("63511", "531255363505487872", new Color(247, 119, 39), "", "UltraPunishments"),
    INSANE_SHOPS("67352", "531255363505487872", new Color(61, 135, 152), "", "InsaneShops");

    private String resourceId;
    private String roleId;
    private Color color;
    private String resourceLogo;
    private String emojiName;

    Plugin(String resourceId, String roleId, Color color, String resourceLogo, String emojiName) {
        this.resourceId = resourceId;
        this.roleId = roleId;
        this.color = color;
        this.resourceLogo = resourceLogo;
        this.emojiName = emojiName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Role getRole(Guild guild){
        return guild.getRoleById(roleId);
    }

    public Color getColor() {
        return color;
    }

    public String getResourceLogo() {
        return resourceLogo;
    }

    public String getEmojiName() {
        return emojiName;
    }

    public Emote getEmoji() {
        return TechDiscordBot.getJDA().getEmotesByName(getEmojiName(), false).get(0);
    }

    public static List<Plugin> fromUser(Member member) {
        Verification verification = TechDiscordBot.getBot().getStorage().retrieveVerificationWithDiscord(member.getUser().getId());
        PurchaseCollection pc = TechDiscordBot.getBot().getTechsCodeAPI().getPurchases().userId(verification.getUserId());
        return Arrays.stream(pc.get()).map(purchase -> fromId(purchase.getResourceId())).collect(Collectors.toList());
    }

    public static Plugin fromId(String resourceId) {
        for(Plugin all : values()) {
            if(all.getResourceId().equals(resourceId)) {
                return all;
            }
        }
        return null;
    }
}