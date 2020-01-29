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

    public static List<String> runBashCommandArgs(String[] args) {
        try {
            Process proc = Runtime.getRuntime().exec(args);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            List<String> lines = reader.lines().collect(Collectors.toList());
            proc.waitFor();
            return lines;
        } catch (IOException | InterruptedException ex) {
            TechDiscordBot.getBot().log(ConsoleColor.RED + "An error has occurred while trying to execute a bash command (" + String.join(" ", args) + "):");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<String> runBashCommand(String command) {
        try {
            String[] commandWithArgs = new String[]{"/bin/bash", "-c"};
            ArrayUtils.addAll(commandWithArgs, command.split(" "));
            //Process proc = new ProcessBuilder(commandWithArgs).start();
            Process proc = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            List<String> lines = reader.lines().collect(Collectors.toList());
            proc.waitFor();
            return lines;
        } catch (IOException | InterruptedException ex) {
            TechDiscordBot.getBot().log(ConsoleColor.RED + "An error has occurred while trying to execute a bash command (" + command + "):");
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
