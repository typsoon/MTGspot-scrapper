package org.example.mtgspotscrapper.viewmodel.eventHandling;

import javafx.event.EventHandler;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

public abstract class MyEventHandler<T extends UserInteractionEvent> implements EventHandler<T> {
    protected final DatabaseService databaseService;

    public MyEventHandler(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public abstract void handle(T myGUIEvent);
}
