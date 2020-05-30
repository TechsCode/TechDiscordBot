package me.TechsCode.TechDiscordBot.util;

import java.nio.charset.StandardCharsets;

public class Base64 {

    public static String toBase64(String s) {
        return org.apache.commons.net.util.Base64.encodeBase64String(s.getBytes());
    }

    public static String fromBase64(String s) {
        return new String(org.apache.commons.net.util.Base64.decodeBase64(s), StandardCharsets.UTF_8);
    }
}
