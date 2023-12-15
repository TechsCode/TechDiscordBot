package me.techscode.techdiscordbot.model;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Pastebin {

    public static String post(final String text, final boolean raw) throws IOException {
        final byte[] postData = text.getBytes(StandardCharsets.UTF_8);
        final int postDataLength = postData.length;

        final String requestURL = "https://paste.techscode.com/documents";
        final URL url = new URL(requestURL);
        final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Hastebin Java Api");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);

        String response = null;
        final DataOutputStream wr;
        try {
            wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = reader.readLine();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        if (response.contains("\"key\"")) {
            response = response.substring(response.indexOf(":") + 2, response.length() - 2);

            final String postURL = raw ? "https://paste.techscode.com/raw/" : "https://paste.techscode.com/";
            response = postURL + response;
        }

        return response;
    }
}
