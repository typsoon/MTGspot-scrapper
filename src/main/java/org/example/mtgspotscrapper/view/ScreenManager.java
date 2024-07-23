package org.example.mtgspotscrapper.view;

import javafx.stage.Stage;
import org.example.mtgspotscrapper.model.DatabaseService;

public class ScreenManager {
    private final Stage primaryStage;
    private final DatabaseService databaseService;

    ScreenManager(DatabaseService databaseService, Stage primaryStage) {
        this.databaseService = databaseService;
        this.primaryStage = primaryStage;
    }
}
