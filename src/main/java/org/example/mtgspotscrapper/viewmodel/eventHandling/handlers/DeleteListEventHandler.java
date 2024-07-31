package org.example.mtgspotscrapper.viewmodel.eventHandling.handlers;

import org.example.mtgspotscrapper.viewmodel.eventHandling.MyEventHandler;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.DeleteListEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

public class DeleteListEventHandler extends MyEventHandler<DeleteListEvent> {
    protected static Logger deleteListLogger = LoggerFactory.getLogger(DeleteListEventHandler.class);

    public DeleteListEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(DeleteListEvent deleteListEvent) {
        try {
            if (Objects.equals(deleteListEvent.getListName(), "Collection")) {
                deleteListLogger.info("Can't delete collection");
                return;
            }

            if(databaseService.deleteList(deleteListEvent.getListName()))
                deleteListLogger.info("Deleted list: {}", deleteListEvent.getListName());
            else deleteListLogger.info("Could not delete list (list doesn't exist): {}", deleteListEvent.getListName());
        } catch (Exception e) {
            deleteListLogger.error("Failed to delete list {}", deleteListEvent.getListName(), e);
            throw new RuntimeException(e);
        }
    }
}
