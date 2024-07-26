package org.example.mtgspotscrapper.viewmodel;

import org.example.mtgspotscrapper.model.records.CardData;

public interface Card {
    CardData getCardData();
    String getLocalImageAddress();

    @SuppressWarnings("unused")
    void setPrevPrice(double price);

    @SuppressWarnings("unused")
    void setActPrice();
}
