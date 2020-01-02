package me.TechsCode.TechDiscordBot;

import me.TechsCode.TechDiscordBot.objects.ChannelQuery;
import me.TechsCode.TechDiscordBot.songoda.SongodaAPIClient;
import me.TechsCode.TechDiscordBot.storage.Storage;
import me.TechsCode.TechDiscordBot.util.ConsoleColor;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.Project;
import me.TechsCode.TechsCodeAPICli.TechsCodeAPIClient;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TechDiscordBot extends ListenerAdapter implements EventListener {

    private final String TECHSCODEAPI = "api.techsco.de";

    private static TechDiscordBot i;

    private static JDA jda;
    private Guild guild;
    private Member self;

    private TechsCodeAPIClient techsCodeAPIClient;
    private SongodaAPIClient songodaAPIClient;
    private Storage storage;

    private List<Module> modules;
    private List<CommandModule> cmdModules;

    public static void main(String[] args) {
        if (args.length != 8) {
            System.out.println("Invalid start arguments. Consider using:");
            System.out.println("java -jar TechPluginSupportBot.jar <Discord Bot Token> <Tech API Token> <Songoda Token> <MySQL Host> <MySQL Port> <MySQL Database> <MySQL Username> <MySQL Password>");
            return;
        }
        new TechDiscordBot(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
    }

    public TechDiscordBot(String token, String apiToken, String songodaToken, String mysqlHost, String mysqlPort, String mysqlDatabase, String mysqlUsername, String mysqlPassword) {
        try {
            i = this;
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

        if (guild == null) {
            log(ConsoleColor.RED + "The Bot is not a member of a guild");
            return;
        }

        if (guilds.size() > 1) {
            log(ConsoleColor.RED + "The Bot is a member of too many guilds.");
            return;
        }

        log("Successfully logged in as " + self.getEffectiveName() + " into " + guild.getName());

        log("Connecting to " + TECHSCODEAPI + "..");
        this.techsCodeAPIClient = new TechsCodeAPIClient(TECHSCODEAPI, apiToken);

        log("Connecting to Songoda.com");
        this.songodaAPIClient = new SongodaAPIClient(songodaToken);

        log("Initializing MySQL Storage " + mysqlHost + ":" + mysqlPort + "..");
        this.storage = new Storage(mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword);

        if (!storage.isEnabled()) {
            log(ConsoleColor.RED + "Failed connecting to MySQL:");
            log(storage.getErrorMessage());
            return;
        }

        log("Loading modules...");
        modules = new ArrayList<>();
        cmdModules = new ArrayList<>();

        for (Class each : Project.getClasses("me.TechsCode.")) {
            if (CommandModule.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())) {
                try {
                    CommandModule module = (CommandModule)each.getConstructor(TechDiscordBot.class).newInstance(this);
                    module.enable();
                    if (module.isEnabled()) cmdModules.add(module);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else if (Module.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())) {
                try {
                    Module module = (Module) each.getConstructor(TechDiscordBot.class).newInstance(this);
                    module.enable();
                    if (module.isEnabled()) modules.add(module);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        jda.addEventListener(modules.toArray());
        jda.addEventListener(cmdModules.toArray());
        jda.addEventListener(this);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> modules.forEach(Module::onDisable)));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> cmdModules.forEach(CommandModule::onDisable)));

        log("Successfully loaded " + (modules.size() + cmdModules.size()) + " modules!");
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        String first = e.getMessage().getContentDisplay().split(" ")[0];

        CommandModule cmd = cmdModules.stream().filter(cmdM -> cmdM.getCommand().equalsIgnoreCase(first) || (cmdM.getAliases() != null && Arrays.asList(cmdM.getAliases()).contains(first))).findFirst().orElse(null);
        if(cmd == null) return;

        List<Role> restrictedRoles = new ArrayList<>();
        if(cmd.getRestrictedRoles() != null && cmd.getRestrictedRoles().query() != null && cmd.getRestrictedRoles().query().all() != null) restrictedRoles.addAll(cmd.getRestrictedRoles().query().all());
        List<TextChannel> restrictedChannels =  new ArrayList<>();
        if(cmd.getRestrictedChannels() != null && cmd.getRestrictedChannels().query() != null && cmd.getRestrictedChannels().query().all() != null) restrictedChannels.addAll(cmd.getRestrictedChannels().query().all());

        // Check if the player has at least one of the restricted roles
        if(!restrictedRoles.isEmpty() && Collections.disjoint(e.getMember().getRoles(), restrictedRoles)) {
            new CustomEmbedBuilder("No Permissions")
                    .error()
                    .setText("You don't have enough permissions to execute this command!")
                    .sendTemporary(e.getChannel(), 5, TimeUnit.SECONDS);
            return;
        }

        // Check if the message was sent in one of the restricted channels (if there are any)
        if (!restrictedChannels.isEmpty() && !restrictedChannels.contains(e.getChannel())) return;

        String message = e.getMessage().getContentDisplay();
        String[] args = Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length);

        cmd.onCommand(e.getChannel(), e.getMessage(), e.getMember(), args);

        e.getMessage().delete().complete();
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

    public SongodaAPIClient getSongodaAPIClient() {
        return songodaAPIClient;
    }

    public Storage getStorage() {
        return storage;
    }

    public void log(String message) {
        System.out.println(ConsoleColor.BLUE_BRIGHT + "[" + ConsoleColor.WHITE_BOLD_BRIGHT + "Discord Bot" + ConsoleColor.BLUE_BRIGHT + "] " + ConsoleColor.RESET+message);
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

    public static JDA getJDA() {
        return jda;
    }

    public static TechDiscordBot getBot() {
        return i;
    }
}
