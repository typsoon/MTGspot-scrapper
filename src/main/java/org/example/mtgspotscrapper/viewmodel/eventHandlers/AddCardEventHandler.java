package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.AddCardEvent;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class AddCardEventHandler extends MyEventHandler<AddCardEvent> {
    private static final Logger addEventLogger = LoggerFactory.getLogger(AddCardEventHandler.class);

    public AddCardEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(AddCardEvent addCardEvent) {
        try {
            String cardName = addCardEvent.getAddCardData().cardName();
            Card addedCard = databaseService.getCard(cardName);

            if (addedCard == null)
                addedCard = databaseService.addCard(addCardEvent.getAddCardData().cardName());

            CardList cardList = addCardEvent.addCardData.cardList();
            if (cardList != null) {
                cardList.addCardToList(addedCard);
            }

            if (addedCard != null)
                addEventLogger.info("Card added: {}, List: {}", cardName, addCardEvent.addCardData.cardList());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
