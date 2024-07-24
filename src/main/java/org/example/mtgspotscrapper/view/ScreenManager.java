package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.model.CardList;
import org.example.mtgspotscrapper.model.DatabaseService;

import java.util.Arrays;
import java.util.Collection;

public class ScreenManager {
    private final DatabaseService databaseService;

    ScreenManager(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @SuppressWarnings("unused")
    @FXML
    private void initialize() {
        cardLists.setCursor(Cursor.HAND);
        collection.setCursor(Cursor.HAND);

        borderPane.leftProperty();
        double leftPaneWidth = ((Region) borderPane.getLeft()).getPrefWidth();

        if (borderPane.getPrefWidth() == 0) {
            App.logger.debug("Someone set borderPane width to 0");
            throw new ArithmeticException("Someone set borderPane width to 0");
        }

        final double leftToWholeRatio = leftPaneWidth / borderPane.getPrefWidth();
        borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            ((Region) borderPane.getLeft()).setPrefWidth(newValue.doubleValue()*leftToWholeRatio);
            cardsFlowPane.setPrefWidth(newValue.doubleValue()*leftToWholeRatio);
        });

        cardLists.setOnMouseClicked(mouseEvent -> displayLists());
        collection.setOnMouseClicked(mouseEvent -> displayCollection());
    }

    void displayLists() {
        try {
            cardsFlowPane.getChildren().clear();
            Collection<CardList> cardLists = databaseService.getAllLists();
            for (CardList cardList : cardLists) {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Addresses.LIST_INFO));
                fxmlLoader.setController(new ListItemController(cardList, this));

                cardsFlowPane.getChildren().add(fxmlLoader.load());
            }
        }
        catch (Exception e) {
            App.logger.error(Arrays.toString(e.getStackTrace()));
            displayAlert(e);
        }
    }

    void displayCollection() {
        try {
            cardsFlowPane.getChildren().clear();
        }
        catch (Exception e) {
            App.logger.error(Arrays.toString(e.getStackTrace()));
            displayAlert(e);
        }
    }

    void displayList(CardList cardList) {
        try {
            cardsFlowPane.getChildren().clear();
            for (var cardData : cardList.getCards()) {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Addresses.CARD_INFO));
                fxmlLoader.setController(new CardItemController(cardData, this));

                cardsFlowPane.getChildren().add(fxmlLoader.load());
            }
        }
        catch (Exception e) {
            App.logger.error(Arrays.toString(e.getStackTrace()));
            displayAlert(e);
        }
    }

    public void displayAlert(Exception e) {
        e.printStackTrace();

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

    @SuppressWarnings("unused")
    @FXML
    private Label cardLists;

    @SuppressWarnings("unused")
    @FXML
    private Label collection;

    @SuppressWarnings("unused")
    @FXML
    private BorderPane borderPane;

    @SuppressWarnings("unused")
    @FXML
    private FlowPane cardsFlowPane;
}
