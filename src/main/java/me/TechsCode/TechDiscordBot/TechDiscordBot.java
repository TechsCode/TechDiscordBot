package me.TechsCode.TechDiscordBot;

import me.TechsCode.TechDiscordBot.storage.Storage;
import me.TechsCode.TechDiscordBot.util.Project;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;
import me.TechsCode.TechsCodeAPICli.TechsCodeAPIClient;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

public class TechDiscordBot implements EventListener {

    private final String TECHSCODEAPI = "api.techsco.de";

    private JDA jda;
    private Guild guild;
    private Member self;

    private TechsCodeAPIClient techsCodeAPIClient;
    private Storage storage;

    private List<Module> modules;

    public static void main(String[] args) {
        if (args.length != 7) {
            System.out.println("Invalid start arguments. Consider using:");
            System.out.println("java -jar TechPluginSupportBot.jar <Discord Bot Token> <Tech API Token> <MySQL Host> <MySQL Port> <MySQL Database> <MySQL Username> <MySQL Password>");
            return;
        }

        new TechDiscordBot(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
    }

    public TechDiscordBot(String token, String apiToken, String mysqlHost, String mysqlPort, String mysqlDatabase, String mysqlUsername, String mysqlPassword) {
        try {
            try {
                jda = new JDABuilder(AccountType.BOT)
                        .setEventManager(new AnnotatedEventManager())
                        .setToken(token).build().awaitReady();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        List<Guild> guilds = jda.getGuilds();

        guild = guilds.size() != 0 ? guilds.get(0) : null;
        self = guild != null ? guild.getSelfMember() : null;

        if(guild == null){
            log(ConsoleColor.RED+"The Bot is not a member of a guild");
            return;
        }

        if(guilds.size() > 1){
            log(ConsoleColor.RED+"The Bot is a member of too many guilds.");
            return;
        }

        log("Successfully logged in as "+self.getEffectiveName()+" into "+guild.getName());

        log("Connecting to "+TECHSCODEAPI+"..");
        this.techsCodeAPIClient = new TechsCodeAPIClient(TECHSCODEAPI, apiToken);

        log("Initializing MySQL Storage "+mysqlHost+":"+mysqlPort+"..");
        this.storage = new Storage(mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword);

        if(!storage.isEnabled()){
            log(ConsoleColor.RED+"Failed connecting to MySQL:");
            log(storage.getErrorMessage());
            return;
        }

        log("Loading modules...");
        modules = new ArrayList<>();

        for(Class each : Project.getClasses("me.TechsCode.")){
            if(Module.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())){
                try {
                    Module module = (Module) each.getConstructor(TechDiscordBot.class).newInstance(this);

                    if(module.isEnabled()){
                        modules.add(module);
                    }

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        jda.addEventListener(modules.toArray());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> modules.forEach(Module::onDisable)));

        log("Successfully loaded "+modules.size()+" modules");
    }

    public JDA getJDA() {
        return jda;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getSelf() {
        return self;
    }

    public TechsCodeAPIClient getTechsCodeAPI() {
        return techsCodeAPIClient;
    }

    public Storage getStorage() {
        return storage;
    }

    public void log(String message){
        System.out.println(ConsoleColor.BLUE_BRIGHT+"["+ConsoleColor.WHITE_BOLD_BRIGHT+"Discord Bot"+ConsoleColor.BLUE_BRIGHT+"] "+ConsoleColor.RESET+message);
    }

    public Role getRole(String name){
        return guild.getRolesByName(name, true).stream().findFirst().orElse(null);
    }

    public TextChannel getChannel(String name){
        return guild.getTextChannelsByName(name, true).stream().findFirst().orElse(null);
    }

    public Category getCategory(String name){
        return guild.getCategoriesByName(name, true).stream().findFirst().orElse(null);
    }

    public Emote getEmote(String name){
        return guild.getEmotesByName(name, true).stream().findFirst().orElse(null);
    }
}
