package org.example.mtgspotscrapper.viewmodel.eventHandling;
import javafx.event.Event;
import javafx.event.EventType;

//public abstract class UserInteractionEvent<T> extends Event {
public abstract class UserInteractionEvent extends Event {
    public UserInteractionEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
//    protected final T data;
//
//    protected UserInteractionEvent(EventType<? extends Event> eventType, T data) {
//        super(eventType);
//        this.data = data;
//    }
//
//    public T getData() {
//        return data;
//    }
}
