package me.TechsCode.TechDiscordBot.client;

import me.TechsCode.SpigotAPI.client.APIScanner;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.storage.SongodaPurchase;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class SongodaAPIClient extends APIClient {

    private List<SongodaPurchase> purchases;

    private APIScanner.APIStatus status;
    private APIScanner.APIStatus cacheStatus;

    public SongodaAPIClient(String token) {
        super(token);
        status = APIScanner.APIStatus.OFF;
        cacheStatus = APIScanner.APIStatus.OFF;
        purchases = null;
    }

    public boolean isLoaded() {
        return purchases != null;
    }

    public List<SongodaPurchase> getPurchases() {
        return purchases;
    }

    public APIScanner.APIStatus getStatus() {
        return status;
    }

    public APIScanner.APIStatus getCacheStatus() {
        return cacheStatus;
    }

    public List<SongodaPurchase> getPurchases(String discord) {
        return getPurchases().stream().filter(sp -> sp.getDiscord() != null && sp.getDiscord().equals(discord)).collect(Collectors.toList());
    }

    public List<SongodaPurchase> getPurchases(User member) {
        return getPurchases().stream().filter(sp -> sp.getDiscord() != null && sp.getDiscord().equals(member.getName() + "#" + member.getDiscriminator())).collect(Collectors.toList());
    }

    @Override
    public void run() {
        TechDiscordBot.log("Connecting to Songoda.com");
        while (true) {
            try {
                URI url = new URI("https://songoda.com/api/dashboard/payments?token=" + getToken() + "&per_page=2000000");
                HttpURLConnection httpcon = (HttpURLConnection) url.toURL().openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
                httpcon.connect();
                JSONParser parser = new JSONParser();
                JSONObject root = (JSONObject) parser.parse(new InputStreamReader((InputStream) httpcon.getContent()));
                JSONArray data = (JSONArray) root.get("data");
                purchases = (List<SongodaPurchase>) data.stream().map(x -> new SongodaPurchase((String) ((JSONObject) x).get("product"), (String) ((JSONObject) x).get("discord"))).collect(Collectors.toList());
                status = APIScanner.APIStatus.OK;
                httpcon.disconnect();
            } catch (IOException | URISyntaxException | ParseException e) {
                //e.printStackTrace();
                status = APIScanner.APIStatus.OFF;
            }
            if(isLoaded()) {
                cacheStatus = APIScanner.APIStatus.OK;
            } else {
                cacheStatus = APIScanner.APIStatus.OFF;
            }
            try {
                sleep(50000L);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }

        }
    }
}