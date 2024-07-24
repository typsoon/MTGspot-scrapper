package org.example.mtgspotscrapper.model.records;

public record CardData(Integer multiverseId, String cardName, double prevPrice, double actPrice, String imageUrl) {
}
