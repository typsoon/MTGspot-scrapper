package org.example.mtgspotscrapper.viewmodel.eventHandling.handlers;

import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.eventHandling.MyEventHandler;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.ImportListEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportListEventHandler extends MyEventHandler<ImportListEvent> {
    protected static Logger importEventLogger = LoggerFactory.getLogger(ImportListEventHandler.class);
    public ImportListEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }

    @Override
    public void handle(ImportListEvent importListEvent) {
        Collection<String> cardNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+\\s+([^(]+)\\s+([^\\n]*)");
        Matcher matcher = pattern.matcher(importListEvent.getCards());

        while (matcher.find()) {
            String cardName = matcher.group(1).trim();
            cardNames.add(cardName);
        }

        try {
            CardList addedList = databaseService.addList(importListEvent.getListName());

            for (String name : cardNames) {
                Card addedCard = databaseService.getCard(name);
                if (addedCard == null) {
                    databaseService.addCard(name).thenAccept(card -> executeAdd(card, addedList));
                }
                else {
                    executeAdd(addedCard, addedList);
                }
            }
        }
        catch (SQLException | IOException e) {
            importEventLogger.error("Error adding list {}", importListEvent.getListName(), e);
            throw new RuntimeException(e);
        }
    }

    private void executeAdd(Card card, CardList cardList) {
        try {
            Objects.requireNonNull(cardList).addCardToList(card);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

//        if (card != null)
//            importEventLogger.info("Card added: {}, List: {}", card.getCardData().cardName(), cardList);
    }
}
