package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SimpleCardList implements CardList {
    private final ListData listData;
    private final Connection connection;

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
        String sql =
            """
                SELECT multiverse_id, previous_price, card_name, null, null, null, image_address
                FROM cards JOIN public.listcards USING(multiverse_id)
                JOIN lists USING (list_id)
                WHERE list_name = ?::varchar;
            """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, listData.name());
        ResultSet resultSet = preparedStatement.executeQuery();

        Collection<Card> answer = new ArrayList<>();
        while (resultSet.next()) {
            answer.add(new SimpleCard(connection,
                    new CardData(resultSet.getInt("multiverse_id"), resultSet.getString("card_name"), 0, 0, null), resultSet.getString("image_address")));
        }
        return answer;
    }

    @Override
    public void addCardToList(Card card) throws SQLException {
//        if (!databaseService.cardIsPresent(card.getCardData().cardName())) {
//            databaseService.addCard(card.getCardData().cardName());
//        }

        String sql =
        """
            INSERT INTO listcards (list_id, multiverse_id) VALUES (?::integer, ?::integer);
        """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, listData.id());
        preparedStatement.setInt(2, card.getCardData().multiverseId());

        preparedStatement.execute();
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
