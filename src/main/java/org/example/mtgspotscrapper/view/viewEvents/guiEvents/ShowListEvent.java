package org.example.mtgspotscrapper.view.viewEvents.guiEvents;

import javafx.event.Event;
import javafx.event.EventType;
import org.example.mtgspotscrapper.view.viewEvents.GUIEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;

public class ShowListEvent extends GUIEvent {
    public static final EventType<ShowListEvent> SHOW_LIST = new EventType<>(Event.ANY, "LIST_CLICKED");
    private final CardList cardList;

    public ShowListEvent(CardList cardList) {
        super(ShowListEvent.SHOW_LIST);
        this.cardList = cardList;
    }

    public CardList getCardList() {
        return cardList;
    }
}
