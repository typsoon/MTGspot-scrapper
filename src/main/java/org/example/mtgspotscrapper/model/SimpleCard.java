package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.viewmodel.Card;

import java.sql.Connection;

public class SimpleCard implements Card {
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Connection connection;

    private final CardData cardData;
    private final String localImageAddress;

    public SimpleCard(Connection connection, CardData cardData, String localImageAddress) {
        this.connection = connection;
        this.cardData = cardData;
        this.localImageAddress = localImageAddress;
    }

    @Override
    public CardData getCardData() {
        return cardData;
    }

    @Override
    public String getLocalImageAddress() {
        return localImageAddress;
    }

    @Override
    public void setPrevPrice(double price) {

    }

    @Override
    public void setActPrice() {

    }

    @Override
    public String toString() {
        return cardData.toString() + " " + localImageAddress;
    }
}
