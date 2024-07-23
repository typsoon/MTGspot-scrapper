package org.example.mtgspotscrapper.model.records;

import org.example.mtgspotscrapper.model.mtgapi.ImportantCardData;

public record PrevPriceAndCardData(double prevPrice, ImportantCardData importantCardData) {
}
