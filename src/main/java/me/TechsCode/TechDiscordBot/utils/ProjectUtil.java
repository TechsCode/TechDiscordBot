package me.techscode.techdiscordbot.utils;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.debug.Debugger;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProjectUtil {

    @NotNull
    public static String[] getFiles() {
        ArrayList<String> names = new ArrayList<>();
        try {
            CodeSource src = ProjectUtil.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                while(true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null) break;
                    names.add(e.getName());
                }
            }
        } catch (IOException e) {
            Common.throwError(e, "Failed to get files");
        }
        return names.toArray(new String[0]);
    }

    @NotNull
    public static Class<?>[] getClasses(String prefix) {
        return Arrays.stream(getFiles())
                .filter(fileName -> fileName.endsWith(".class"))
                .map(className -> className.replace("/", ".").replace(".class", ""))
                .filter(fileName -> fileName.startsWith(prefix))
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Class[]::new);
    }

    @NotNull
    @Contract(pure = true)
    public static String removeFront(@NotNull String s, int am) {
        return s.substring(am);
    }

    @NotNull
    public static String removeEnd(@NotNull String s, int am) {
        return s.substring(0, s.length() - am);
    }

    @NotNull
    public static String removeBoth(@NotNull String s, int am) {
        return s.substring(am, s.length() - am);
    }

    @Nullable
    public static String getTextFromImage(File imageFile) {
        ITesseract tesseract = new Tesseract();
        try {
            tesseract.setLanguage("eng");
            String temp = tesseract.doOCR(imageFile);
            return temp;
        } catch (TesseractException e) {
            Common.throwError(e, "Failed to read image");
            return null;
        }
    }

    @Nullable
    public static File GrayScale(InputStream inputStream) {

        try {

            // TODO:
            //  Saves tempFile is 0KB meaning there is no image.
            //  This might be an issue with inputStream.read(buffer);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            File tempFile = File.createTempFile("tempImage", ".png");
            OutputStream outStream = new FileOutputStream(tempFile);
            outStream.write(buffer);

            Debugger.debug("Image2", "Image saved to " + tempFile.getAbsolutePath());

            BufferedImage image = ImageIO.read(tempFile);
            int width = image.getWidth();
            int height = image.getHeight();

            for(int i=0; i<height; i++) {

                for(int j=0; j<width; j++) {

                    Color c = new Color(image.getRGB(j, i));
                    int red = (int)(c.getRed() * 0.299);
                    int green = (int)(c.getGreen() * 0.587);
                    int blue = (int)(c.getBlue() *0.114);
                    Color newColor = new Color(red+green+blue,

                            red+green+blue,red+green+blue);

                    image.setRGB(j,i,newColor.getRGB());
                }
            }

            File ouptut = new File("grayscale.jpg");
            ImageIO.write(image, "jpg", ouptut);

            return ouptut;

        } catch (Exception e) {
            Common.throwError(e, "Failed to grayscale image");
        }
        return null;
    }
}