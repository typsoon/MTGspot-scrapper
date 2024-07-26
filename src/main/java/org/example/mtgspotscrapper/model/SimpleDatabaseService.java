package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.mtgapi.MtgApiService;
import org.example.mtgspotscrapper.model.mtgapi.SimpleMtgApiService;
import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.sql.*;
import java.util.*;

public class SimpleDatabaseService implements DatabaseService {
    private static final Logger log = LoggerFactory.getLogger(SimpleDatabaseService.class);
    private final Connection connection;

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
        CardData cardData = mtgApiService.getCardData(cardName);

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

        String downloadedImageAddress = downloadImage(cardData);

        String insertCardSql = """
            INSERT INTO Cards(multiverse_id, card_name, previous_price, actual_price, image_address)
            VALUES (?::integer, ?::varchar, null, null, ?::varchar);
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCardSql)) {
            preparedStatement.setInt(1, cardData.multiverseId());
            preparedStatement.setString(2, cardData.cardName());
            preparedStatement.setString(3, downloadedImageAddress);
            preparedStatement.execute();
        }

        return new SimpleCard(connection, cardData, downloadedImageAddress);
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
        String sql =
        """
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

        sql =
        """
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
        String sql = """
            SELECT * FROM Cards WHERE card_name = ?::varchar;
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cardName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    CardData cardData = new CardData(resultSet.getInt("multiverse_id"),
                            resultSet.getString("card_name"),
                            resultSet.getDouble("previous_price"),
                            resultSet.getDouble("actual_price"),
                            null);
                    return new SimpleCard(connection, cardData, resultSet.getString("image_address"));
                }
                else return null;
            }
        }
    }

    @Override
    public Collection<Card> getAllCardsData() throws SQLException {
        String sql = "SELECT * FROM cards";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSetToCardData(resultSet);
        }
    }

    private Collection<Card> resultSetToCardData(ResultSet resultSet) throws SQLException {
        Collection<Card> answer = new ArrayList<>();
        while (resultSet.next()) {
            answer.add(new SimpleCard(connection,
                    new CardData(resultSet.getInt("multiverse_id"), resultSet.getString("card_name"), 0, 0, null),
                    resultSet.getString("image_address")));
        }
        return answer;
    }

    public SimpleDatabaseService(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    protected String downloadImage(CardData cardData) throws IOException {
        URL imageURL;
        try {
            imageURL = new URI(cardData.imageUrl()).toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException("Inconsistent data in database:", e);
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) imageURL.openConnection();
        int responseCode = httpURLConnection.getResponseCode();
        while (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
            String newUrl = httpURLConnection.getHeaderField("Location");
            httpURLConnection.disconnect();
            try {
                imageURL = new URI(newUrl).toURL();
            } catch (Exception e) {
                log.error("Problems on wizards side: {}", Arrays.toString(e.getStackTrace()));
                throw new RuntimeException("Problems on wizards side:", e);
            }
            httpURLConnection = (HttpURLConnection) imageURL.openConnection();
            responseCode = httpURLConnection.getResponseCode();
        }

        String contentType = httpURLConnection.getContentType();
        String fileExtension = getFileExtension(contentType);
        if (fileExtension == null) {
            throw new IOException("Unsupported content type: " + contentType);
        }

        File directory = new File("downloaded images");
        if (!directory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdirs();
        }

        File file = new File(directory, "card" + cardData.multiverseId() + "." + fileExtension);

        try (InputStream inputStream = httpURLConnection.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                ImageIO.write(image, fileExtension, file);
                return file.getAbsolutePath();
            } else {
                throw new IOException("Failed to load image from URL: " + imageURL);
            }
        } finally {
            httpURLConnection.disconnect();
        }
    }

    private static String getFileExtension(String contentType) {
        if (contentType == null) return null;
        return switch (contentType) {
            case "image/jpeg", "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> null;
        };
    }

    public static void main(String[] args) {
        try {
            DatabaseService databaseService = new SimpleDatabaseService("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa");
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
