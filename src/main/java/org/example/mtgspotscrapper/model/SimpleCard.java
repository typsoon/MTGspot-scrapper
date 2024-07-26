package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.CardPrice;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapper;
import org.example.mtgspotscrapper.model.scrapper.CardInfoScrapperImpl;
import org.example.mtgspotscrapper.viewmodel.Availability;
import org.example.mtgspotscrapper.viewmodel.Card;

import java.sql.*;

public class SimpleCard implements Card {
    private final Connection connection;

    private final CardData cardData;
    private final String localImageAddress;

    public SimpleCard(Connection connection, CardData cardData, String localImageAddress) {
        this.connection = connection;
        this.cardData = cardData;
        this.localImageAddress = localImageAddress;
    }

    @Override
    public CardData getCardData() {
        return cardData;
    }

    @Override
    public String getLocalImageAddress() {
        return localImageAddress;
    }

    @Override
    public CardPrice getCardPrice() throws SQLException {
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
    public boolean updatePrice() throws SQLException {
        CardInfoScrapper cardInfoScrapper = new CardInfoScrapperImpl();

        Double actPrice = cardInfoScrapper.getCardPrice(cardData.cardName());
        if (actPrice == null) {
            actPrice = 0.0;
        }

        String sql = """
            UPDATE cards SET previous_price = actual_price, actual_price = ?::numeric(4,2)
            WHERE multiverse_id = ?::integer;
       """;

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (actPrice != 0.0) {
                preparedStatement.setDouble(1, actPrice);
            }
            else preparedStatement.setNull(1, Types.NUMERIC);

            preparedStatement.setInt(2, cardData.multiverseId());

            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public Availability getAvailability() throws SQLException {
        CardPrice cardPrice = getCardPrice();

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
        return cardData.toString() + " " + localImageAddress;
    }
}