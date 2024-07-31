package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.view.sidesControllers.LeftSideManager;
import org.example.mtgspotscrapper.view.sidesControllers.RightSideManager;
import org.example.mtgspotscrapper.view.viewEvents.guiEvents.ShowAllListsEvent;
import org.example.mtgspotscrapper.view.viewEvents.guiEvents.ShowListEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.*;
import org.example.mtgspotscrapper.view.rightPanesImplementations.CardRightPane;
import org.example.mtgspotscrapper.view.rightPanesImplementations.ListRightPane;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.eventHandling.handlers.*;

import java.io.IOException;

public class ScreenManager {
    private final DatabaseService databaseService;
    private LeftSideManager leftSideManager;
    private RightSideManager rightSideManager;

    ScreenManager(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @SuppressWarnings("unused")
    @FXML
    private void initialize() {
        leftSideManager = new LeftSideManager(cardsFlowPane, databaseService);
        try {
            rightSideManager = new RightSideManager(rightContainer, databaseService);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
            rightContainer.setPrefWidth(newValue.doubleValue()*(1-leftToWholeRatio));
        });

        cardLists.setOnMouseClicked(mouseEvent -> showAllLists());
        collection.setOnMouseClicked(mouseEvent -> showList(databaseService.getCardList("Collection")));

        borderPane.addEventHandler(ShowListEvent.SHOW_LIST, event -> showList(event.getCardList()));
        borderPane.addEventHandler(ShowAllListsEvent.SHOW_ALL_LISTS, event -> showAllLists());
    }

    private void showAllLists() {
        leftSideManager.displayLists();
        rightSideManager.showListsMenu();
    }

    private void showList(CardList cardList) {
        leftSideManager.displayList(cardList);
        rightSideManager.showCardsMenu(cardList);
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

    @SuppressWarnings("unused")
    @FXML
    private Pane rightContainer;
}
