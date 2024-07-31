package org.example.mtgspotscrapper.model.cardImpl;

import org.example.mtgspotscrapper.model.mtgapi.MtgApiService;
import org.example.mtgspotscrapper.model.mtgapi.SimpleMtgApiService;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.DownloaderService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;
import static org.example.mtgspotscrapper.model.databaseClasses.Tables.FULLCARDDATA;

public class CardManager {
    private static final Logger log = LoggerFactory.getLogger(CardManager.class);
    private final HashMap<String, Card> loadedCards = new HashMap<>();
    private final DSLContext dslContext;
    private final MtgApiService mtgApiService = new SimpleMtgApiService();
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final DownloaderService downloaderService;

    public CardManager(DSLContext dslContext, DownloaderService downloaderService) {
        this.dslContext = dslContext;
        this.downloaderService = downloaderService;
    }

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

    public CompletableFuture<Card> addCard(String cardName) {
        return CompletableFuture.supplyAsync(()-> {
            Card addedCard = putCardInDatabase(cardName);
            loadedCards.put(cardName, addedCard);
            return addedCard;
        }, executorService);
    }

    private Card putCardInDatabase(String cardName) {
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

        return new SimpleCard(cardData, downloadedImageAddress, dslContext);
    }

    public Card getCard(CardData cardData, CompletableFuture<String> downloadedImageAddress, DSLContext dslContext) {
        if (loadedCards.containsKey(cardData.cardName())) {
            return loadedCards.get(cardData.cardName());
        }

        Card loadedCard = new SimpleCard(cardData, downloadedImageAddress, dslContext);
        loadedCards.put(cardData.cardName(), loadedCard);
        return loadedCard;
    }
}
