package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;

public class DeleteListEvent extends UserInteractionEvent {
    public static final EventType<DeleteListEvent> DELETE_LIST = new EventType<>(Event.ANY, "DELETE_LIST");

    private final String listName;

    public DeleteListEvent(String listName) {
        super(DELETE_LIST);
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }
}
