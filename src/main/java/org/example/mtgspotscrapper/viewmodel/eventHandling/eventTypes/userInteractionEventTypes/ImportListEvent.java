package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;

public class ImportListEvent extends UserInteractionEvent {
    public static final EventType<ImportListEvent> IMPORT_LIST = new EventType<>(Event.ANY, "IMPORT_LIST");

    private final String listName;
    private final String listData;

    public ImportListEvent(String listData, String listName) {
        super(IMPORT_LIST);
        this.listData = listData;
        this.listName = listName;
    }

    public String getCards() {
        return listData;
    }

    public String getListName() {
        return listName;
    }
}
