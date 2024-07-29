package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.records.DeleteCardData;

public class DeleteCardEvent extends UserInteractionEvent {
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
