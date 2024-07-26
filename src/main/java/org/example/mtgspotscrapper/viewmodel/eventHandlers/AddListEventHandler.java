package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.AddListEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.io.IOException;
import java.sql.SQLException;

public class AddListEventHandler extends MyEventHandler<AddListEvent> {
    public AddListEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(AddListEvent addListEvent) {
        try {
            @SuppressWarnings("unused")
            CardList cardList = databaseService.addList(addListEvent.getListName());
            myEventLogger.info("List added, list name: {}", addListEvent.getListName());
        } catch (SQLException | IOException e) {
            myEventLogger.error("Failed to add list: {}", addListEvent.getListName(),  e);
            throw new RuntimeException(e);
        }
    }
}
