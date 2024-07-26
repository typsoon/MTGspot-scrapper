package org.example.mtgspotscrapper.view.rightPanesImplementations;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.example.mtgspotscrapper.view.AbstractRightPane;
import org.example.mtgspotscrapper.view.ScreenManager;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.AddListEvent;
import org.example.mtgspotscrapper.view.viewEvents.eventTypes.DeleteListEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.io.IOException;

public class ListRightPane extends AbstractRightPane {
    public ListRightPane(String fxmlPath, DatabaseService databaseService, ScreenManager screenManager) throws IOException {
        super(fxmlPath, databaseService, screenManager);
    }

    public Node getRightPane() {
        return rightPane;
    }

    @SuppressWarnings("unused")
    @FXML
    private Label importList;

    @Override
    protected void initialize() {
        addLabel.setCursor(Cursor.HAND);
        deleteLabel.setCursor(Cursor.HAND);

        searchLabel.setCursor(Cursor.HAND);
        importList.setCursor(Cursor.HAND);

        addLabel.setOnMouseClicked(mouseEvent -> {
            addLabel.fireEvent(new AddListEvent(addDeleteField.getText()));
            screenManager.displayLists();
        });

        deleteLabel.setOnMouseClicked(mouseEvent -> {
            deleteLabel.fireEvent(new DeleteListEvent(addDeleteField.getText()));
            screenManager.displayLists();
        });

//        importList.setOnMouseClicked(mouseEvent -> {new ImportListEvent()
//        });
    }
}
