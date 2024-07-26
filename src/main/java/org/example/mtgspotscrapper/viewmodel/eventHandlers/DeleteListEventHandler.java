package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.DeleteListEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.sql.SQLException;

public class DeleteListEventHandler extends MyEventHandler<DeleteListEvent> {
    public DeleteListEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(DeleteListEvent deleteListEvent) {
        try {
            databaseService.deleteList(deleteListEvent.getListName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
