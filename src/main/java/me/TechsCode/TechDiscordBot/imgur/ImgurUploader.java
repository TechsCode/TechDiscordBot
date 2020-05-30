package me.TechsCode.TechDiscordBot.imgur;

import com.eazyftw.imgurapi.ImgurApiClient;
import com.eazyftw.imgurapi.model.Image;
import com.eazyftw.imgurapi.util.ImgurApiException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.spigotmc.SpigotMC;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImgurUploader {

    public static String upload(String url) {
        String base64 = getBase64EncodedImage(url);
        try {
            ImgurApiClient client = new ImgurApiClient.Builder().clientAuth(TechDiscordBot.getImgurClientId(), TechDiscordBot.getImgurClientSecret()).build();
            Image img = client.imageService().uploadBase64Image(base64, null, null, null);
            return img.getLink();
        } catch (ImgurApiException ignored) {}
        return null;
    }

    public static String getBase64EncodedImage(String url) {
        try {
            InputStream is = SpigotMC.getBrowser().request2(url, HttpMethod.GET).getWebResponse().getContentAsStream();
            byte[] bytes = IOUtils.toByteArray(is);
            return Base64.encodeBase64String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            imageByte = Base64.decodeBase64(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static File bufferedImageToFile(String name, BufferedImage img) {
        File file = new File(name + ".png");
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}