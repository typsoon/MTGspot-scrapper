package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;

public class UpdateAvailabilityEvent extends UserInteractionEvent {
    public static final EventType<UpdateAvailabilityEvent> UPDATE_AVAILABILITY = new EventType<>(Event.ANY, "UPDATE_AVAILABILITY");

    private final CardList cardList;

    public UpdateAvailabilityEvent(CardList cardList) {
        super(UPDATE_AVAILABILITY);
        this.cardList = cardList;
    }

    @SuppressWarnings("unused")
    public CardList getCardList() {
        return cardList;
    }
}
