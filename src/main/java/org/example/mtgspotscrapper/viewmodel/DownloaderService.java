package org.example.mtgspotscrapper.viewmodel;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public interface DownloaderService {
    CompletableFuture<String> downloadCardImage(URL cardURL, int multiverseId) throws IOException;
}
