package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.ListData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SimpleCardList implements CardList {
    private final ListData listData;
    private final Connection connection;
    private final DatabaseService databaseService;

    public SimpleCardList(ListData listData, Connection connection, DatabaseService databaseService) {
        this.listData = listData;
        this.connection = connection;
        this.databaseService = databaseService;
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
    public String toString() {
        return listData.toString();
    }
}
