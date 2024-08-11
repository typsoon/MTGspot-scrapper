package org.example.mtgspotscrapper.viewmodel;

import org.example.mtgspotscrapper.model.cardImpl.Availability;
import org.example.mtgspotscrapper.model.cardImpl.CardData;
import org.example.mtgspotscrapper.model.cardImpl.CardPrice;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface Card {
    CardData getCardData();
    CompletableFuture<String> getDownloadedImageAddress() throws ExecutionException, InterruptedException;
    CardPrice getActCardPrice();
    CompletableFuture<CardPrice> getFutureCardPrice() throws SQLException;

    @SuppressWarnings({"UnusedReturnValue"})
    void updatePrice();
    Availability getAvailability() throws SQLException;
}
