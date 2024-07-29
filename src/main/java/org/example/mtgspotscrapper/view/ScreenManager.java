package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.view.cardLogoAndNameImpl.CardItemController;
import org.example.mtgspotscrapper.view.cardLogoAndNameImpl.ListItemController;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.*;
import org.example.mtgspotscrapper.view.rightPanesImplementations.CardRightPane;
import org.example.mtgspotscrapper.view.rightPanesImplementations.ListRightPane;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.eventHandling.handlers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class ScreenManager {
    private final DatabaseService databaseService;

    private final CardRightPane cardRightPaneController;
    private final ListRightPane listRightPaneController;

    ScreenManager(DatabaseService databaseService) throws IOException {
        this.databaseService = databaseService;
        listRightPaneController = new ListRightPane(Addresses.LIST_RIGHT_PANE, databaseService, this);
        cardRightPaneController = new CardRightPane(Addresses.CARD_RIGHT_PANE, databaseService, this);
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

        borderPane.addEventHandler(AddCardEvent.ADD_CARD_EVENT, new AddCardEventHandler(databaseService));
        borderPane.addEventHandler(AddListEvent.ADD_LIST, new AddListEventHandler(databaseService));

        borderPane.addEventHandler(DeleteCardEvent.DELETE_CARD_EVENT, new DeleteCardEventHandler(databaseService));
        borderPane.addEventHandler(DeleteListEvent.DELETE_LIST, new DeleteListEventHandler(databaseService));

        borderPane.addEventHandler(ImportListEvent.IMPORT_LIST, new ImportListEventHandler(databaseService));
//        borderPane.addEventHandler(SearchCardEvent.SEARCH_CARD, new SearchCardEventHandler(this, databaseService));

        borderPane.addEventHandler(UpdateAvailabilityEvent.UPDATE_AVAILABILITY, new UpdateAvailabilityEventHandler(databaseService));
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
                fxmlLoader.setController(new ListItemController(cardList, this));

                cardsFlowPane.getChildren().add(fxmlLoader.load());
            }

            borderPane.setRight(listRightPaneController.getRightPane());
        }
        catch (Exception e) {
            App.logger.error(Arrays.toString(e.getStackTrace()));
            displayAlert(e);
        }
    }

    void displayCollection() {
        try {
            displayList(databaseService.getCardList("Collection"));
        }
        catch (Exception e) {
            App.logger.error(Arrays.toString(e.getStackTrace()));
            displayAlert(e);
        }
    }

    public void displayList(CardList cardList) {
        try {
            cardsFlowPane.getChildren().clear();
            for (var cardData : cardList.getCards()) {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Addresses.CARD_INFO));
                fxmlLoader.setController(new CardItemController(cardData, this));

                cardsFlowPane.getChildren().add(fxmlLoader.load());
            }

            borderPane.setRight(cardRightPaneController.getRightPane(cardList));
        }
        catch (Exception e) {
            App.logger.error(Arrays.toString(e.getStackTrace()));
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
