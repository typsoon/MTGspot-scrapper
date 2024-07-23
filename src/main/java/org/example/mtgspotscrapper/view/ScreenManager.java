package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.example.mtgspotscrapper.model.DatabaseService;

public class ScreenManager {
    private final Stage primaryStage;
    private final DatabaseService databaseService;
    private double leftToWholeRatio;

    ScreenManager(DatabaseService databaseService, Stage primaryStage) {
        this.databaseService = databaseService;
        this.primaryStage = primaryStage;
    }

    @SuppressWarnings("unused")
    @FXML
    private void initialize() {
        cardLists.setCursor(Cursor.HAND);
        collection.setCursor(Cursor.HAND);

        borderPane.leftProperty();
        double leftPaneWidth = ((Region) borderPane.getLeft()).getPrefWidth();

        leftToWholeRatio = leftPaneWidth / borderPane.getPrefWidth();
        borderPane.widthProperty().addListener((observable, oldValue, newValue) -> ((Region) borderPane.getLeft()).setPrefWidth(newValue.doubleValue()*leftToWholeRatio));
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
}
