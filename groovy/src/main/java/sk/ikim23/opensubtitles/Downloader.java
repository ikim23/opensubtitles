package sk.ikim23.opensubtitles;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class Downloader {
    public static void downloadAndSave(String link, String charset, File saveFile) throws MalformedURLException {
        URL url = new URL(link);
        try (InputStreamReader reader = new InputStreamReader(new GZIPInputStream(url.openStream()), charset);
             OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(saveFile), "UTF-8")) {
            char[] buffer = new char[4096];
            int length = 0;
            while ((length = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
