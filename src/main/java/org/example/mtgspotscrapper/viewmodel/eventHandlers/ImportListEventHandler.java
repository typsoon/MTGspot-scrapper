package org.example.mtgspotscrapper.viewmodel.eventHandlers;

import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.MyEventHandler;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.ImportListEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImportListEventHandler extends MyEventHandler<ImportListEvent> {
    public ImportListEventHandler(DatabaseService databaseService) {
        super(databaseService);
    }


    @Override
    public void handle(ImportListEvent importListEvent) {
        Collection<String> cardNames = new ArrayList<>();
//        Pattern pattern = Pattern.compile("\\d+\\s+([^\\(]+)\\s+\\([A-Z0-9]+([^\\n]*)");
        Pattern pattern = Pattern.compile("\\d+\\s+([^(]+)\\s+([^\\n]*)");
        Matcher matcher = pattern.matcher(importListEvent.getCards());

        while (matcher.find()) {
//            myEventLogger.debug('\n' + "{}", matcher.group());
            String cardName = matcher.group(1).trim();
            cardNames.add(cardName);
        }

        try {
            CardList addedList = databaseService.addList(importListEvent.getListName());

            for (String name : cardNames) {
                Card card = databaseService.getCard(name);
                if (card == null) {
                    card = databaseService.addCard(name);
                }

                addedList.addCardToList(card);
            }
        }
        catch (SQLException | IOException e) {
            myEventLogger.error("Error adding list {}", importListEvent.getListName(), e);
            throw new RuntimeException(e);
        }
    }
}
