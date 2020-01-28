package me.TechsCode.TechDiscordBot.util;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static boolean isStaff(Member member) { return member.getRoles().stream().anyMatch(r -> r.getName().contains("Supporter") || r.getName().contains("Staff")); }

    public static List<String> runBashCommand(String command) {
        try {
            String[] commandWithArgs = new String[]{"/bin/bash", "-send"};
            ArrayUtils.addAll(commandWithArgs, command.split(" "));
            Process proc = new ProcessBuilder(commandWithArgs).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            List<String> lines = reader.lines().collect(Collectors.toList());
            proc.waitFor();
            lines.forEach(l -> TechDiscordBot.getBot().log(l));
            return lines;
        } catch (IOException | InterruptedException ex) {
            TechDiscordBot.getBot().log(ConsoleColor.RED + "An error has occurred while trying to execute a bash command (" + command + "):");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
