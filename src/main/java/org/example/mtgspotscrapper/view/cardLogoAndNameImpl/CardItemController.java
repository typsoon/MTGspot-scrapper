package org.example.mtgspotscrapper.view.cardLogoAndNameImpl;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.example.mtgspotscrapper.model.records.CardPrice;
import org.example.mtgspotscrapper.view.CardLogoAndNameController;
import org.example.mtgspotscrapper.view.ScreenManager;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.DownloaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CardItemController extends CardLogoAndNameController {
    private static final Logger log = LoggerFactory.getLogger(CardItemController.class);
    private final Card card;

    @SuppressWarnings("unused")
    @FXML
    private Label prevPrice;

    public CardItemController(Card card, ScreenManager screenManager, DownloaderService downloaderService) {
        super(screenManager, downloaderService);
        this.card = card;
    }

    @Override
    protected void initialize() {
//        log.debug("before");
        try {
            card.getDownloadedImageAddress().thenAcceptAsync(imageAddress -> {
                if (imageAddress != null) {
                    String imagePath = "file:" + imageAddress;
//                    log.debug("Image path: {}", "file:" + imageAddress);
                    // Ensure UI updates happen on the JavaFX Application Thread
                    Platform.runLater(() -> imageView.setImage(new Image(imagePath)));
                } else {
                    log.error("Image address is null. Failed to download image.");
                }
            }).exceptionally(ex -> {
                log.error("Error loading image", ex);
                return null;
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
//        log.debug("after");

        try {
            displayPrice(card.getActCardPrice());

            log.debug("Is done: {}, multiverseId: {}, hash: {}", card.getFutureCardPrice().isDone(), card.getCardData().multiverseId(), card.getFutureCardPrice().hashCode());
            if (!card.getFutureCardPrice().isDone()) {
                card.getFutureCardPrice()
                        .thenComposeAsync(cardPrice -> {
                            // Display the card price on the JavaFX Application Thread
                            Platform.runLater(() -> {
                                try {
                                    displayPrice(cardPrice);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            return CompletableFuture.completedFuture(null); // Return a completed future to continue the chain
                        })
                        .exceptionally(throwable -> {
                            log.error("Error loading price", throwable);
                            throw new RuntimeException(throwable);
                        });
            }
        } catch (SQLException e) {
            log.error("Couldn't query database", e);
            throw new RuntimeException(e);
        }
    }

    final void displayPrice(CardPrice cardPrice) throws SQLException {
        final Label actPrice = label;

        prevPrice.setText(cardPrice.prevPrice() != 0.0 ? String.valueOf(cardPrice.prevPrice()) : "-" );
        actPrice.setText(cardPrice.actPrice() != 0.0 ? String.valueOf(cardPrice.actPrice()) : "-" );

        vBox.setBackground(new Background(new BackgroundFill(
                switch (card.getAvailability()) {
                    case AVAILABLE_PREV_UNAVAILABLE -> Color.LIGHTGREEN;
                    case AVAILABLE_PREV_AVAILABLE -> Color.LIGHTYELLOW;
                    case UNAVAILABLE_PREV_AVAILABLE, UNAVAILABLE_PREV_UNAVAILABLE -> Color.rgb(255, 192,192);
                }
                , null, null)));
    }
}
