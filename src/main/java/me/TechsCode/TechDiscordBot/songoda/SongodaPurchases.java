package me.TechsCode.TechDiscordBot.songoda;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SongodaPurchases {

    public static List<SongodaPurchase> getPurchases() {
        Gson gson = new Gson();

        try {
            return gson.fromJson(new FileReader("songodaPurchases.json"), SongodaGson.class).getData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
