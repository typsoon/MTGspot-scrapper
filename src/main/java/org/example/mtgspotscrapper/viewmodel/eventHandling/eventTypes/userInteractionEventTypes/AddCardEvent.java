package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;

public class AddCardEvent extends UserInteractionEvent {
    public static final EventType<AddCardEvent> ADD_CARD_EVENT = new EventType<>(Event.ANY, "ADD_CARD_EVENT");

    public String cardName;
    public CardList cardList;

    public AddCardEvent(String cardName, CardList cardList) {
        super(ADD_CARD_EVENT);
        this.cardName = cardName;
        this.cardList = cardList;
    }

    public String getCardName() {
        return cardName;
    }

    public CardList getCardList() {
        return cardList;
    }
}
