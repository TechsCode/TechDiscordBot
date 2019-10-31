package me.TechsCode.TechDiscordBot.util;


import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;

public enum PluginSettings {

    ULTRA_PERMISSIONS("42678", "416194311080771596", new Color(0,235,229), "http://i.techmods.de/Spigot/default/Ultra%20Permissions%20-%20Logo.png"),
    YOUTUBE_BRIDGE("35928", "416194347294392321", new Color(241,90,0), "http://i.techmods.de/Spigot/default/Youtube%20Bridge%20-%20Logo.png"),
    ULTEA_CUSTOMIZER("49330", "416194287567372298", new Color(48, 101, 240), "http://i.techmods.de/Spigot/default/Hydra%20-%20Logo.png"),
    ULTRA_REGIONS("58317", "465975554101739520", new Color(180,200,59), "http://i.techmods.de/Spigot/default/ReWorld%20-%20Logo.png"),
    ULTRA_PUNISHMENTS("63511", "531255363505487872", new Color(247, 119, 39), "https://www.spigotmc.org/data/resource_icons/63/63511.jpg?1546006549");

    private String resourceId;
    private String roleId;
    private Color color;
    private String resourceLogo;

    PluginSettings(String resourceId, String roleId, Color color, String resourceLogo){
        this.resourceId = resourceId;
        this.roleId = roleId;
        this.color = color;
        this.resourceLogo = resourceLogo;
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

    public static PluginSettings fromId(String resourceId){
        for(PluginSettings all : values()){
            if(all.getResourceId().equals(resourceId)){
                return all;
            }
        }

        return null;
    }
}