package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.cardImpl.CardManager;
import org.example.mtgspotscrapper.model.listImpl.ListData;
import org.example.mtgspotscrapper.model.listImpl.SimpleCardList;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.DownloaderService;


import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class PSQLDatabaseService implements DatabaseService {
    private static final Logger log = LoggerFactory.getLogger(PSQLDatabaseService.class);

    private final DSLContext dslContext;
    private final CardManager cardManager;

    public PSQLDatabaseService(String url, String user, String password, DownloaderService downloaderService, boolean executeJooqLogging) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);

        Settings settings = new Settings()
                .withExecuteLogging(executeJooqLogging);

        dslContext = DSL.using(connection, SQLDialect.POSTGRES, settings);
        cardManager = new CardManager(dslContext, downloaderService);
    }

    @Override
    public Collection<CardList> getAllLists() {
        Collection<CardList> answer = new ArrayList<>();
        Result<Record> lists = dslContext.select().from(LISTS).leftJoin(LISTSLOGOS)
                .using(LISTS.LOGO_ID)
                .fetch();

        for (Record list : lists) {
            answer.add(new SimpleCardList(new ListData(list.getValue(LISTS.LIST_ID), list.getValue(LISTS.LIST_NAME), list.getValue(LISTSLOGOS.LOGO_PATH)), dslContext, cardManager));
        }

        return answer;
    }

    @Override
    public CardList getCardList(String name) {
        Result<Record> lists = dslContext.select()
                .from(LISTS)
                .leftJoin(LISTSLOGOS)
                .using(LISTS.LOGO_ID)
                .where(LISTS.LIST_NAME.eq(name))
                .fetch();

        if (lists.isEmpty()) {
            return null;
        }
        if (lists.size() > 1) {
            throw new IllegalStateException("More than one card found for name " + name);
        }

        Record list = lists.getFirst();
        return new SimpleCardList(new ListData(list.getValue(LISTS.LIST_ID), list.getValue(LISTS.LIST_NAME), list.getValue(LISTSLOGOS.LOGO_PATH)), dslContext, cardManager);
    }

    @Override
    public CompletableFuture<Card> addCard(String cardName) {
        return cardManager.addCard(cardName);
    }

    @Override
    public CardList addList(String listName) {
        try {
            dslContext.insertInto(LISTS, LISTS.LIST_NAME)
                    .values(listName).returningResult(LISTS.LIST_ID)
                    .execute();

            int newListId = Objects.requireNonNull(dslContext.select(LISTS.LIST_ID)
                            .from(LISTS)
                            .where(LISTS.LIST_NAME.eq(listName))
                            .fetchOne())
                    .getValue(LISTS.LIST_ID);

            return new SimpleCardList(new ListData(newListId, listName, null), dslContext, cardManager);
//            return new SimpleCardList(new ListData(listId, listName, null), dslContext);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteList(String listName) {
        Result<Record1<Integer>> lists = dslContext.selectDistinct(LISTS.LIST_ID).from(LISTS)
                .where(LISTS.LIST_NAME.eq(listName))
                .fetch();

        if (lists.isEmpty()) {
            return false;
        }

        if (lists.size() > 1) {
            throw new IllegalStateException("More than one list found for name " + listName);
        }

        int listId = lists.getFirst().getValue(LISTS.LIST_ID);
        dslContext.deleteFrom(LISTCARDS).where(LISTCARDS.LIST_ID.eq(listId)).execute();
        dslContext.deleteFrom(LISTS).where(LISTS.LIST_ID.eq(listId)).execute();

        return true;
    }

    @Override
    public Card getCard(String cardName) {
        return cardManager.getCard(cardName);
    }


    public static void main(String[] args) {
        try {
            DatabaseService databaseService = new PSQLDatabaseService("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa", new SimpleDownloaderService(), true);
            log.debug(databaseService.getAllLists().toString());
//            log.debug(databaseService.getAllCardsData().toString());
            log.debug(databaseService.getCardList("test list").toString());
            log.debug(databaseService.addCard("Kodama's Reach").toString());
            log.debug(databaseService.addCard("Swords to Plowshares").toString());
            log.debug(databaseService.addCard("Kenrith, the Returned King").toString());
            log.debug(databaseService.addCard("Path to Exile").toString());

//            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa");
//            DSLContext dslContext = DSL.using(conn, SQLDialect.POSTGRES);
//            Result<Record> temp = dslContext.select().from(CARDS).fetch();
//            System.out.println(temp.getClass());
//
//            for (Record record : temp) {
//                System.out.println(record.getValue(CARDS.MULTIVERSE_ID));
//            }

        } catch (SQLException e) {
            log.error("Something went wrong", e);
        }
    }
}
