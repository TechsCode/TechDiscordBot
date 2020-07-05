package me.TechsCode.TechDiscordBot;

import com.gargoylesoftware.htmlunit.HttpMethod;
import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.TechDiscordBot.module.ModulesManager;
import me.TechsCode.TechDiscordBot.mysql.MySQLSettings;
import me.TechsCode.TechDiscordBot.mysql.storage.Storage;
import me.TechsCode.TechDiscordBot.objects.ChannelQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.reminders.ReminderManager;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchase;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchases;
import me.TechsCode.TechDiscordBot.spigotmc.SpigotMC;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TechDiscordBot {

    private static JDA jda;
    private static TechDiscordBot i;

    private static Guild guild;
    private static Member self;

    private static SpigotAPIClient spigotAPIClient;
    private static List<SongodaPurchase> songodaPurchases;

    private static Storage storage;

    private static String imgurClientId, imgurClientSecret;

    private static ModulesManager modulesManager;
    private static ReminderManager remindersManager;

    public static void main(String[] args) {
        if (args.length < 10) {
            log(ConsoleColor.RED + "Invalid start arguments. Consider using:");
            log(ConsoleColor.WHITE_BOLD_BRIGHT + "java -jar TechPluginSupportBot.jar <Discord Bot Token> <Tech API Token> <Songoda Token> <MySQL Host> <MySQL Port> <MySQL Database> <MySQL Username> <MySQL Password> <Imgur Client ID> <Ptero API Key>");
            return;
        }

        new TechDiscordBot(args[0], args[1], args[2], MySQLSettings.of(args[3], args[4], args[5], args[6], args[7]), args[8], args[8], args[9]);
    }

    public TechDiscordBot(String token, String apiToken, String songodaToken, MySQLSettings mySQLSettings, String iClientId, String iClientSecret, String pteroApiKey) {
        try {
            i = this;
            try {
                jda = JDABuilder.createDefault(token)
                        .setEventManager(new AnnotatedEventManager())
                        .setActivity(Activity.listening("for help."))
                        .build().awaitReady();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        List<Guild> guilds = jda.getGuilds();

        if(guilds.size() > 2) {
            log(ConsoleColor.RED + "The bot is a member of too many guilds. Please leave them all except two!");
            return;
        }

        guild = guilds.size() != 0 ? guilds.stream().filter(g -> g.getId().equals("311178000026566658")).findFirst().orElse(guild) : null;
        self = guild != null ? guild.getSelfMember() : null;

        if(guild == null) {
            log(ConsoleColor.RED + "The bot is not a member of any guild. Please join a guild!");
            return;
        }

        spigotAPIClient = new SpigotAPIClient("https://api.techscode.de", apiToken);
        songodaPurchases = SongodaPurchases.getPurchases();

        log("Initializing MySQL Storage " + mySQLSettings.getHost() + ":" + mySQLSettings.getPort() + "!");
        storage = Storage.of(mySQLSettings);

        if(!storage.isConnected()) {
            log(ConsoleColor.RED + "Failed to connect to MySQL!");
            log(storage.getLatestErrorMessage());
        }

        modulesManager = new ModulesManager();
        log("Loading modules..");
        modulesManager.load();

        remindersManager = new ReminderManager();
        log("Loading reminders..");
        remindersManager.load();

        jda.addEventListener(modulesManager);
        jda.addEventListener(remindersManager);

        imgurClientId = iClientId;
        imgurClientSecret = iClientSecret;

        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.OFF);

        log("Successfully loaded the bot and logged into " + guild.getName() + " as " + self.getEffectiveName() + "!");

        log("");

        log("Spigot:");
        if(getSpigotAPI().isAvailable()) {
            log("  » Purchases: " + getSpigotAPI().getPurchases().size());
            log("  » Resources: " + getSpigotAPI().getResources().size());
            log("  » Updates: " + getSpigotAPI().getUpdates().size());
            log("  » Reviews: " + getSpigotAPI().getReviews().size());
        } else {
            log("  » " + ConsoleColor.RED + "Could not connect. Cannot show info!");
        }

        log("");

        log("Songoda: ");
        log("  » Final Purchases: " + getSongodaPurchases().size());

        log("");

        log("Guild:");
        log("  » Members: " + getGuild().getMembers().size());
        log("  » Verified Members: " + getStorage().retrieveVerifications().stream().filter(v -> guild.getMemberById(v.getDiscordId()) != null).count());
        log("  » Review Squad Members: " + getGuild().getMembers().stream().filter(member -> member.getRoles().stream().anyMatch(role -> role.getName().equals("Review Squad"))).count());
        log("  » Donators: " + getGuild().getMembers().stream().filter(member -> member.getRoles().stream().anyMatch(role -> role.getName().contains("Donator"))).count());

        log("");

        getModulesManager().logLoad();

        log("");
        log("Startup Completed! The bot has successfully started!");

        Logger.getLogger("ImgurApi").setLevel(Level.OFF);
        startSpigotCloudflareBypass();
    }

    private void startSpigotCloudflareBypass() {
        new Thread(() -> {
            while(true) {
                SpigotMC.getBrowser().request("https://spigotmc.org", HttpMethod.GET, false);
                try {
                    Thread.sleep(300000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static JDA getJDA() {
        return jda;
    }

    public static TechDiscordBot getBot() {
        return i;
    }

    public static Guild getGuild() {
        return guild;
    }

    public static Member getSelf() {
        return self;
    }

    public static Storage getStorage() {
        return storage;
    }

    public static String getImgurClientId() {
        return imgurClientId;
    }

    public static String getImgurClientSecret() { return imgurClientSecret; }

    public static SpigotAPIClient getSpigotAPI() {
        return spigotAPIClient;
    }

    public static List<SongodaPurchase> getSongodaPurchases() {
        return songodaPurchases;
    }

    public static ModulesManager getModulesManager() {
        return modulesManager;
    }

    public static ReminderManager getRemindersManager() {
        return remindersManager;
    }

    public Query<Role> getRoles(String... names) {
        List<Role> roles = Arrays.stream(names).flatMap(name -> guild.getRolesByName(name, true).stream()).collect(Collectors.toList());

        return new Query<>(roles);
    }

    public ChannelQuery getChannels(String... names) {
        List<TextChannel> channels = Arrays.stream(names).flatMap(name -> guild.getTextChannelsByName(name, true).stream()).collect(Collectors.toList());
        return new ChannelQuery(channels);
    }

    public TextChannel getChannel(String id) {
        return guild.getTextChannelById(id);
    }

    public Query<Category> getCategories(String... names) {
        List<Category> channels = Arrays.stream(names).flatMap(name -> guild.getCategoriesByName(name, true).stream()).collect(Collectors.toList());
        return new Query<>(channels);
    }

    public Query<Member> getMembers(String... names) {
        List<Member> channels = Arrays.stream(names).flatMap(name -> guild.getMembersByName(name, true).stream()).collect(Collectors.toList());
        return new Query<>(channels);
    }

    public Member getMember(String id) {
        return guild.getMemberById(id);
    }

    public Query<Emote> getEmotes(String... names) {
        List<Emote> emotes = Arrays.stream(names).flatMap(name -> guild.getEmotesByName(name, true).stream()).collect(Collectors.toList());

        return new Query<>(emotes);
    }

    public static Member getMemberFromString(Message msg, String s) {
        if (msg.getMentionedMembers().size() > 0) {
            return msg.getMentionedMembers().get(0);
        } else if (getGuild().getMembers().stream().anyMatch(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(s) || mem.getUser().getId().equalsIgnoreCase(s))) {
            return getGuild().getMembers().stream().filter(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(s) || mem.getUser().getId().equalsIgnoreCase(s)).findFirst().orElse(null);
        }
        return null;
    }

    public static boolean isStaff(Member member) {
        return member.getRoles().stream().anyMatch(r -> r.getName().contains("Supporter") || r.getName().contains("Staff"));
    }

    public static void log(String prefix, String message) {
        System.out.println(prefix + " " + ConsoleColor.RESET + message + ConsoleColor.RESET);
    }

    public static void log(String message) {
        System.out.println(ConsoleColor.PURPLE_BRIGHT + "[" + ConsoleColor.WHITE_BOLD_BRIGHT + "TechPluginSupport" + ConsoleColor.PURPLE_BRIGHT + "] " + ConsoleColor.RESET + message + ConsoleColor.RESET);
    }
}