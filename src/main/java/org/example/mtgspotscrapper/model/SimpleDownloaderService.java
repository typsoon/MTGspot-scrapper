package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.viewmodel.DownloaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.*;

public class SimpleDownloaderService implements DownloaderService {
    private static final Logger log = LoggerFactory.getLogger(SimpleDownloaderService.class);
//    TODO: learn about shutdown hooks to properly shut this down
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static HttpURLConnection manualRedirect(URL imageURL) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) imageURL.openConnection();
        int responseCode = httpURLConnection.getResponseCode();
        while (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
            String newUrl = httpURLConnection.getHeaderField("Location");
            httpURLConnection.disconnect();
            try {
                imageURL = new URI(newUrl).toURL();
            } catch (Exception e) {
                log.error("Problems on wizards side: {}", Arrays.toString(e.getStackTrace()));
                throw new RuntimeException("Problems on wizards side:", e);
            }
            httpURLConnection = (HttpURLConnection) imageURL.openConnection();
            responseCode = httpURLConnection.getResponseCode();
        }

        return httpURLConnection;
    }

    @Override
    public CompletableFuture<String> downloadCardImage(URL imageURL, int multiverseId) {
        return CompletableFuture.supplyAsync(()-> {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = manualRedirect(imageURL);

                String contentType = httpURLConnection.getContentType();
                String fileExtension = getFileExtension(contentType);
                if (fileExtension == null) {
                    log.error("Unsupported content type: {}", contentType);
                    throw new RuntimeException("Unsupported content type: " + contentType);
                }

                File directory = new File("downloaded images");
                if (!directory.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    directory.mkdirs();
                }

                File file = new File(directory, "card" + multiverseId + "." + fileExtension);

                try (InputStream inputStream = httpURLConnection.getInputStream()) {
                    log.info("Downloading image: {}", file.getAbsolutePath());
                    BufferedImage image = ImageIO.read(inputStream);
                    if (image != null) {
                        ImageIO.write(image, fileExtension, file);

                        log.info("Image just downloaded: {}", file.getAbsolutePath());
                        return file.getAbsolutePath();
                    } else {
                        throw new RuntimeException("Failed to load image from URL: " + imageURL);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
        }, executor);
    }

    private static String getFileExtension(String contentType) {
        if (contentType == null) return null;
        return switch (contentType) {
            case "image/jpeg", "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> null;
        };
    }
}
