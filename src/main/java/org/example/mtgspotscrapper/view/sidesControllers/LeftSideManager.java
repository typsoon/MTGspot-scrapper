package org.example.mtgspotscrapper.view.sidesControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.view.Addresses;
import org.example.mtgspotscrapper.view.cardLogoAndNameImpl.CardItemController;
import org.example.mtgspotscrapper.view.cardLogoAndNameImpl.ListItemController;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public class LeftSideManager {
    private final FlowPane cardsFlowPane;
    private final DatabaseService databaseService;

    public LeftSideManager(FlowPane cardsFlowPane, DatabaseService databaseService) {
        this.cardsFlowPane = cardsFlowPane;
        this.databaseService = databaseService;
    }

    public void displayLists() {
        try {
            cardsFlowPane.getChildren().clear();
            Collection<CardList> cardLists = databaseService.getAllLists();
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
            App.logger.error("Failed to load lists", e);
            displayAlert(e);
        }
    }

    public void displayList(CardList cardList) {
        try {
            cardsFlowPane.getChildren().clear();
            for (var cardData : cardList.getCards()) {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Addresses.CARD_INFO));
                fxmlLoader.setController(new CardItemController(cardData));

                cardsFlowPane.getChildren().add(fxmlLoader.load());
            }
        }
        catch (IOException e) {
            App.logger.error("Failed to load cards", e);
            displayAlert(e);
        }
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
        alert.showAndWait();
    }
}
