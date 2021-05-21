package me.TechsCode.TechDiscordBot;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.TechDiscordBot.module.ModulesManager;
import me.TechsCode.TechDiscordBot.mysql.MySQLSettings;
import me.TechsCode.TechDiscordBot.mysql.storage.Storage;
import me.TechsCode.TechDiscordBot.objects.ChannelQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.reminders.ReminderManager;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchase;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchases;
import me.TechsCode.TechDiscordBot.spigotmc.api.APIStatus;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
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

    private static String githubToken;

    private static ModulesManager modulesManager;
    private static ReminderManager remindersManager;

    public static void main(String[] args) {
        if (args.length != 8) {
            log(ConsoleColor.RED + "Invalid start arguments. Consider using:");
            log(ConsoleColor.WHITE_BOLD_BRIGHT + "java -jar TechPluginSupportBot.jar <Discord Bot Token> <Tech API Token> <MySQL Host> <MySQL Port> <MySQL Database> <MySQL Username> <MySQL Password> <Github Token>");
            return;
        }

        try {
            new TechDiscordBot(args[0], args[1], MySQLSettings.of(args[2], args[3], args[4], args[5], args[6]), args[7]);
            //Sentry.init(options -> options.setDsn("https://21ba29cc18984aa7a02598fb8e94d05c@o565371.ingest.sentry.io/5734422"));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            //Sentry.captureException(e);
        }
    }

    public TechDiscordBot(String token, String apiToken, MySQLSettings mySQLSettings, String githubToken) throws LoginException, InterruptedException {
        i = this;

        jda = JDABuilder.createDefault(token)
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.DEFAULT | GatewayIntent.GUILD_MEMBERS.getRawValue() | GatewayIntent.GUILD_BANS.getRawValue()))
                .setDisabledIntents(GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_TYPING)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setActivity(Activity.watching("for help."))
                .setEventManager(new AnnotatedEventManager())
                .build().awaitReady();

        List<Guild> guilds = jda.getGuilds();

        if(guilds.size() > 2) {
            log(ConsoleColor.RED + "The bot is a member of too many guilds. Please leave them all except two!");
            return;
        }

        guild = guilds.size() != 0 ? jda.getGuildById("311178000026566658") : null;
        self = guild != null ? guild.getSelfMember() : null;

        if(guild == null) {
            log(ConsoleColor.RED + "The bot is not a member of any guild. Please join a guild!");
            return;
        }

        TechDiscordBot.githubToken = githubToken;

        spigotAPIClient = new SpigotAPIClient("http://api.techscode.de/", apiToken);
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

        jda.addEventListener(new Object[] { modulesManager });
        jda.addEventListener(new Object[] { remindersManager });

        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.OFF);

        log("Successfully loaded the bot and logged into " + guild.getName() + " as " + self.getEffectiveName() + "!");

        log("");

        log("Spigot:");
        if(!getStatus().isUsable())
            log("  » " + ConsoleColor.RED + "API is not usable!");

        log("  » Purchases: " + getSpigotAPI().getPurchases().size());
        log("  » Resources: " + getSpigotAPI().getResources().size());
        log("  » Updates: " + getSpigotAPI().getUpdates().size());
        log("  » Reviews: " + getSpigotAPI().getReviews().size());
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

    public static String getGithubToken() {
        return githubToken;
    }

    public APIStatus getStatus() {
        return APIStatus.getStatus(getSpigotAPI());
    }
}
