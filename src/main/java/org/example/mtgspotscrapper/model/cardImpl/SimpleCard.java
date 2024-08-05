package org.example.mtgspotscrapper.model.cardImpl;

import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapper;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapperImpl;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.jooq.DSLContext;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.*;

import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

public class SimpleCard implements Card {
    private final DSLContext dslContext;
    private final CardData cardData;
    private final CompletableFuture<String> downloadedImageAddress;

    private CardPrice cachedActPrice;
    private CompletableFuture<CardPrice> futureCardPrice;

    SimpleCard(CardData cardData, CompletableFuture<String> downloadedImageAddress, DSLContext dslContext) {
        this.dslContext = dslContext;
        this.cardData = cardData;
        this.downloadedImageAddress = downloadedImageAddress;
    }

    @Override
    public CardData getCardData() {
        return cardData;
    }

    @Override
    public CompletableFuture<String> getDownloadedImageAddress() {
        return downloadedImageAddress;
    }

    @Override
    public CardPrice getActCardPrice() {
        if (futureCardPrice != null && futureCardPrice.isDone()) {
            return futureCardPrice.resultNow();
        }

        if (cachedActPrice == null) {
            cachedActPrice = Objects.requireNonNull(dslContext.select(FULLDOWNLOADEDCARDDATA.PREVIOUS_PRICE, FULLDOWNLOADEDCARDDATA.ACTUAL_PRICE)
                            .from(FULLDOWNLOADEDCARDDATA)
                            .where(FULLDOWNLOADEDCARDDATA.MULTIVERSE_ID.eq(cardData.multiverseId()))
                            .fetchOne())
                    .map(priceRecord -> new CardPrice(
                            nullsafeBigDecToDouble(priceRecord.getValue(FULLDOWNLOADEDCARDDATA.PREVIOUS_PRICE)),
                            nullsafeBigDecToDouble(priceRecord.getValue(FULLDOWNLOADEDCARDDATA.ACTUAL_PRICE))));
        }

        return cachedActPrice;
    }

    private static double nullsafeBigDecToDouble(BigDecimal bigDecimal) {
        return bigDecimal == null ? 0 : bigDecimal.doubleValue();
    }

    @Override
    public final CompletableFuture<CardPrice> getFutureCardPrice() {
        if (futureCardPrice == null) {
            futureCardPrice = CompletableFuture.completedFuture(getActCardPrice());
        }

        return futureCardPrice.thenApply(cardPrice -> cardPrice);
    }

//    TODO: make it possible to add -1 values to database to mark that an exception has occurred
    @Override
//    public CompletableFuture<CardPrice> updatePrice() {
    public void updatePrice() {
        CardInfoScrapper cardInfoScrapper = new CardInfoScrapperImpl();

        futureCardPrice = cardInfoScrapper
                .getCardPrice(cardData.cardName())
                .thenApply(this::updateDatabaseData)
                .exceptionally(throwable ->
                    updateDatabaseData(-1.0));

        futureCardPrice.thenApply(cardPrice -> cachedActPrice = cardPrice);
//        log.debug("Future price: {}, isDone: {}, multiverseId: {}, hash: {}", futureCardPriceWrapper.getFuturePrice(), futureCardPriceWrapper.getFuturePrice().isDone(),
//                cardData.multiverseId(), futureCardPriceWrapper.getFuturePrice().hashCode());
//        return futureCardPrice;
    }

    private CardPrice updateDatabaseData(Double newActCardPrice) {

        final double actCardPrice = getActCardPrice().actPrice();

        dslContext.update(CARDSWITHPRICES)
                .set(CARDSWITHPRICES.PREVIOUS_PRICE, CARDSWITHPRICES.ACTUAL_PRICE)
                .set(CARDSWITHPRICES.ACTUAL_PRICE, newActCardPrice != null ? new BigDecimal(newActCardPrice) : null)
                .where(CARDSWITHPRICES.MULTIVERSE_ID.eq(cardData.multiverseId()))
                .execute();

        return new CardPrice(actCardPrice, newActCardPrice != null ? newActCardPrice : actCardPrice);
    }

    @Override
    public Availability getAvailability() {
        return getActCardPrice().getAvailability();
    }

    @Override
    public String toString() {
        return cardData.toString() + " " + getDownloadedImageAddress();
    }
}