package org.example.mtgspotscrapper.model.cardImpl;

import org.example.mtgspotscrapper.model.ObservableAtomicCounter;
import org.example.mtgspotscrapper.model.SimpleObservableAtomicCounter;
import org.example.mtgspotscrapper.model.mtgapi.MtgApiService;
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

public class CardManager {
    private static final Logger log = LoggerFactory.getLogger(CardManager.class);
    private final HashMap<String, Card> loadedCards = new HashMap<>();
    private final DSLContext dslContext;
    private final MtgApiService mtgApiService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final DownloaderService downloaderService;
    private final ObservableAtomicCounter currentlyAddedCardsCounter = new SimpleObservableAtomicCounter();

    public CardManager(DSLContext dslContext, MtgApiService mtgApiService, DownloaderService downloaderService) {
        this.dslContext = dslContext;
        this.mtgApiService = mtgApiService;
        this.downloaderService = downloaderService;
    }

    private Integer getMultiverseId(String cardName) {
        var record = dslContext.select(ALLCARDSVIEW.MULTIVERSE_ID)
                .from(ALLCARDSVIEW)
                .where(ALLCARDSVIEW.CARD_NAME.eq(cardName))
                .fetchOne();
        return record == null ? null : record.getValue(ALLCARDSVIEW.MULTIVERSE_ID);
    }

    public Card getCard(String cardName) {
        if (loadedCards.containsKey(cardName)) {
            return loadedCards.get(cardName);
        }

        Result<Record> cards = dslContext.select().from(FULLDOWNLOADEDCARDDATA)
                .where(FULLDOWNLOADEDCARDDATA.CARD_NAME.eq(cardName))
                .fetch();

        if (cards.isEmpty()) {
            return null;
        }

        if (cards.size() > 1) {
            throw new IllegalStateException("More than one card found for name " + cardName);
        }

        Record card = cards.getFirst();

        return new SimpleCard(
                new CardData(card.getValue(FULLDOWNLOADEDCARDDATA.MULTIVERSE_ID), card.getValue(FULLDOWNLOADEDCARDDATA.CARD_NAME), card.getValue(FULLDOWNLOADEDCARDDATA.IMAGE_URL)),
                CompletableFuture.completedFuture(card.getValue(FULLDOWNLOADEDCARDDATA.LOCAL_ADDRESS)), dslContext);
    }

    public CompletableFuture<Card> addCard(String cardName) {
        return CompletableFuture.supplyAsync(()-> {
            log.debug("Before pinging api");
//            final Integer multiverseId = getMultiverseId(cardName);
            CardData cardData;
//            if (multiverseId != null) {
//                cardData = mtgApiService.getCardData(multiverseId);
//            }
//            else {
                cardData = mtgApiService.getCardData(cardName);
//            }
            log.debug("After pinging api");

            if (cardData == null) {
//                TODO: write an exception for this
//                log.debug("Getting multiverse id of card: {}", cardName);
                final Integer multiverseId = getMultiverseId(cardName);
//                log.debug("Multiverse id is {}", multiverseId);
                if (multiverseId == null) {
                    log.info("Card not found: {}", cardName);
                    return null;
                }
                cardData = new CardData(multiverseId, cardName, "https://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+multiverseId+"&type=card");

//                cardData = mtgApiService.getCardData(multiverseId);
//
//                if (cardData == null) {
//                    log.info("Card not found: {}", cardName);
//                    return null;
//                }
            }

            Card addedCard = putCardInDatabase(cardData);
            loadedCards.put(cardName, addedCard);
            return addedCard;
        }, executorService).exceptionally(throwable -> {
            log.error("Error while adding card", throwable);
            return null;
        });
    }

    public ObservableAtomicCounter getCurrentlyAddedCardsCounter() {
        return currentlyAddedCardsCounter;
    }

    private Card putCardInDatabase(CardData cardData) {
        Result<Record> result = dslContext.select().from(FULLDOWNLOADEDCARDDATA)
                .where(FULLDOWNLOADEDCARDDATA.MULTIVERSE_ID.eq(cardData.multiverseId()))
                .fetch();

        if (!result.isEmpty()) {
            return getCard(cardData.cardName());
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

        dslContext.insertInto(FULLDOWNLOADEDCARDDATA, FULLDOWNLOADEDCARDDATA.MULTIVERSE_ID, FULLDOWNLOADEDCARDDATA.CARD_NAME, FULLDOWNLOADEDCARDDATA.IMAGE_URL)
                .values(cardData.multiverseId(), cardData.cardName(), cardData.imageUrl())
                .execute();

        downloadedImageAddress.thenAccept(imageAddress -> dslContext.insertInto(CARDSIMAGESADDRESSES, CARDSIMAGESADDRESSES.MULTIVERSE_ID, CARDSIMAGESADDRESSES.LOCAL_ADDRESS)
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
