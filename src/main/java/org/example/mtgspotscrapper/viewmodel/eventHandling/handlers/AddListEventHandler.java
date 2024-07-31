package org.example.mtgspotscrapper.viewmodel.eventHandling.handlers;

import org.example.mtgspotscrapper.viewmodel.eventHandling.MyEventHandler;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.AddListEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddListEventHandler extends MyEventHandler<AddListEvent> {
    protected static Logger addListLogger = LoggerFactory.getLogger(AddListEventHandler.class);

    public AddListEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(AddListEvent addListEvent) {
        try {
            @SuppressWarnings("unused")
            CardList cardList = databaseService.addList(addListEvent.getListName());
            addListLogger.info("List added, list name: {}", addListEvent.getListName());
        } catch (Exception e) {
            addListLogger.error("Failed to add list: {}", addListEvent.getListName(),  e);
            throw new RuntimeException(e);
        }
    }
}
