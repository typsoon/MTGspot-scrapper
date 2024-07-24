package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.model.mtgapi.MtgApiService;
import org.example.mtgspotscrapper.model.mtgapi.SimpleMtgApiService;
import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.ListData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.sql.*;
import java.util.*;

public class SimpleDatabaseService implements DatabaseService {
    private final Connection connection;

    @Override
    public Collection<CardList> getAllLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
        """
                SELECT list_id, list_name, logo_path FROM lists LEFT JOIN ListsLogos USING (logo_id);
            """
        );

        Collection<CardList> answer = new ArrayList<>();
        while (resultSet.next()) {
            answer.add(new SimpleCardList(new ListData(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)), connection, this));
        }
        return answer;
    }

    @Override
    public CardList getCardList(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
        """
                SELECT list_id, list_name, logo_path FROM lists LEFT JOIN ListsLogos USING (logo_id) WHERE list_name = ?::varchar;
            """
        );
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return new SimpleCardList(new ListData(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)), connection, this);
        }

        return null;
    }

//    TODO: write custom exception to handle errors like "no such card in mtg database"
    @Override
    public Card addCard(String cardName) throws SQLException, IOException {
        MtgApiService mtgApiService = new SimpleMtgApiService();
        CardData cardData = mtgApiService.getCardData(cardName);

        if (cardData == null) {
            throw new RuntimeException("Card not found in MTG database: " + cardName);
        }

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Cards WHERE multiverse_id = ?::integer;");
        preparedStatement.setInt(1, cardData.multiverseId());
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            App.logger.debug(resultSet.getString(1));
            throw new SQLException("This card is already present in the database");
        }

        String downloadedImageAddress = downloadImage(cardData);

        String sql =
        """
            INSERT INTO Cards(multiverse_id, card_name, previous_price, actual_price, image_address) Values (?::integer, ?::varchar, null, null, ?::varchar);
        """;

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, cardData.multiverseId());
        preparedStatement.setString(2, cardData.cardName());
//        preparedStatement.setDouble(3, cardData.prevPrice());
//        preparedStatement.setDouble(4, cardData.actPrice());
        preparedStatement.setString(3, downloadedImageAddress);

        preparedStatement.execute();

        return new SimpleCard(connection, cardData, downloadedImageAddress);
    }

    @Override
    public Collection<Card> getAllCardsData() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM cards");

        return resultSetToCardData(resultSet);
    }

    private Collection<Card> resultSetToCardData(ResultSet resultSet) throws SQLException {
        Collection<Card> answer = new ArrayList<>();
        while (resultSet.next()) {
            answer.add(new SimpleCard(connection,
                    new CardData(resultSet.getInt("multiverse_id"), resultSet.getString("card_name"), 0, 0, null), resultSet.getString("image_address")));
        }
        return answer;
    }

    public SimpleDatabaseService(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

//    TODO: think whether I should separate these 2 functions from SimpleDatabaseService
    protected String downloadImage(CardData cardData) throws IOException {
        URL imageURL;
        try {
            imageURL = new URI(cardData.imageUrl()).toURL();
        }
        catch (URISyntaxException | MalformedURLException e){
            throw new RuntimeException("Inconsistent data in database:", e);
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) imageURL.openConnection();

        int responseCode = httpURLConnection.getResponseCode();
        while (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
            String newUrl = httpURLConnection.getHeaderField("Location");
            httpURLConnection.disconnect();

            try {
                imageURL = new URI(newUrl).toURL();
            }
            catch (Exception e) {
                App.logger.error("Problems on wizards side: {}", Arrays.toString(e.getStackTrace()));
                throw new RuntimeException("Problems on wizards side:", e);
            }

            httpURLConnection = (HttpURLConnection) imageURL.openConnection();
            responseCode = httpURLConnection.getResponseCode();
        }

        // Determine file extension from content type
        String contentType = httpURLConnection.getContentType();
        String fileExtension = getFileExtension(contentType);
        if (fileExtension == null) {
            throw new IOException("Unsupported content type: " + contentType);
        }

        // Create directories if not exist
        File directory = new File("downloaded images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create file
        File file = new File(directory, "card"+cardData.multiverseId() + "." + fileExtension);

        // Download and save the image
        try (InputStream inputStream = httpURLConnection.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
//                file.createNewFile();

                ImageIO.write(image, fileExtension, file);
//                App.logger.debug("Image successfully saved: {}", file.getAbsolutePath());
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
//            case "image/jpeg" -> "jpeg";
//            case "image/png" -> "png";
            case "image/jpeg", "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> null;
        };
    }

    public static void main(String[] args) throws SQLException, IOException {
        DatabaseService databaseService = new SimpleDatabaseService("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa");

        App.logger.debug(databaseService.getAllLists().toString());
        App.logger.debug(databaseService.getAllCardsData().toString());
        App.logger.debug(databaseService.getCardList("test list").toString());
//            props.setProperty("ssl", "true");

        App.logger.debug(databaseService.addCard("Kodama's reach").toString());
        App.logger.debug(databaseService.addCard("Swords to Plowshares").toString());
        App.logger.debug(databaseService.addCard("Kenrith, the Returned King").toString());
    }
}
