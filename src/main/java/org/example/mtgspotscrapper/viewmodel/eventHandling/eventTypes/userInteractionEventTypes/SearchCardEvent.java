package org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.viewmodel.eventHandling.UserInteractionEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.records.SearchCardData;

@SuppressWarnings("unused")
public class SearchCardEvent extends UserInteractionEvent {
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
