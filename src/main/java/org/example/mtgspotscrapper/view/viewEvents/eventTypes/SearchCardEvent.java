package org.example.mtgspotscrapper.view.viewEvents.eventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.MyGUIEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandlers.records.SearchCardData;

@SuppressWarnings("unused")
public class SearchCardEvent extends MyGUIEvent {
    public static final EventType<SearchCardEvent> SEARCH_CARD = new EventType<>(Event.ANY, "SEARCH_CARD");

    private final SearchCardData searchCardData;

    public SearchCardEvent(SearchCardData searchCardData) {
        super(SEARCH_CARD);
        this.searchCardData = searchCardData;
    }

    public SearchCardData getSearchCardData() {
        return searchCardData;
    }
}
