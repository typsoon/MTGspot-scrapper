package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.records.CardData;

public interface Card {
    CardData getCardData();
    String getLocalImageAddress();
    void setPrevPrice(double price);
    void setActPrice();
}
