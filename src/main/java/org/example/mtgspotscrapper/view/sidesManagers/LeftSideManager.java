package org.example.mtgspotscrapper.view.sidesManagers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.view.Addresses;
import org.example.mtgspotscrapper.view.cardLogoAndNameImpl.CardItemController;
import org.example.mtgspotscrapper.view.cardLogoAndNameImpl.ListItemController;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public class LeftSideManager {
    private static final Logger log = LoggerFactory.getLogger(LeftSideManager.class);
    private final FlowPane cardsFlowPane;
    private final DatabaseService databaseService;

    private Runnable refreshingRunnable;

    public LeftSideManager(FlowPane cardsFlowPane, DatabaseService databaseService) {
        this.cardsFlowPane = cardsFlowPane;
        this.databaseService = databaseService;

        databaseService.getCurrentlyAddedCardsCounter().addDecrementObserver(state->{
            if (state == 0) {
                refresh();
            }
        });
    }

    public void displayLists() {
        Collection<CardList> cardLists = databaseService.getAllLists();

        Platform.runLater(()->{
            cardsFlowPane.getChildren().clear();
            try {
                for (CardList cardList : cardLists) {
                    if (Objects.equals(cardList.getName(), "Collection")) {
                        continue;
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Addresses.LIST_INFO));
                    fxmlLoader.setController(new ListItemController(cardList));

                    cardsFlowPane.getChildren().add(fxmlLoader.load());
                }
            }
            catch (IOException e) {
                displayAlert(e);
                throw new RuntimeException(e);
            }
        });

        refreshingRunnable = this::displayLists;
    }

    public void displayList(CardList cardList) {
        var cards = cardList.getCards();

        Platform.runLater(() -> {
            cardsFlowPane.getChildren().clear();
            try {
                for (var cardData : cards) {
                    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Addresses.CARD_INFO));
                    fxmlLoader.setController(new CardItemController(cardData));

                    cardsFlowPane.getChildren().add(fxmlLoader.load());
                }
            }
            catch (IOException e) {
                displayAlert(e);
                throw new RuntimeException(e);
            }
        });

        refreshingRunnable = ()-> displayList(cardList);
    }

    public void refresh() {
        refreshingRunnable.run();
    }

    public void displayAlert(Exception e) {
        App.logger.error("Something went wrong {}", e.getMessage(), e);

        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Set the title of the alert
        alert.setTitle("Unsuccessful Operation");

        // Set the header text (null if no header text)
        alert.setHeaderText(null);

        // Set the content text
        alert.setContentText(e.getMessage());

        // Show the alert and wait for the user to respond
        Platform.runLater(alert::showAndWait);
    }
}
