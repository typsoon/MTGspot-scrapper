package org.example.mtgspotscrapper.viewmodel.eventHandling.handlers;

import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.eventHandling.MyEventHandler;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.FixBadPricesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixBadPricesEventHandler extends MyEventHandler<FixBadPricesEvent> {
    private static final Logger fixBadPricesLogger = LoggerFactory.getLogger(FixBadPricesEventHandler.class);

    public FixBadPricesEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(FixBadPricesEvent fixBadPricesEvent) {
        try {
            fixBadPricesEvent.getCardList()
                    .getCards().stream().filter(card -> card.getActCardPrice().actPrice() == -1)
                    .forEach(Card::updatePrice);
        } catch (Exception e) {
            fixBadPricesLogger.error("Error while fixing bad prices", e);
        }
    }
}
