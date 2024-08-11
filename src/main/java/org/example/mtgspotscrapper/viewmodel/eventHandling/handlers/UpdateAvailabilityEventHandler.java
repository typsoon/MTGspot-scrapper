package org.example.mtgspotscrapper.viewmodel.eventHandling.handlers;

import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.eventHandling.MyEventHandler;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.UpdateAvailabilityEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAvailabilityEventHandler extends MyEventHandler<UpdateAvailabilityEvent> {
    protected static Logger updateAvailabilityLogger = LoggerFactory.getLogger(UpdateAvailabilityEvent.class);

    public UpdateAvailabilityEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(UpdateAvailabilityEvent updateAvailabilityEvent) {
        try {
            updateAvailabilityEvent.getCardList()
                    .getCards()
                    .forEach(Card::updatePrice);
        } catch (Exception e) {
            updateAvailabilityLogger.error("Error while updating availability", e);
        }
    }
}
