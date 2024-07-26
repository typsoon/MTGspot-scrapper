package org.example.mtgspotscrapper.view.cardLogoAndNameImpl;

import javafx.scene.image.Image;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.view.CardLogoAndNameController;
import org.example.mtgspotscrapper.view.ScreenManager;
import org.example.mtgspotscrapper.viewmodel.Card;

import java.io.IOException;
import java.net.*;

public class CardItemController extends CardLogoAndNameController {
    private final Card cardData;

    public CardItemController(Card cardData, ScreenManager screenManager) {
        super(screenManager);
        this.cardData = cardData;
    }

    @Override
    protected void initialize() {
        imageView.setImage(new Image("file:" + cardData.getLocalImageAddress()));
    }



    public static void main(String[] args) throws IOException, URISyntaxException {
//        URL url = new URL("https://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=600&type=card");
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
