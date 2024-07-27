package org.example.mtgspotscrapper.viewmodel;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.CardPrice;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface Card {
    CardData getCardData();
    CompletableFuture<String> getDownloadedImageAddress() throws ExecutionException, InterruptedException;
    CardPrice getActCardPrice() throws SQLException;
    CompletableFuture<CardPrice> getFutureCardPrice() throws SQLException;

    @SuppressWarnings({"UnusedReturnValue"})
    void updatePrice() throws SQLException;
    Availability getAvailability() throws SQLException;
}
