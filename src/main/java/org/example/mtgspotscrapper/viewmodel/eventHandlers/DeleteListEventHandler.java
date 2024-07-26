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
            if(databaseService.deleteList(deleteListEvent.getListName()))
                myEventLogger.info("Deleted list: {}", deleteListEvent.getListName());
            else myEventLogger.info("Could not delete list (list doesn't exist): {}", deleteListEvent.getListName());
        } catch (SQLException e) {
            myEventLogger.error("Failed to delete list {}", deleteListEvent.getListName(), e);
            throw new RuntimeException(e);
        }
    }
}
