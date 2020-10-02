package me.TechsCode.SpigotAPI.logging;

public class Logger {

    public static void log(String message){
        if(message.isEmpty()){
            System.out.println();
            return;
        }

        System.out.println(ConsoleColor.BLUE_BRIGHT+"["+ConsoleColor.WHITE_BOLD_BRIGHT+"SpigotAPI"+ConsoleColor.BLUE_BRIGHT+"] "+ConsoleColor.RESET+message);
    }
}
