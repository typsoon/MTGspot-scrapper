package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;

public class FixBadPricesEvent extends UserInteractionEvent {
    public static final EventType<FixBadPricesEvent> FIX_PRICES = new EventType<>(Event.ANY, "FIX_PRICES");

    private final CardList cardList;

    public FixBadPricesEvent(CardList cardList) {
        super(FIX_PRICES);
        this.cardList = cardList;
    }

    public CardList getCardList() {
        return cardList;
    }
}
