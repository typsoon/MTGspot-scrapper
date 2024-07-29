package org.example.mtgspotscrapper.viewmodel.eventHandling.records;

import org.example.mtgspotscrapper.viewmodel.CardList;

public record DeleteCardData(String cardName, CardList cardList) {
}
