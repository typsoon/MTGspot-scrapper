package org.example.mtgspotscrapper.view.sidesManagers.rightPanesImplementations;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckComboBox;
import org.example.mtgspotscrapper.view.AbstractRightPane;
import org.example.mtgspotscrapper.view.sidesManagers.AutoCompletionSupplier;
import org.example.mtgspotscrapper.view.viewEvents.guiEvents.ShowListEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.AddCardEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.DeleteCardEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.FixBadPricesEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.UpdateAvailabilityEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.records.DeleteCardData;
import org.example.mtgspotscrapper.viewmodel.CardList;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public class CardRightPane extends AbstractRightPane {
    private CardList cardList;
    private final Collection<String> allCardsNames;

    public CardRightPane(String fxmlPath, Collection<String> allCardsNames) throws IOException {
        super(fxmlPath);
        this.allCardsNames = allCardsNames;
        load();
    }

    public Node getRightPane(CardList cardList) {
        this.cardList = Objects.requireNonNull(cardList);
        return rightPane;
    }

    @SuppressWarnings("unused")
    @FXML
    private Label updateAvailability;

    @SuppressWarnings("unused")
    @FXML
    private Label fixBadPrices;

    @Override
    protected void initialize() {
        CheckComboBox<String> cb = new CheckComboBox<>();
        cb.setTitle("Availability");
        cb.getItems().add("Available, previously unavailable");
        cb.getItems().add("Available, previously available");
        cb.getItems().add("Unavailable, previously available");
        cb.getItems().add("Unavailable, previously unavailable");
        gridPane.add(cb, 3, 3);

        addLabel.setOnMouseClicked(mouseEvent -> {
            addLabel.fireEvent(new AddCardEvent(addDeleteField.getText(), cardList));
//            addLabel.fireEvent(new ShowListEvent(cardList));
        });

        deleteLabel.setOnMouseClicked(mouseEvent -> {
            deleteLabel.fireEvent(new DeleteCardEvent(new DeleteCardData(addDeleteField.getText(), cardList)));
            deleteLabel.fireEvent(new ShowListEvent(cardList));
        });

        updateAvailability.setOnMouseClicked(mouseEvent -> {
            updateAvailability.fireEvent(new UpdateAvailabilityEvent(cardList));
            deleteLabel.fireEvent(new ShowListEvent(cardList));
        });

        fixBadPrices.setOnMouseClicked(mouseEvent -> {
            fixBadPrices.fireEvent(new FixBadPricesEvent(cardList));
            fixBadPrices.fireEvent(new ShowListEvent(cardList));
        });

        AutoCompletionSupplier autoCompletionSupplier = new AutoCompletionSupplier(addDeleteField);
        autoCompletionSupplier.getEntries().addAll(allCardsNames);
//        TextFields.bindAutoCompletion(addDeleteField, allCardsNames);
    }
}