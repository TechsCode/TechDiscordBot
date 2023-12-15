package me.techscode.techdiscordbot.utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonReader {

	/**
	 * An util that allows you to read JSON Objects
	 *
	 * @param rd The reader
	 * @return String
	 * @throws IOException Possible IOException
	 */
	private static @NotNull String readAll(@NotNull final Reader rd) throws IOException {
		final StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * An util that allows you to read JSON Objects
	 *
	 * @param url The URL
	 * @return JSONObject
	 * @throws IOException   Possible IOException
	 * @throws JSONException Possible JSONException
	 */
	public static @NotNull JSONArray readJsonFromUrl(final String url) throws IOException, JSONException {
		try (InputStream inputStream = new URL(url).openStream()) {
			final BufferedReader rd = new BufferedReader(
					new InputStreamReader(
							inputStream, StandardCharsets.UTF_8
					)
			);

			final String jsonText = readAll(rd);
			return new JSONArray(jsonText);
		}
	}

	public static JSONObject makePostRequest(final String url) throws Exception {
		final URL obj = new URL(url);
		final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Set request method to POST
		con.setRequestMethod("POST");

		// Add request header
		con.setRequestProperty("Content-Type", "application/json");

		// Send post request
		con.setDoOutput(true);
		final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.flush();
		wr.close();

		final int responseCode = con.getResponseCode();
		System.out.println("Response Code: " + responseCode);

		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		final StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new JSONObject(response.toString());
	}

	public static JSONObject makePolymartPostRequest(final String url, final String apiKey) throws Exception {
		final URL obj = new URL(url);
		final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Set request method to POST
		con.setRequestMethod("POST");

		// Add request header
		con.setRequestProperty("Content-Type", "application/json");

		// Send post request
		con.setDoOutput(true);
		final DataOutputStream wr = new DataOutputStream(con.getOutputStream());

		// Add API key to request body as JSON data
		final JSONObject postData = new JSONObject();
		postData.put("api_key", apiKey);
		wr.writeBytes(postData.toString());
		wr.flush();
		wr.close();

		final int responseCode = con.getResponseCode();
		System.out.println("Response Code: " + responseCode);

		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		final StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return new JSONObject(response.toString());
	}
}
