package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.cardImpl.SimpleCard;
import org.example.mtgspotscrapper.model.mtgapi.MtgApiService;
import org.example.mtgspotscrapper.model.mtgapi.SimpleMtgApiService;
import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.model.utils.ResultProcessor;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.DownloaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleDatabaseService implements DatabaseService {
    private static final Logger log = LoggerFactory.getLogger(SimpleDatabaseService.class);
    private final Connection connection;
    private final DownloaderService downloaderService;
    private final ResultProcessor resultProcessor = new ResultProcessor();
    private final ConcurrentMap<String, Card> loadedCards = new ConcurrentHashMap<>();

    @Override
    public Collection<CardList> getAllLists() throws SQLException {
        String sql = """
                SELECT list_id, list_name, logo_path FROM lists LEFT JOIN ListsLogos USING (logo_id);
            """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            Collection<CardList> answer = new ArrayList<>();
            while (resultSet.next()) {
                answer.add(new SimpleCardList(new ListData(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)), connection));
            }
            return answer;
        }
    }

    @Override
    public CardList getCardList(String name) throws SQLException {
        String sql = """
                SELECT list_id, list_name, logo_path FROM lists LEFT JOIN ListsLogos USING (logo_id) WHERE list_name = ?::varchar;
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new SimpleCardList(new ListData(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)), connection);
                }
            }
        }
        return null;
    }

    @Override
    public Card addCard(String cardName) throws SQLException, IOException {
        MtgApiService mtgApiService = new SimpleMtgApiService();

        log.debug("Before pinging api");
        CardData cardData = mtgApiService.getCardData(cardName);
        log.debug("After pinging api");

        if (cardData == null) {
            throw new RuntimeException("Card not found in MTG database: " + cardName);
        }

        String checkCardSql = "SELECT * FROM Cards WHERE multiverse_id = ?::integer;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkCardSql)) {
            preparedStatement.setInt(1, cardData.multiverseId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    throw new SQLException("This card is already present in the database");
                }
            }
        }

//        TODO: make it so the download is handled by another thread
        CompletableFuture<String> downloadedImageAddress;
        try {
            downloadedImageAddress = downloaderService.downloadCardImage(new URI(cardData.imageUrl()).toURL(), cardData.multiverseId());
        } catch (URISyntaxException e) {
            log.error("Inconsistent data in database: ", e);
            throw new RuntimeException(e);
        }

        String insertCardSql = """
            INSERT INTO Cards(multiverse_id, card_name, image_url)
            VALUES (?::integer, ?::varchar, ?::varchar);
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCardSql)) {
            preparedStatement.setInt(1, cardData.multiverseId());
            preparedStatement.setString(2, cardData.cardName());
            preparedStatement.setString(3, cardData.imageUrl());
            if (preparedStatement.executeUpdate() == 0)
                throw new SQLException("Failed to insert card");
        }

        downloadedImageAddress.thenAcceptAsync(imageAddress -> {
            String sql = """
                INSERT INTO LocalAddresses(multiverse_id, local_address) VALUES (?::integer, ?::varchar);
            """;

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, cardData.multiverseId());
                preparedStatement.setString(2, imageAddress);

                if (preparedStatement.executeUpdate() == 0)
                    throw new RuntimeException("Failed to insert local address");
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(throwable -> {
            log.error("Failed to insert card", throwable);
            throw new RuntimeException("Failed to insert card", throwable);
        });

        Card adedCard = new SimpleCard(connection, cardData, downloadedImageAddress);
        loadedCards.put(cardName, adedCard);
        return adedCard;
    }

    @Override
    public CardList addList(String listName) throws SQLException {
        String sql = """
            INSERT INTO Lists(list_name) VALUES (?::varchar) RETURNING list_id;
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, listName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int listId = resultSet.getInt("list_id");
                    return new SimpleCardList(new ListData(listId, listName, null), connection);
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteList(String listName) throws SQLException {
        String sql = """
            DELETE FROM Lists WHERE list_name = ?::varchar RETURNING list_id;
        """;

        int listId = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, listName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    listId = resultSet.getInt(1);
                }
            }
        }

        if (listId == -1) {
            return false;
        }

        sql = """
            DELETE FROM ListCards WHERE list_id = ?::integer;
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, listId);

            preparedStatement.execute();
        }

        return true;
    }

    @Override
    public Card getCard(String cardName) throws SQLException {
        if (loadedCards.containsKey(cardName)) {
            return loadedCards.get(cardName);
        }

        String sql = """
            SELECT * FROM FullCardData WHERE card_name = ?::varchar;
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cardName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Collection<Card> cards = resultProcessor.getCardsFromResultSet(resultSet, connection);
                if (cards.size() > 1) {
                    throw new RuntimeException("Inconsistent data in database, collection of cards with card_name ==  " + cardName + ":" + cards);
                }
                return cards.isEmpty() ? null : cards.iterator().next();
            }
        }
    }

    @Override
    public Collection<Card> getAllCardsData() throws SQLException {
        String sql = "SELECT * FROM Cards";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultProcessor.getCardsFromResultSet(resultSet, connection);
        }
    }

    public SimpleDatabaseService(String url, String user, String password, DownloaderService downloaderService) throws SQLException {
        this.downloaderService = downloaderService;
        this.connection = DriverManager.getConnection(url, user, password);
    }


    public static void main(String[] args) {
        try {
            DatabaseService databaseService = new SimpleDatabaseService("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa", new SimpleDownloaderService());
            log.debug(databaseService.getAllLists().toString());
            log.debug(databaseService.getAllCardsData().toString());
            log.debug(databaseService.getCardList("test list").toString());
            log.debug(databaseService.addCard("Kodama's Reach").toString());
            log.debug(databaseService.addCard("Swords to Plowshares").toString());
            log.debug(databaseService.addCard("Kenrith, the Returned King").toString());
            log.debug(databaseService.addCard("Path to Exile").toString());
        } catch (SQLException | IOException e) {
            log.error("Something went wrong", e);
        }
    }
}
