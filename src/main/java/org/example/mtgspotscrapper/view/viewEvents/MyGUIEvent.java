package org.example.mtgspotscrapper.view.viewEvents;
import javafx.event.Event;
import javafx.event.EventType;

//public abstract class MyGUIEvent<T> extends Event {
public abstract class MyGUIEvent extends Event {
    public MyGUIEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
//    protected final T data;
//
//    protected MyGUIEvent(EventType<? extends Event> eventType, T data) {
//        super(eventType);
//        this.data = data;
//    }
//
//    public T getData() {
//        return data;
//    }
}
