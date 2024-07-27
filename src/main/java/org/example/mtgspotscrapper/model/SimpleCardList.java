package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.model.utils.ResultProcessor;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class SimpleCardList implements CardList {
    private final ListData listData;
    private final Connection connection;
    private final ResultProcessor resultProcessor = new ResultProcessor();

    public SimpleCardList(ListData listData, Connection connection) {
        this.listData = listData;
        this.connection = connection;
    }

    @Override
    public String getName() {
        return listData.name();
    }

    @Override
    public String getLogoPath() {
        return listData.logoPath();
    }

    @Override
    public Collection<Card> getCards() throws SQLException {
        String sql = """
                SELECT multiverse_id, previous_price, card_name, image_url, local_address
                FROM fullcarddata JOIN public.listcards USING(multiverse_id)
                JOIN lists USING (list_id)
                WHERE list_name = ?::varchar;
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, listData.name());
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                return resultProcessor.getCardsFromResultSet(resultSet, connection);
            }
        }
    }

    @Override
    public void addCardToList(Card card) {
//        if (!databaseService.cardIsPresent(card.getCardData().cardName())) {
//            databaseService.addCard(card.getCardData().cardName());
//        }
        if (card == null) {
            return;
        }

        String sql = """
            INSERT INTO listcards (list_id, multiverse_id) VALUES (?::integer, ?::integer);
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, listData.id());
            preparedStatement.setInt(2, card.getCardData().multiverseId());

            preparedStatement.execute();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteCardFromList(String cardName) throws SQLException {
        String sql = """
            SELECT multiverse_id FROM cards WHERE card_name = ?::varchar;
        """;

        int cardId = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cardName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    cardId = resultSet.getInt("multiverse_id");
                }
            }
        }

        sql = """
            DELETE FROM listcards WHERE multiverse_id = ?::integer;
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cardId);

            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public String toString() {
        return listData.toString();
    }
}
