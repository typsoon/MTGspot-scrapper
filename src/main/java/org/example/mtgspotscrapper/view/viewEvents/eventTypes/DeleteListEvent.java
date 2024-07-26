package org.example.mtgspotscrapper.view.viewEvents.eventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.MyGUIEvent;

public class DeleteListEvent extends MyGUIEvent {
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
