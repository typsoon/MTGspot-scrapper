package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.UpdateAvailabilityEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

public class UpdateAvailabilityEventHandler extends MyEventHandler<UpdateAvailabilityEvent> {
    public UpdateAvailabilityEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }


    @Override
    public void handle(UpdateAvailabilityEvent updateAvailabilityEvent) {

    }
}
