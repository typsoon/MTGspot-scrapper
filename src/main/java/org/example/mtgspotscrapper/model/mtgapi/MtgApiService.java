package org.example.mtgspotscrapper.model.mtgapi;

import org.example.mtgspotscrapper.model.cardImpl.CardData;

public interface MtgApiService {
    @SuppressWarnings("unused")
    CardData getCardData(Integer multiverseId);
    CardData getCardData(String cardName);
}
