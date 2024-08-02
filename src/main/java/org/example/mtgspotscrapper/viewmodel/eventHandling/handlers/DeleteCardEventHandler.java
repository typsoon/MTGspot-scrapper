package org.example.mtgspotscrapper.viewmodel.eventHandling.handlers;

import org.example.mtgspotscrapper.viewmodel.eventHandling.MyEventHandler;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.DeleteCardEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteCardEventHandler extends MyEventHandler<DeleteCardEvent> {
    protected static Logger deleteCardLogger = LoggerFactory.getLogger(DeleteListEventHandler.class);

    public DeleteCardEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(DeleteCardEvent deleteCardEvent) {
        CardList cardList = deleteCardEvent.getDeleteCardData().cardList();

        try {
            if(cardList.deleteCardFromList(deleteCardEvent.getDeleteCardData().cardName()))
                deleteCardLogger.info("Card deleted: {}, List: {}", deleteCardEvent.getDeleteCardData().cardName(), cardList);
            else deleteCardLogger.info("Card was not deleted (not in list): Card: {}, List: {}", deleteCardEvent.getDeleteCardData().cardName(), cardList);
        } catch (Exception e) {
//            screenManager.displayAlert(e);
            deleteCardLogger.error("Failed to delete card: {}, from list: {}", deleteCardEvent.getDeleteCardData().cardName(), cardList, e);
            throw new RuntimeException(e);
        }
    }
}
