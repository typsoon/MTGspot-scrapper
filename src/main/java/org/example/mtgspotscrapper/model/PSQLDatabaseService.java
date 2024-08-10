package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.cardImpl.CardManager;
import org.example.mtgspotscrapper.model.listImpl.ListData;
import org.example.mtgspotscrapper.model.listImpl.SimpleCardList;
import org.example.mtgspotscrapper.model.mtgapi.MtgApiService;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.DownloaderService;

import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class PSQLDatabaseService implements DatabaseService {

    private final DSLContext dslContext;
    private final CardManager cardManager;

    private final Collection<String> allCardsNames;

    public PSQLDatabaseService(String url, String user, String password, DownloaderService downloaderService, MtgApiService mtgApiService, boolean executeJooqLogging) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);

        Settings settings = new Settings()
                .withExecuteLogging(executeJooqLogging);

        dslContext = DSL.using(connection, SQLDialect.POSTGRES, settings);
        cardManager = new CardManager(dslContext, mtgApiService, downloaderService);

        allCardsNames = dslContext.select(ALLCARDSVIEW.CARD_NAME)
                .from(ALLCARDSVIEW)
                .fetchInto(String.class);
    }

    @Override
    public Collection<CardList> getAllLists() {
        Collection<CardList> answer = new ArrayList<>();
        Result<Record> lists = dslContext.select().from(LISTSWITHLOGOS)
                .fetch();

        for (Record list : lists) {
            answer.add(new SimpleCardList(new ListData(list.getValue(LISTSWITHLOGOS.LIST_ID), list.getValue(LISTSWITHLOGOS.LIST_NAME), list.getValue(LISTSWITHLOGOS.LOGO_PATH)), dslContext, cardManager));
        }

        return answer;
    }

    @Override
    public CardList getCardList(String name) {
        Result<Record> lists = dslContext.select()
                .from(LISTSWITHLOGOS)
                .where(LISTSWITHLOGOS.LIST_NAME.eq(name))
                .fetch();

        if (lists.isEmpty()) {
            return null;
        }
        if (lists.size() > 1) {
            throw new IllegalStateException("More than one card found for name " + name);
        }

        Record list = lists.getFirst();
        return new SimpleCardList(new ListData(list.getValue(LISTSWITHLOGOS.LIST_ID), list.getValue(LISTSWITHLOGOS.LIST_NAME), list.getValue(LISTSWITHLOGOS.LOGO_PATH)), dslContext, cardManager);
    }

    @Override
    public CompletableFuture<Card> addCard(String cardName) {
        return cardManager.addCard(cardName);
    }

    @Override
    public CardList addList(String listName) {
        Integer newListId = dslContext.insertInto(LISTSWITHLOGOS, LISTSWITHLOGOS.LIST_NAME)
                    .values(listName)
                    .returningResult(LISTSWITHLOGOS.LIST_ID)
                    .fetch().map(Record1::component1)
                    .getFirst();

        return new SimpleCardList(new ListData(newListId, listName, null), dslContext, cardManager);
    }

    @Override
    public ObservableAtomicCounter getCurrentlyAddedCardsCounter() {
        return cardManager.getCurrentlyAddedCardsCounter();
    }

    @Override
    public boolean deleteList(String listName) {
        dslContext.deleteFrom(LISTSWITHLOGOS)
                .where(LISTSWITHLOGOS.LIST_NAME.eq(listName))
                .execute();

        return true;
    }

    @Override
    public Card getCard(String cardName) {
        return cardManager.getCard(cardName);
    }


    @Override
    public Collection<String> getAllCardNames() {
        return allCardsNames;
    }
}
