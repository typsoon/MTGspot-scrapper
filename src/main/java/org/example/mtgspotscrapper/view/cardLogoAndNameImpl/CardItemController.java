package org.example.mtgspotscrapper.view.cardLogoAndNameImpl;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.model.records.CardPrice;
import org.example.mtgspotscrapper.view.CardLogoAndNameController;
import org.example.mtgspotscrapper.view.ScreenManager;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;

public class CardItemController extends CardLogoAndNameController {
    private static final Logger log = LoggerFactory.getLogger(CardItemController.class);
    private final Card card;

    @SuppressWarnings("unused")
    @FXML
    private Label prevPrice;

    public CardItemController(Card card, ScreenManager screenManager) {
        super(screenManager);
        this.card = card;
    }

    @Override
    protected void initialize() {
        imageView.setImage(new Image("file:" + card.getLocalImageAddress()));

        Label actPrice = label;

        App.logger.debug("Card: {}", card);

        try {
            CardPrice cardPrice = card.getCardPrice();

            prevPrice.setText(cardPrice.prevPrice() != 0.0 ? String.valueOf(cardPrice.prevPrice()) : "-" );
            actPrice.setText(cardPrice.actPrice() != 0.0 ? String.valueOf(cardPrice.actPrice()) : "-" );

            vBox.setBackground(new Background(new BackgroundFill(
                    switch (card.getAvailability()) {
                        case AVAILABLE_PREV_UNAVAILABLE -> Color.LIGHTGREEN;
                        case AVAILABLE_PREV_AVAILABLE -> Color.LIGHTYELLOW;
                        case UNAVAILABLE_PREV_AVAILABLE, UNAVAILABLE_PREV_UNAVAILABLE -> Color.rgb(255, 192,192);
                    }
                    , null, null)));
        } catch (SQLException e) {
            log.error("Couldn't query database", e);
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) throws IOException, URISyntaxException {
        URL url = new URI("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=129626&type=card").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setInstanceFollowRedirects(true);

        while (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
            String newUrl = connection.getHeaderField("Location");
            connection.disconnect();
            url = new URI(newUrl).toURL();
            connection = (HttpURLConnection) url.openConnection();
        }

        App.logger.debug(connection.getResponseMessage());
        App.logger.debug(connection.getContentType());
    }
}
