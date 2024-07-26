package org.example.mtgspotscrapper.viewmodel;

import javafx.event.EventHandler;
import org.example.mtgspotscrapper.view.viewEvents.MyGUIEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MyEventHandler<T extends MyGUIEvent> implements EventHandler<T> {
    protected static Logger myEventLogger = LoggerFactory.getLogger(MyEventHandler.class);

    protected final DatabaseService databaseService;

    public MyEventHandler(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public abstract void handle(T myGUIEvent);
}
