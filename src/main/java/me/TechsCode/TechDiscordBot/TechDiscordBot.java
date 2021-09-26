package me.TechsCode.TechDiscordBot;

import me.TechsCode.TechDiscordBot.module.ModulesManager;
import me.TechsCode.TechDiscordBot.mysql.MySQLSettings;
import me.TechsCode.TechDiscordBot.mysql.storage.Storage;
import me.TechsCode.TechDiscordBot.objects.ChannelQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.reminders.ReminderManager;
import me.TechsCode.TechDiscordBot.songoda.SongodaAPIClient;
import me.TechsCode.TechDiscordBot.spigotmc.SpigotApi;
import me.TechsCode.TechDiscordBot.spigotmc.data.APIStatus;
import me.TechsCode.TechDiscordBot.util.Config;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;
import me.TechsCode.TechDiscordBot.util.PterodactylAPI;
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

    private static SpigotApi spigotAPI;
    private static SongodaAPIClient songodaAPIClient;
//    private static List<SongodaPurchase> songodaPurchases;

    private static Storage storage;

    private static String githubToken;

    private static ModulesManager modulesManager;
    private static ReminderManager remindersManager;

    private static PterodactylAPI pterodactylAPI;

    public static void main(String[] args) {
        if (!Config.getInstance().isConfigured()) {
            log(ConsoleColor.RED + "Invalid config file. Please enter the information in config.json");
            return;
        }

        try {
            new TechDiscordBot(Config.getInstance().getToken(), Config.getInstance().getApiToken(), Config.getInstance().getSongodaApiToken(), MySQLSettings.of(Config.getInstance().getMySqlHost(), Config.getInstance().getMySqlPort(), Config.getInstance().getMySqlDatabase(), Config.getInstance().getMySqlUsername(), Config.getInstance().getMySqlPassword()), Config.getInstance().getGithubToken(), Config.getInstance().getPteroUrl(), Config.getInstance().getPteroClientToken(), Config.getInstance().getPteroApiToken(), Config.getInstance().getSpigotApiUrl());
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TechDiscordBot(String token, String apiToken, String songodaApiToken, MySQLSettings mySQLSettings, String githubToken, String pteroUrl, String pteroClientToken, String pteroApiToken, String spigotApiUrl) throws LoginException, InterruptedException {
        i = this;

        jda = JDABuilder.createDefault(token)
                .setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.DEFAULT | GatewayIntent.GUILD_MEMBERS.getRawValue() | GatewayIntent.GUILD_BANS.getRawValue()))
                .setDisabledIntents(GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGE_TYPING)
                .enableCache(CacheFlag.ONLINE_STATUS)
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

        if(guilds.size() == 0) {
            log(ConsoleColor.RED + "The bot is not a member of any guild. Please join a guild!");
            return;
        }

        guild = jda.getGuildById("311178000026566658");
        self = guild != null ? guild.getSelfMember() : null;

        if(guild == null) {
            log(ConsoleColor.RED + "The bot is not in the right guild!");
            return;
        }

        TechDiscordBot.githubToken = githubToken;

        spigotAPI = new SpigotApi(spigotApiUrl, apiToken);
        songodaAPIClient = new SongodaAPIClient(songodaApiToken);

        log("Initializing MySQL Storage " + mySQLSettings.getHost() + ":" + mySQLSettings.getPort() + "!");
        storage = Storage.of(mySQLSettings);

        if(!storage.isConnected()) {
            log(ConsoleColor.RED + "Failed to connect to MySQL!");
            log(storage.getLatestErrorMessage());
        }

        pterodactylAPI = new PterodactylAPI();
        pterodactylAPI.setup(pteroUrl, pteroClientToken, pteroApiToken);

        modulesManager = new ModulesManager();
        log("Loading modules..");
        modulesManager.load();

        remindersManager = new ReminderManager();
        log("Loading reminders..");
        remindersManager.load();

        jda.addEventListener(modulesManager);
        jda.addEventListener(remindersManager);

        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.OFF);

        log("Successfully loaded the bot and logged into " + guild.getName() + " as " + self.getEffectiveName() + "!");

        log("");

        log("Spigot:");
        if(!getSpigotStatus().isUsable()) {
            log("  > " + ConsoleColor.RED + "API is not usable!");
        }else{
            log("  > Purchases: " + getSpigotAPI().getSpigotPurchases().size());
            log("  > Resources: " + getSpigotAPI().getSpigotResources().size());
            log("  > Updates: " + getSpigotAPI().getSpigotUpdates().size());
            log("  > Reviews: " + getSpigotAPI().getSpigotReviews().size());
        }
        log("");

        log("Market:");
        if(!getMarketStatus().isUsable()) {
            log("  > " + ConsoleColor.RED + "API is not usable!");
        }else{
            log("  > Purchases: " + getSpigotAPI().getMarketPurchases().size());
            log("  > Resources: " + getSpigotAPI().getMarketResources().size());
            log("  > Updates: " + getSpigotAPI().getMarketUpdates().size());
            log("  > Reviews: " + getSpigotAPI().getMarketReviews().size());
        }
        log("");

        log("Songoda: ");
        if(getSongodaAPI().isLoaded()) {
            log("  > Purchases: " + getSongodaAPI().getSpigotPurchases().size());
        } else {
            log("  > " + ConsoleColor.RED + "Could not connect. Cannot show info!");
        }

        log("");

        log("Guild:");
        log("  > Members: " + getGuild().getMembers().size());
        log("  > Verified Members: " + getStorage().retrieveVerifications().stream().filter(v -> guild.getMemberById(v.getDiscordId()) != null).count());
        log("  > Review Squad Members: " + getGuild().getMembers().stream().filter(member -> member.getRoles().stream().anyMatch(role -> role.getName().equals("Review Squad"))).count());
        log("  > Donators: " + getGuild().getMembers().stream().filter(member -> member.getRoles().stream().anyMatch(role -> role.getName().contains("Donator"))).count());

        log("");

        getModulesManager().logLoad();

        log("");
        log("Startup Completed! The bot has successfully started!");
        log("The bot is ready");
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

    public static SpigotApi getSpigotAPI() {
        return spigotAPI;
    }

    public static PterodactylAPI getPterodactylAPI(){return pterodactylAPI;};

    public static SongodaAPIClient getSongodaAPI() {
        return songodaAPIClient;
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
        System.out.println("[TechPluginSupport] " + message + ConsoleColor.RESET);
    }

    public static String getGithubToken() {
        return githubToken;
    }

    public static APIStatus getSpigotStatus() {
        return APIStatus.getSpigotStatus(spigotAPI.getSpigotAPIManager());
    }

    public static APIStatus getMarketStatus() {
        return APIStatus.getMarketStatus(spigotAPI.getSpigotAPIManager());
    }

    public static APIStatus getSongodaStatus() {
        return APIStatus.getStatus(getSongodaAPI());
    }
}
