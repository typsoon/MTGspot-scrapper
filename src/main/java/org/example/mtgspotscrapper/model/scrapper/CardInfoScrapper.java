package org.example.mtgspotscrapper.model.scrapper;

import java.util.concurrent.CompletableFuture;

public interface CardInfoScrapper {
    CompletableFuture<Double> getCardPrice(String cardName);
}
