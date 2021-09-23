package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class WordBlacklistModule extends Module {

    private final DefinedQuery<Category> IGNORED_CATEGORIES = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("\uD83D\uDCC1 | Archives", "\uD83D\uDCD1 | Staff Logs", "Other Staff Discussions", "staff discussions", "⚖ | Leadership-Discussions");
        }
    };

    private final List<String> BLACKLISTED_WORDS = new ArrayList<>();
    private final String URL = "https://raw.githubusercontent.com/TechsCode-Team/TechBot-Whitelists/main/wordBlacklist";

    public WordBlacklistModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            while (true) {
                getBlacklist();

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(10));
                } catch (Exception ignored) { }
            }
        }).start();
    }

    @Override
    public void onDisable() { }

    public String getName() {
        return "WordBlacklistModule";
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if (e.getMember() == null || e.getAuthor().isBot() || IGNORED_CATEGORIES.query().stream().anyMatch(c -> c.getId().equals(e.getChannel().getParent().getId()))) return;

        String message = e.getMessage().getContentRaw().toLowerCase();
        String blacklist = String.join("|", BLACKLISTED_WORDS);

        String regex = "[^!@#$%^&*]*(" + blacklist + ")[^!@#$%^&*]*";
        boolean blockMessage= Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(message).matches();

        if (blockMessage) {
            e.getMessage().delete().queue();
            new TechEmbedBuilder("Blocked Word(s)")
                    .color(Color.RED)
                    .text("Your message contained a world which is in our blacklist.\n\nIf you think this is a mistake, take a look at our [**word blacklist**](" + URL + ").")
                    .sendTemporary(e.getChannel(), 10, TimeUnit.SECONDS);
        }
    }

    private void getBlacklist() {
        try {
            URL url = new URL(URL);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();

            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    String word = inputLine.trim().toLowerCase();

                    if(!BLACKLISTED_WORDS.contains(word))
                        BLACKLISTED_WORDS.add(word);
                }

                in.close();
            } else {
                TechDiscordBot.log("ERROR", "Failed to fetch the black listed words.");
            }

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Requirement[] getRequirements() {
        return new Requirement[] {
                new Requirement(IGNORED_CATEGORIES, IGNORED_CATEGORIES.query().amount(), "Could not find all of the ignored category channels.")
        };
    }
}
