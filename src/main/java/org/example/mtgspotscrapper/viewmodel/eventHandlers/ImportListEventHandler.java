package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.ImportListEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;


public class ImportListEventHandler extends MyEventHandler<ImportListEvent> {
    public ImportListEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }


    @Override
    public void handle(ImportListEvent importListEvent) {

    }
}
