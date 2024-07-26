package org.example.mtgspotscrapper.view.viewEvents.eventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.MyGUIEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandlers.records.SearchListData;

@SuppressWarnings("unused")
public class SearchListEvent extends MyGUIEvent {
    public static final EventType<SearchListEvent> searchListEvent = new EventType<>(Event.ANY, "SEARCH_LIST");

    private final SearchListData searchListData;

    public SearchListEvent(SearchListData searchListData) {
        super(searchListEvent);
        this.searchListData = searchListData;
    }

    public SearchListData getSearchListData() {
        return searchListData;
    }
}
