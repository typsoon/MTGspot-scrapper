package org.example.mtgspotscrapper.view.sidesManagers;

import javafx.scene.layout.Pane;
import org.example.mtgspotscrapper.view.Addresses;
import org.example.mtgspotscrapper.view.sidesManagers.rightPanesImplementations.CardRightPane;
import org.example.mtgspotscrapper.view.sidesManagers.rightPanesImplementations.ListRightPane;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.*;
import org.example.mtgspotscrapper.viewmodel.eventHandling.handlers.*;

import java.io.IOException;

public class RightSideManager {
    private final Pane rightContainer;
    private final CardRightPane cardRightPaneController;
    private final ListRightPane listRightPaneController;

    public RightSideManager(Pane rightContainer, DatabaseService databaseService) throws IOException {
        this.rightContainer = rightContainer;

        var allCards = databaseService.getAllCardNames();
        cardRightPaneController = new CardRightPane(Addresses.CARD_RIGHT_PANE, allCards);
        listRightPaneController = new ListRightPane(Addresses.LIST_RIGHT_PANE);

        rightContainer.addEventHandler(AddCardEvent.ADD_CARD_EVENT, new AddCardEventHandler(databaseService));
        rightContainer.addEventHandler(AddListEvent.ADD_LIST, new AddListEventHandler(databaseService));

        rightContainer.addEventHandler(DeleteCardEvent.DELETE_CARD_EVENT, new DeleteCardEventHandler(databaseService));
        rightContainer.addEventHandler(DeleteListEvent.DELETE_LIST, new DeleteListEventHandler(databaseService));

        rightContainer.addEventHandler(ImportListEvent.IMPORT_LIST, new ImportListEventHandler(databaseService));
//        borderPane.addEventHandler(SearchCardEvent.SEARCH_CARD, new SearchCardEventHandler(this, databaseService));

        rightContainer.addEventHandler(UpdateAvailabilityEvent.UPDATE_AVAILABILITY, new UpdateAvailabilityEventHandler(databaseService));
    }

    public void showListsMenu() {
        rightContainer.getChildren().setAll(listRightPaneController.getRightPane());
    }

    public void showCardsMenu(CardList cardList) {
        rightContainer.getChildren().setAll(cardRightPaneController.getRightPane(cardList));
    }
}
