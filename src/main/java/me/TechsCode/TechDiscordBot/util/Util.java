package me.TechsCode.TechDiscordBot.util;

import net.dv8tion.jda.core.entities.Member;

public class Util {

    public static boolean isStaff(Member member) {
        return member.getRoles().stream().anyMatch(r -> r.getName().contains("Supporter") || r.getName().contains("Staff"));
    }

}
