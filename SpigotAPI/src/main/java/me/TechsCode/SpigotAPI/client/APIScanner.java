package me.TechsCode.SpigotAPI.client;

import me.TechsCode.SpigotAPI.client.objects.*;
import me.TechsCode.SpigotAPI.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class APIScanner {

    private SpigotAPIClient client;
    private String url;
    private String token;

    private APIStatus status;

    public APIScanner(SpigotAPIClient client, String url, String token) {
        this.client = client;
        this.url = url;
        this.token = token;
        this.status = APIStatus.WAITING;
    }

    public Data retrieveData() {
        JSONObject data;

        try {
            JSONParser parser = new JSONParser();
            String json = IOUtils.toString(new URI(url + "/?token=" + token), StandardCharsets.UTF_8);
            JSONObject root = (JSONObject) parser.parse(json);

            String status = (String) root.get("status");
            if(!status.equalsIgnoreCase("success")){
                Logger.log("API returned error message:");
                System.out.println(root.get("message"));
                this.status = APIStatus.WAITING;
                return null;
            }
            data = (JSONObject) root.get("data");
        } catch (Exception e) {
            Logger.log("Could not reach SpigotAPI on " + url);
            this.status = APIStatus.OFF;
            return null;
        }

        this.status = APIStatus.OK;

        Resource[] resources = getChilds(data.get("resources")).map(item -> new Resource(client, item)).toArray(Resource[]::new);
        Purchase[] purchases = getChilds(data.get("purchases")).map(item -> new Purchase(client, item)).toArray(Purchase[]::new);
        Review[] reviews = getChilds(data.get("reviews")).map(item -> new Review(client, item)).toArray(Review[]::new);
        Update[] updates = getChilds(data.get("updates")).map(item -> new Update(client, item)).toArray(Update[]::new);

        return new Data(System.currentTimeMillis(), resources, purchases, reviews, updates);
    }

    private Stream<JSONObject> getChilds(Object object) {
        JSONArray jsonArray = (JSONArray) object;
        return IntStream.rangeClosed(0, jsonArray.size() - 1).mapToObj(i -> (JSONObject) jsonArray.get(i)).collect(Collectors.toList()).stream();
    }

    public APIStatus getStatus() {
        return status;
    }

    public enum APIStatus {
        OK("Online", "The api is currently running with no issues!"),
        WAITING("Gathering Info", "The api is currently gathering information. This usually takes around 5-10 mins."),
        OFF("Offline", "The api is offline and it cannot even fetch the url!");

        private String name, description;

        APIStatus(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
