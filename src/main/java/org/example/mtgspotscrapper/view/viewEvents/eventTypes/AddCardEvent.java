package org.example.mtgspotscrapper.view.viewEvents.eventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.MyGUIEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandlers.records.AddCardData;

public class AddCardEvent extends MyGUIEvent {
    public static final EventType<AddCardEvent> ADD_CARD_EVENT = new EventType<>(Event.ANY, "ADD_CARD_EVENT");

    public AddCardData addCardData;

    public AddCardEvent(AddCardData addCardData) {
        super(ADD_CARD_EVENT);
        this.addCardData = addCardData;
    }

    public AddCardData getAddCardData() {
        return addCardData;
    }
}
