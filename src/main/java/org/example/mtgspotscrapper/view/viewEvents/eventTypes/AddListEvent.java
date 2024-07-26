package org.example.mtgspotscrapper.view.viewEvents.eventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.MyGUIEvent;

public class AddListEvent extends MyGUIEvent {
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
