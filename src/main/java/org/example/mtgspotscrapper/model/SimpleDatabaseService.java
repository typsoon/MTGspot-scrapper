package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.mtgapi.ImportantCardData;
import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.model.records.PrevPriceAndCardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class SimpleDatabaseService implements DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(SimpleDatabaseService.class);

    private final Connection connection;

    @Override
    public Collection<ListData> getListsNames() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM lists");

        Collection<ListData> answer = new ArrayList<>();
        while (resultSet.next()) {
            answer.add(new ListData(resultSet.getInt(1), resultSet.getString(2)));
        }
        return answer;
    }

    @Override
    public Collection<PrevPriceAndCardData> getCardData(String listName) throws SQLException {
        String sql =
        """
            SELECT Cards.previous_price, Cards.card_name, null, null, null, Cards.image_url
            FROM cards JOIN public.listcards USING(card_id)
            JOIN lists USING (list_id)
            WHERE list_name = ?::varchar;
        """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, listName);
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSetToCardData(resultSet);
    }

    @Override
    public Collection<PrevPriceAndCardData> getAllCardData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM cards");

        return resultSetToCardData(resultSet);
    }

    private Collection<PrevPriceAndCardData> resultSetToCardData(ResultSet resultSet) throws SQLException {
        Collection<PrevPriceAndCardData> answer = new ArrayList<>();
        while (resultSet.next()) {
            answer.add(new PrevPriceAndCardData(resultSet.getDouble("previous_price"),
                    new ImportantCardData(resultSet.getString("card_name"), 0, null, null, null, null, resultSet.getString("image_url"))));
        }
        return answer;
    }

    public SimpleDatabaseService(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) throws SQLException {
        DatabaseService databaseService = new SimpleDatabaseService("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa");

        logger.debug(databaseService.getListsNames().toString());
        logger.debug(databaseService.getAllCardData().toString());
        logger.debug(databaseService.getCardData("test list").toString());
//            props.setProperty("ssl", "true");
    }
}
