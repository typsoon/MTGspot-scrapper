package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.cardImpl.SimpleCard;
import org.example.mtgspotscrapper.model.mtgapi.MtgApiService;
import org.example.mtgspotscrapper.model.mtgapi.SimpleMtgApiService;
import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.DownloaderService;


import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class PSQLDatabaseService implements DatabaseService {
    private static final Logger log = LoggerFactory.getLogger(PSQLDatabaseService.class);
    private final DownloaderService downloaderService;
    private final ConcurrentMap<String, Card> loadedCards = new ConcurrentHashMap<>();
    private final MtgApiService mtgApiService = new SimpleMtgApiService();
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final DSLContext dslContext;

    @Override
    public Collection<CardList> getAllLists() {
        Collection<CardList> answer = new ArrayList<>();
        Result<Record> lists = dslContext.select().from(LISTS).leftJoin(LISTSLOGOS)
                .using(LISTS.LOGO_ID)
                .fetch();

        for (Record list : lists) {
            answer.add(new SimpleCardList(new ListData(list.getValue(LISTS.LIST_ID), list.getValue(LISTS.LIST_NAME), list.getValue(LISTSLOGOS.LOGO_PATH)), dslContext));
        }

        return answer;
    }

    @Override
    public CardList getCardList(String name) {
        Result<Record> lists = dslContext.select()
                .from(LISTS).leftJoin(LISTSLOGOS).using(LISTS.LOGO_ID)
                .where(LISTS.LIST_NAME.eq(name))
                .fetch();

        if (lists.isEmpty()) {
            return null;
        }
        if (lists.size() > 1) {
            throw new IllegalStateException("More than one card found for name " + name);
        }

        Record list = lists.getFirst();
        return new SimpleCardList(new ListData(list.getValue(LISTS.LIST_ID), list.getValue(LISTS.LIST_NAME), list.getValue(LISTSLOGOS.LOGO_PATH)), dslContext);
    }

    @Override
    public CompletableFuture<Card> addCard(String cardName) {
        return CompletableFuture.supplyAsync(()-> {
            log.debug("Before pinging api");
            CardData cardData = mtgApiService.getCardData(cardName);
            log.debug("After pinging api");

            if (cardData == null) {
//                TODO: write an exception for this
                log.info("Card not found: {}", cardName);
                return null;
            }

            Result<Record> result = dslContext.select().from(CARDS)
                    .where(CARDS.MULTIVERSE_ID.eq(cardData.multiverseId()))
                    .fetch();

            if (!result.isEmpty()) {
                return getCard(cardName);
            }

//        TODO: make it so the download is handled by another thread

            CompletableFuture<String> downloadedImageAddress;
            try {
                downloadedImageAddress = downloaderService.downloadCardImage(new URI(cardData.imageUrl()).toURL(), cardData.multiverseId());
            } catch (URISyntaxException e) {
                log.error("Inconsistent data in database: ", e);
                throw new IllegalStateException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            dslContext.insertInto(CARDS, CARDS.MULTIVERSE_ID, CARDS.CARD_NAME, CARDS.IMAGE_URL)
                    .values(cardData.multiverseId(), cardData.cardName(), cardData.imageUrl())
                    .execute();

            downloadedImageAddress.thenAcceptAsync(imageAddress -> dslContext.insertInto(LOCALADDRESSES, LOCALADDRESSES.MULTIVERSE_ID, LOCALADDRESSES.LOCAL_ADDRESS)
                    .values(cardData.multiverseId(), imageAddress)
                    .execute()).exceptionally(throwable -> {
                log.error("Failed to insert card", throwable);
                throw new RuntimeException("Failed to insert card", throwable);
            });

            Card addedCard = new SimpleCard(cardData, downloadedImageAddress, dslContext);
            loadedCards.put(cardName, addedCard);
            return addedCard;
        }, executorService);
    }

    @Override
    public CardList addList(String listName) {
        try {
            int listId = dslContext.insertInto(LISTS, LISTS.LIST_NAME)
                    .values(listName).returning(LISTS.LIST_ID)
                    .execute();

            return new SimpleCardList(new ListData(listId, listName, null), dslContext);
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
        if (loadedCards.containsKey(cardName)) {
            return loadedCards.get(cardName);
        }

        Result<Record> cards = dslContext.select().from(FULLCARDDATA)
                .where(FULLCARDDATA.CARD_NAME.eq(cardName))
                .fetch();

        if (cards.isEmpty()) {
            return null;
        }

        if (cards.size() > 1) {
            throw new IllegalStateException("More than one card found for name " + cardName);
        }

        Record card = cards.getFirst();

        return new SimpleCard(
                new CardData(card.getValue(FULLCARDDATA.MULTIVERSE_ID), card.getValue(FULLCARDDATA.CARD_NAME), card.getValue(FULLCARDDATA.IMAGE_URL)),
                CompletableFuture.completedFuture(card.getValue(FULLCARDDATA.LOCAL_ADDRESS)), dslContext);
    }

    public PSQLDatabaseService(String url, String user, String password, DownloaderService downloaderService) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        this.downloaderService = downloaderService;

        dslContext = DSL.using(connection, SQLDialect.POSTGRES);
    }


    public static void main(String[] args) {
        try {
            DatabaseService databaseService = new PSQLDatabaseService("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa", new SimpleDownloaderService());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
