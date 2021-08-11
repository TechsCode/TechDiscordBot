package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
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
    List<String> whitelistedUrls = new ArrayList<String>();

    public UrlWhitelistModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            while (true) {
                getWhitelist();

                try {
                    Thread.sleep(TimeUnit.HOURS.toMillis(1)); //Wait every hour
                } catch (Exception ignored) {}
            }
        }).start();
    }

    @Override
    public void onDisable() {}

    public String getName() {
        return "UrlWhitelistModule";
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if (e.getMember() == null) return;
        if (e.getAuthor().isBot()) return;
        //if (e.getMember().getRoles().contains(STAFF_ROLE.query().first())) return;

        String message = e.getMessage().getContentRaw();
        boolean blockMessage = false;

        if(!whitelistedUrls.isEmpty()){
            Set<String> extractedUrls = extractUrls(message);
            for (String extractedUrl : extractedUrls) {
                if (!whitelistedUrls.contains(extractedUrl)) {
                    blockMessage = true;
                    break;
                }
            }

            if(blockMessage){
                e.getMessage().delete().queue();
                new TechEmbedBuilder("Blocked url(s)")
                        .color(Color.RED)
                        .text("Your message contained a link which is not in our whitelist.\nTo add it to our whitelist please do `/urlwhitelist` to view how.")
                        .queue(e.getChannel());
            }
        }
    }

    private void getWhitelist(){
        try{
            URL url = new URL("https://raw.githubusercontent.com/TechsCode-Team/UrlWhitelist/main/urls.txt");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if(status == 200){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    whitelistedUrls.add(inputLine.trim());
                }
                in.close();
            }else{
                System.err.println("Error getting url whitelist list");
            }
            con.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Set<String> extractUrls(String text) {
        Set<String> containedUrls = new LinkedHashSet<>();
        String urlRegex = "(^|\\s)((https?:\\/\\/)?[\\w-]+(\\.[\\w-]+)+\\.?(:\\d+)?(\\/\\S*)?)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                            urlMatcher.end(0))
                    .replaceAll("www.", "")
                    .replaceAll("https://", "")
                    .replaceAll("http://", "")
                    .replaceAll("\\/.*", "").trim());
        }

        return containedUrls;
    }

    public Requirement[] getRequirements() {
        return new Requirement[0];
    }
}
