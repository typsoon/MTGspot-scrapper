package org.example.mtgspotscrapper.view.viewEvents.guiEvents;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.GUIEvent;

public class ShowAllListsEvent extends GUIEvent {
    public final static EventType<ShowAllListsEvent> SHOW_ALL_LISTS= new EventType<>(Event.ANY, "SHOW_ALL_LISTS");

    public ShowAllListsEvent() {
        super(SHOW_ALL_LISTS);
    }
}
