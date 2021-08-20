package me.TechsCode.TechDiscordBot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Config {

    private static Config instance;

    public static Config getInstance(){
        if(instance == null){
            instance = new Config();
        }

        return instance;
    }

    private JsonObject root;

    private Config() {
        File file = new File("config.json");

        if(!file.exists()){
            try {
                InputStream src = Config.class.getResourceAsStream("/config.json");
                Files.copy(src, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            JsonParser jsonParser = new JsonParser();
            root = (JsonObject) jsonParser.parse(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConfigured(){
        return !getToken().equals("") ||
                !getApiToken().equals("") ||
                !getSongodaApiToken().equals("") ||
                !getMySqlHost().equals("") ||
                !getMySqlPort().equals("") ||
                !getMySqlDatabase().equals("") ||
                !getMySqlUsername().equals("") ||
                !getMySqlPassword().equals("") ||
                !getGithubToken().equals("");
    }

    public String getToken(){
        return root.get("token").getAsString();
    }

    public String getApiToken(){
        return root.get("apiToken").getAsString();
    }

    public String getSongodaApiToken(){
        return root.get("songodaApiToken").getAsString();
    }

    public String getMySqlHost(){
        return root.get("mySQL_host").getAsString();
    }

    public String getMySqlPort(){
        return root.get("mySQL_port").getAsString();
    }

    public String getMySqlDatabase(){
        return root.get("mySQL_database").getAsString();
    }

    public String getMySqlUsername(){
        return root.get("mySQL_username").getAsString();
    }

    public String getMySqlPassword(){
        return root.get("mySQL_password").getAsString();
    }

    public String getGithubToken(){
        return root.get("githubToken").getAsString();
    }
}
