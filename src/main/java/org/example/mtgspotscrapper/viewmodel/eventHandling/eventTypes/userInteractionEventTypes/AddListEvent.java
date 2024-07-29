package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;

public class AddListEvent extends UserInteractionEvent {
    public static final EventType<AddListEvent> ADD_LIST = new EventType<>(Event.ANY, "ADD_LIST");

    private final String listName;

    public AddListEvent(String listName) {
        super(ADD_LIST);
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }
}
