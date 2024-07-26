package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.UpdateAvailabilityEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.sql.SQLException;

public class UpdateAvailabilityEventHandler extends MyEventHandler<UpdateAvailabilityEvent> {
    public UpdateAvailabilityEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }


    @Override
    public void handle(UpdateAvailabilityEvent updateAvailabilityEvent) {
        try {
            for (Card card : updateAvailabilityEvent.getCardList().getCards())
                card.updatePrice();
        } catch (SQLException e) {
            myEventLogger.error("Error while updating availability", e);
            throw new RuntimeException(e);
        }
    }
}
