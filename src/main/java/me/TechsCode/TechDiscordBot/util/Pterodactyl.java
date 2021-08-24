package me.TechsCode.TechDiscordBot.util;

import com.google.gson.JsonObject;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

public class Pterodactyl {

	public static boolean doRestart() {
		try {
			URL url = new URL(Config.getInstance().getPteroUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer "+Config.getInstance().getPteroToken());
			con.setDoOutput(true);

			JsonObject json = new JsonObject();
			json.addProperty("signal", "restart");
			String jsonInputString = json.toString();

			try(OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}catch (Exception e){
				e.printStackTrace();
			}

			int responseCode = con.getResponseCode();
			con.disconnect();

			return responseCode == 204;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean doKill() {
		try {
			URL url = new URL(Config.getInstance().getPteroUrl());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + Config.getInstance().getPteroToken());
			con.setDoOutput(true);

			JsonObject json = new JsonObject();
			json.addProperty("signal", "kill");
			String jsonInputString = json.toString();

			try(OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}catch (Exception e){
				e.printStackTrace();
			}

			int responseCode = con.getResponseCode();
			con.disconnect();

			return responseCode == 204;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
