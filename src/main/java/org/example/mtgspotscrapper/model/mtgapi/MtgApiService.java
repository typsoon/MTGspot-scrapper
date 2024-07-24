package org.example.mtgspotscrapper.model.mtgapi;

import org.example.mtgspotscrapper.model.records.CardData;

public interface MtgApiService {
    CardData getCardData(String cardName);
}
