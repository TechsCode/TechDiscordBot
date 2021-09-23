package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlWhitelistModule extends Module {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    private final DefinedQuery<Category> IGNORED_CATEGORIES = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("\uD83D\uDCC1 | Archives", "\uD83D\uDCD1 | Staff Logs", "Other Staff Discussions", "staff discussions", "⚖ | Leadership-Discussions", "\uD83C\uDFAB ︱Tickets");
        }
    };

    private final List<String> WHITELISTED_URLS = new ArrayList<>();
    private final String URL = "https://raw.githubusercontent.com/TechsCode-Team/TechBot-Whitelists/main/urlWhitelist.txt";

    public UrlWhitelistModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            while (true) {
                getWhitelist();

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(10));
                } catch (Exception ignored) { }
            }
        }).start();
    }

    @Override
    public void onDisable() { }

    public String getName() {
        return "UrlWhitelistModule";
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if (e.getMember() == null || e.getAuthor().isBot() || e.getMember().getRoles().contains(STAFF_ROLE.query().first()) || IGNORED_CATEGORIES.query().stream().anyMatch(c -> c.getId().equals(e.getChannel().getParent().getId()))) return;

        String message = e.getMessage().getContentRaw();

        if (!WHITELISTED_URLS.isEmpty()) {
            Set<String> extractedUrls = extractUrls(message);
            boolean blockMessage = extractedUrls.stream().anyMatch(extractedUrl -> !WHITELISTED_URLS.contains(extractedUrl));

            if (blockMessage) {
                e.getMessage().delete().queue();
                new TechEmbedBuilder("Blocked URL(s)")
                        .color(Color.RED)
                        .text("Your message contained a URL which is not in our whitelist.\n\nIf you think this is a mistake, take a look at our [**link whitelist**](" + URL + ").")
                        .sendTemporary(e.getChannel(), 10, TimeUnit.SECONDS);
            }
        }
    }

    private void getWhitelist() {
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

                    if(!WHITELISTED_URLS.contains(word))
                        WHITELISTED_URLS.add(word);
                }

                in.close();
            } else {
                TechDiscordBot.log("ERROR", "Failed to fetch the white listed urls.");
            }

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> extractUrls(String text) {
        Set<String> containedUrls = new LinkedHashSet<>();

        String domain = "";
        boolean successfulParse = false;

        String regex = "(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher m = p.matcher(text);

        while (m.find()) {
            String regexResponse = m.group(0);

            try {
                URL url = new URL(regexResponse);
                String[] domainExploded = url.getHost().split("\\.");
                domain = domainExploded[domainExploded.length - 2] + "." + domainExploded[domainExploded.length - 1];
                successfulParse = true;
            } catch (Exception ignored) {}

            if(successfulParse)
                containedUrls.add(domain);
        }

        return containedUrls;
    }

    public Requirement[] getRequirements() {
        return new Requirement[] {
                new Requirement(IGNORED_CATEGORIES, IGNORED_CATEGORIES.query().amount(), "Could not find all of the ignored category channels.")
        };
    }
}
