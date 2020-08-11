package me.TechsCode.TechDiscordBot.github;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import org.apache.commons.io.FileUtils;
import org.kohsuke.github.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GitHubUtil {

    private static GitHub github = null;

//    public Object get(String surl) {
//        try {
//            URL url = new URL(surl);
//            Map<String, Object> params = new LinkedHashMap<>();
//            params.put("text", txt);
//
//            StringBuilder postData = new StringBuilder();
//            for (Map.Entry<String, Object> param : params.entrySet()) {
//                if (postData.length() != 0) postData.append('&');
//                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//                postData.append('=');
//                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//            }
//
//            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
//
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//            conn.setDoOutput(true);
//            conn.getOutputStream().write(postDataBytes);
//
//            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
//        } catch (Exception ex) {
//            return null;
//        }
//    }

    public static GithubRelease getLatestRelease(String repo) {
        try {
            GHRepository ghrepo = getGithub().getRepository("TechsCode/" + repo);
            GHRelease release = ghrepo.getLatestRelease();

            return new GithubRelease(release);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File downloadFile(String tagName, GHAsset asset) {
        InputStream is = null;
        try {
            HttpsURLConnection urlConn = (HttpsURLConnection) asset.getUrl().openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Authorization", "token " + TechDiscordBot.getGithubToken());
            urlConn.setRequestProperty("Accept", "application/octet-stream");
            urlConn.connect();
            is = urlConn.getInputStream();

            FileUtils.copyInputStreamToFile(is, new File("assets", tagName + "_" + asset.getId() + ".jar"));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                assert is != null;
                is.close();
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }

        return new File("assets", tagName + "_" + asset.getId() + ".jar");
    }

    public static GitHub getGithub() {
        try {
            if(github == null) github = new GitHubBuilder().withOAuthToken(TechDiscordBot.getGithubToken()).build();
            return github;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
