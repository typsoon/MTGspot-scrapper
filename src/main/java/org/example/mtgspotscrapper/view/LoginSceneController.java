package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;
import org.example.mtgspotscrapper.model.SimpleDatabaseService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class LoginSceneController {
    private final Stage primaryStage;

    public LoginSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void displayMainStage(DatabaseService databaseService) throws IOException {
        ScreenManager screenManager = new ScreenManager(databaseService);
        FXMLLoader managerLoader = new FXMLLoader(App.class.getResource(Addresses.SCREEN_MANAGER));
        managerLoader.setController(screenManager);

        primaryStage.setScene(new Scene(managerLoader.load()));
        primaryStage.show();
    }

    private DatabaseService captureDataAndTryToLogIn() {
        try {
            return new SimpleDatabaseService("jdbc:postgresql://localhost/scrapper", "scrapper", "aaa");
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean loggedInWithSavedCredentials() throws IOException {
        try {
            Properties credentials = new Properties();

            try (InputStream stream = App.class.getResourceAsStream("localData/credentials.properties")) {
                DatabaseService databaseService;
                try {
                    credentials.load(stream);

                    databaseService = new SimpleDatabaseService("jdbc:postgresql://localhost/scrapper", credentials.getProperty("username"), credentials.getProperty("password"));
                }
                catch (IOException e) {
                    return false;
                }

                displayMainStage(databaseService);
                return true;
            }
        }
        catch (SQLException ignored) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    @FXML
    private void initialize() {
        logInButton.setOnAction(event -> {
            DatabaseService databaseService = captureDataAndTryToLogIn();
            if (databaseService != null) {
                try {
                    displayMainStage(databaseService);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load screenManager", e);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    @FXML private TextField usernameField;

    @SuppressWarnings("unused")
    @FXML private PasswordField passwordField;

    @SuppressWarnings("unused")
    @FXML private Button logInButton;
}
