package org.example.mtgspotscrapper.model.cardImpl;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.CardPrice;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapper;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapperImpl;
import org.example.mtgspotscrapper.viewmodel.Availability;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.*;

public class SimpleCard implements Card {
    private static final Logger log = LoggerFactory.getLogger(SimpleCard.class);
    private final Connection connection;

    private final CardData cardData;
//    private CompletableFuture<CardPrice> futureCardPrice;

    //    TODO: do something with this because it seems like a bad practice
    //   Cards that share multiverseId should share futureCardPriceWrapper and downloadedImageAddress this is somehow similar to Singleton design pattern
    private static final ConcurrentMap<Integer, FutureCardPriceWrapper> idToPriceWrapperMapping = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, CompletableFuture<String>> downloadedImageAddresses = new ConcurrentHashMap<>();

    private final FutureCardPriceWrapper futureCardPriceWrapper;

    public SimpleCard(Connection connection, CardData cardData, CompletableFuture<String> downloadedImageAddress) {
        this.connection = connection;
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
    public CardPrice getActCardPrice() throws SQLException {
        String sql = """
            SELECT previous_price, actual_price FROM cards WHERE multiverse_id = ?::integer;
        """;

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cardData.multiverseId());

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return new CardPrice(resultSet.getDouble("previous_price"), resultSet.getDouble("actual_price"));
                }
                else {
                    throw new RuntimeException("No price found for multiverseId " + cardData.multiverseId());
                }
            }
        }
    }

    @Override
    public final CompletableFuture<CardPrice> getFutureCardPrice() throws SQLException {
        if (futureCardPriceWrapper.getFuturePrice() == null) {
            futureCardPriceWrapper.setFuturePrice(CompletableFuture.completedFuture(getActCardPrice()));
        }

        return futureCardPriceWrapper.getFuturePrice().thenApply(cardPrice -> cardPrice);
//        return futureCardPriceWrapper.getFuturePrice();
    }

    @Override
//    public CompletableFuture<CardPrice> updatePrice() {
    public void updatePrice() {
        CardInfoScrapper cardInfoScrapper = new CardInfoScrapperImpl();

        futureCardPriceWrapper.setFuturePrice(cardInfoScrapper
                .getCardPrice(cardData.cardName())
                .thenApply(futureActCardPrice -> {
                    String sql = """
                        UPDATE cards SET previous_price = actual_price, actual_price = ?::numeric(4,2)
                        WHERE multiverse_id = ?::integer;
                    """;

                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        final double actCardPrice = getActCardPrice().actPrice();

                        if (futureActCardPrice != 0) {
                            preparedStatement.setDouble(1, futureActCardPrice);
                        } else {
                            preparedStatement.setNull(1, Types.NUMERIC);
                        }

                        preparedStatement.setInt(2, cardData.multiverseId());
                        preparedStatement.executeUpdate();

                        log.debug("New card price: {}", futureActCardPrice);
                        return new CardPrice(actCardPrice, futureActCardPrice);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
        );

        log.debug("Future price: {}, isDone: {}, multiverseId: {}, hash: {}", futureCardPriceWrapper.getFuturePrice(), futureCardPriceWrapper.getFuturePrice().isDone(),
                cardData.multiverseId(), futureCardPriceWrapper.getFuturePrice().hashCode());
//        return futureCardPrice;
    }

    @Override
    public Availability getAvailability() throws SQLException {
        CardPrice cardPrice = getActCardPrice();

        boolean isActPriceZero = cardPrice.actPrice() == 0;
        boolean isPrevPriceZero = cardPrice.prevPrice() == 0;

        if (isActPriceZero) {
            return isPrevPriceZero ? Availability.UNAVAILABLE_PREV_UNAVAILABLE
                    : Availability.AVAILABLE_PREV_AVAILABLE;
        } else {
            return isPrevPriceZero ? Availability.AVAILABLE_PREV_UNAVAILABLE
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

    public CompletableFuture<CardPrice> getFuturePrice() {
        return futurePrice;
    }

    public void setFuturePrice(CompletableFuture<CardPrice> futurePrice) {
        this.futurePrice = futurePrice;
    }
}