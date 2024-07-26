package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.DeleteCardEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.sql.SQLException;

public class DeleteCardEventHandler extends MyEventHandler<DeleteCardEvent> {
    public DeleteCardEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(DeleteCardEvent deleteCardEvent) {
        CardList cardList = deleteCardEvent.getDeleteCardData().cardList();

        try {
            if(cardList.deleteCardFromList(deleteCardEvent.getDeleteCardData().cardName()))
                myEventLogger.info("Card deleted: {}, List: {}", deleteCardEvent.getDeleteCardData().cardName(), cardList);
            else myEventLogger.info("Card was not deleted (not in list): Card: {}, List: {}", deleteCardEvent.getDeleteCardData().cardName(), cardList);
        } catch (SQLException e) {
//            screenManager.displayAlert(e);
            myEventLogger.error("Failed to delete card: {}, from list: {}", deleteCardEvent.getDeleteCardData().cardName(), cardList, e);
            throw new RuntimeException(e);
        }
    }
}
