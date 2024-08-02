package org.example.mtgspotscrapper.viewmodel.eventHandling.handlers;

import org.example.mtgspotscrapper.viewmodel.eventHandling.MyEventHandler;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.AddCardEvent;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AddCardEventHandler extends MyEventHandler<AddCardEvent> {
    private static final Logger addEventLogger = LoggerFactory.getLogger(AddCardEventHandler.class);

    public AddCardEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(AddCardEvent addCardEvent) {
        try {
            String cardName = addCardEvent.getCardName();
            Card addedCard = databaseService.getCard(cardName);

            if (addedCard == null) {
                databaseService.addCard(addCardEvent.getCardName()).thenAccept(card -> executeAdd(card, addCardEvent.getCardList()));
            }
            else {
                executeAdd(addedCard, addCardEvent.getCardList());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeAdd(Card card, CardList cardList) {
        Objects.requireNonNull(cardList).addCardToList(card);

        if (card != null)
            addEventLogger.info("Card added: {}, List: {}", card.getCardData().cardName(), cardList);
    }
}
