package org.example.mtgspotscrapper.view.viewEvents;

import javafx.event.Event;
import javafx.event.EventType;

@SuppressWarnings("unused")
public abstract class GUIEvent extends Event {
    public GUIEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
