package me.TechsCode.TechDiscordBot.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RedirectUtil {

    public static String getRedirectUrl(String url) {
        URL urlTmp;
        String redUrl;
        HttpURLConnection connection;
        try { urlTmp = new URL(url); } catch (MalformedURLException e1) { return url; }
        try { connection = (HttpURLConnection) urlTmp.openConnection(); } catch (IOException e) { return url; }
        try { connection.getResponseCode(); } catch (IOException e) { return url; }
        redUrl = connection.getURL().toString();
        connection.disconnect();
        if(redUrl.equals(url)) {
            try {
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setReadTimeout(5000);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "google.com");
                boolean redirect = false;
                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                }
                if (redirect) {
                    return conn.getHeaderField("Location");
                }
            } catch (Exception e) {}
        } else {
            return redUrl;
        }
        return url;
    }

}
