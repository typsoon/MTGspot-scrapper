package org.example.mtgspotscrapper.view.viewEvents.eventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.MyGUIEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandlers.records.DeleteCardData;

public class DeleteCardEvent extends MyGUIEvent {
    public static final EventType<DeleteCardEvent> DELETE_CARD_EVENT = new EventType<>(Event.ANY, "DELETE_CARD");

    private final DeleteCardData deleteCardData;

    public DeleteCardEvent(DeleteCardData deleteCardData) {
        super(DELETE_CARD_EVENT);
        this.deleteCardData = deleteCardData;
    }

    public DeleteCardData getDeleteCardData() {
        return deleteCardData;
    }
}
