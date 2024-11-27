package org.example.mtgspotscrapper.model.cardImpl;

import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapper;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapperImpl;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.jooq.DSLContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.*;

import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

public class SimpleCard implements Card {
    private static final Logger log = LoggerFactory.getLogger(SimpleCard.class);
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
        if (cachedActPrice != null) {
            return cachedActPrice;
        }

        cachedActPrice = Objects.requireNonNull(dslContext.select(FULLDOWNLOADEDCARDDATA.PREVIOUS_PRICE, FULLDOWNLOADEDCARDDATA.ACTUAL_PRICE)
                        .from(FULLDOWNLOADEDCARDDATA)
                        .where(FULLDOWNLOADEDCARDDATA.MULTIVERSE_ID.eq(cardData.multiverseId()))
                        .fetchOne())
                .map(priceRecord -> new CardPrice(
                        nullSafeBigDecToDouble(priceRecord.getValue(FULLDOWNLOADEDCARDDATA.PREVIOUS_PRICE)),
                        nullSafeBigDecToDouble(priceRecord.getValue(FULLDOWNLOADEDCARDDATA.ACTUAL_PRICE))));

        return cachedActPrice;
    }

    private static double nullSafeBigDecToDouble(BigDecimal bigDecimal) {
        return bigDecimal == null ? 0 : bigDecimal.doubleValue();
    }

    @Override
    public synchronized final CompletableFuture<CardPrice> getFutureCardPrice() {
        if (futureCardPrice == null) {
            futureCardPrice = CompletableFuture.completedFuture(getActCardPrice());
        }

        return futureCardPrice.thenApply(cardPrice -> cardPrice);
    }

    @Override
//    public CompletableFuture<CardPrice> updatePrice() {
    public synchronized void updatePrice() {
        CardInfoScrapper cardInfoScrapper = new CardInfoScrapperImpl();

        futureCardPrice = cardInfoScrapper
                .getCardPrice(cardData.cardName())
                .thenApply(this::updateDatabaseData)
                .exceptionally(throwable -> {
                        try {
                            return updateDatabaseData(-1.0);
                        }
                        finally {
                            switch (throwable) {
                                case TimeoutException ignored -> log.error("Timeout while scrapping {} data: {}", cardData.cardName(), throwable.getMessage());
                                case StaleElementReferenceException ignored -> log.error("Error while scrapping {} data: {}", cardData.cardName(), throwable.getMessage());
                                default -> log.error("Error while scrapping data: ", throwable);
                            }
                            log.error("Error while scrapping data: ", throwable);
                        }
                    }
                );

        futureCardPrice.thenApply(cardPrice -> cachedActPrice = cardPrice);
//        log.debug("Future price: {}, isDone: {}, multiverseId: {}, hash: {}", futureCardPriceWrapper.getFuturePrice(), futureCardPriceWrapper.getFuturePrice().isDone(),
//                cardData.multiverseId(), futureCardPriceWrapper.getFuturePrice().hashCode());
//        return futureCardPrice;
    }

    private CardPrice updateDatabaseData(Double newActCardPrice) {

        final double actCardPrice = getActCardPrice().actPrice();
        if (newActCardPrice == null) {
            dslContext.update(CARDSWITHPRICES)
                    .setNull(CARDSWITHPRICES.ACTUAL_PRICE)
                    .where(CARDSWITHPRICES.MULTIVERSE_ID.eq(cardData.multiverseId()))
                    .execute();
        }
        else {
            dslContext.update(CARDSWITHPRICES)
                    .set(CARDSWITHPRICES.ACTUAL_PRICE, new BigDecimal(newActCardPrice))
                    .where(CARDSWITHPRICES.MULTIVERSE_ID.eq(cardData.multiverseId()))
                    .execute();
        }
        //        Updating the previous_price is handled by psql rule

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