package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.records.SearchListData;

@SuppressWarnings("unused")
public class SearchListEvent extends UserInteractionEvent {
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
