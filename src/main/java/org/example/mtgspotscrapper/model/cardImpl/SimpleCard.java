package org.example.mtgspotscrapper.model.cardImpl;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.CardPrice;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapper;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapperImpl;
import org.example.mtgspotscrapper.viewmodel.Availability;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.*;

import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

public class SimpleCard implements Card {
    private final DSLContext dslContext;

    private final CardData cardData;

    /*
        TODO: do something with this because it seems like a bad practice
           Cards that share multiverseId should share futureCardPriceWrapper and downloadedImageAddress this is somehow similar to Singleton design pattern
    */

    private static final ConcurrentMap<Integer, CompletableFuture<String>> downloadedImageAddresses = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, FutureCardPriceWrapper> idToPriceWrapperMapping = new ConcurrentHashMap<>();

    private final FutureCardPriceWrapper futureCardPriceWrapper;

    public SimpleCard(CardData cardData, CompletableFuture<String> downloadedImageAddress, DSLContext dslContext) {
        this.dslContext = dslContext;
        this.cardData = cardData;

        if (!downloadedImageAddresses.containsKey(cardData.multiverseId())) {
            downloadedImageAddresses.put(cardData.multiverseId(), downloadedImageAddress);
        }

        if (idToPriceWrapperMapping.containsKey(cardData.multiverseId())) {
            futureCardPriceWrapper = idToPriceWrapperMapping.get(cardData.multiverseId());
        }
        else {
            futureCardPriceWrapper = new FutureCardPriceWrapper();
            idToPriceWrapperMapping.put(cardData.multiverseId(), futureCardPriceWrapper);
        }
    }

    @Override
    public CardData getCardData() {
        return cardData;
    }

    @Override
    public CompletableFuture<String> getDownloadedImageAddress() {
        return downloadedImageAddresses.get(cardData.multiverseId());
    }

    @Override
    public CardPrice getActCardPrice() {
        return Objects.requireNonNull(dslContext.select(CARDS.PREVIOUS_PRICE, CARDS.ACTUAL_PRICE)
                        .from(CARDS)
                        .where(CARDS.MULTIVERSE_ID.eq(cardData.multiverseId()))
                        .fetchOne())
                .map(priceRecord -> new CardPrice(
                        nullsafeBigDecToDouble(priceRecord.getValue(CARDS.PREVIOUS_PRICE)),
                        nullsafeBigDecToDouble(priceRecord.getValue(CARDS.ACTUAL_PRICE))));
    }

    private static double nullsafeBigDecToDouble(BigDecimal bigDecimal) {
        return bigDecimal == null ? 0 : bigDecimal.doubleValue();
    }

    @Override
    public final CompletableFuture<CardPrice> getFutureCardPrice() {
        if (futureCardPriceWrapper.getFuturePrice() == null) {
            futureCardPriceWrapper.setFuturePrice(CompletableFuture.completedFuture(getActCardPrice()));
        }

        return futureCardPriceWrapper.getFuturePrice().thenApply(cardPrice -> cardPrice);
    }

//    TODO: make it possible to add -1 values to database to mark that an exception has occurred
    @Override
//    public CompletableFuture<CardPrice> updatePrice() {
    public void updatePrice() {
        CardInfoScrapper cardInfoScrapper = new CardInfoScrapperImpl();

        futureCardPriceWrapper.setFuturePrice(cardInfoScrapper
                .getCardPrice(cardData.cardName())
                .thenApply(this::updateDatabaseData).exceptionally(throwable ->
                    updateDatabaseData(-1.0)
                )
        );

//        log.debug("Future price: {}, isDone: {}, multiverseId: {}, hash: {}", futureCardPriceWrapper.getFuturePrice(), futureCardPriceWrapper.getFuturePrice().isDone(),
//                cardData.multiverseId(), futureCardPriceWrapper.getFuturePrice().hashCode());
//        return futureCardPrice;
    }

    private CardPrice updateDatabaseData(Double newActCardPrice) {

        final double actCardPrice = getActCardPrice().actPrice();

        dslContext.update(CARDS)
                .set(CARDS.PREVIOUS_PRICE, CARDS.ACTUAL_PRICE)
                .set(CARDS.ACTUAL_PRICE, newActCardPrice != null ? new BigDecimal(newActCardPrice) : null)
                .where(CARDS.MULTIVERSE_ID.eq(cardData.multiverseId()))
                .execute();

        return new CardPrice(actCardPrice, newActCardPrice != null ? newActCardPrice : actCardPrice);
    }

    @Override
    public Availability getAvailability() {
        CardPrice cardPrice = getActCardPrice();

        boolean isActPriceUnknown = cardPrice.actPrice() == 0 || cardPrice.actPrice() == -1;
        boolean isPrevPriceUnknown = cardPrice.prevPrice() == 0 || cardPrice.prevPrice() == -1;

        if (isActPriceUnknown) {
            return isPrevPriceUnknown ? Availability.UNAVAILABLE_PREV_UNAVAILABLE
                    : Availability.AVAILABLE_PREV_AVAILABLE;
        } else {
            return isPrevPriceUnknown ? Availability.AVAILABLE_PREV_UNAVAILABLE
                    : Availability.AVAILABLE_PREV_AVAILABLE;
        }
    }

    @Override
    public String toString() {
        return cardData.toString() + " " + getDownloadedImageAddress();
    }
}

class FutureCardPriceWrapper {
    private CompletableFuture<CardPrice> futurePrice;

    CompletableFuture<CardPrice> getFuturePrice() {
        return futurePrice;
    }

    void setFuturePrice(CompletableFuture<CardPrice> futurePrice) {
        this.futurePrice = futurePrice;
    }
}