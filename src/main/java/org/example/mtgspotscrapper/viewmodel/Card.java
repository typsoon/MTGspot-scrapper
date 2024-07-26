package org.example.mtgspotscrapper.viewmodel;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.CardPrice;

import java.sql.SQLException;

public interface Card {
    CardData getCardData();
    String getLocalImageAddress();
    CardPrice getCardPrice() throws SQLException;

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    boolean updatePrice() throws SQLException;
    Availability getAvailability() throws SQLException;
}
