package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.model.DatabaseService;
import org.example.mtgspotscrapper.model.SimpleDatabaseService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoginSceneController {
    private final Stage primaryStage;

    public LoginSceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void displayMainStage(DatabaseService databaseService) throws IOException {
        ScreenManager screenManager = new ScreenManager(databaseService, primaryStage);
        FXMLLoader managerLoader = new FXMLLoader(App.class.getResource(FXMLAddresses.SCREEN_MANAGER));
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

    public DatabaseService loggedInWithSavedCredentials() {
        try {
            Properties credentials = new Properties();
            InputStream stream = App.class.getResourceAsStream("localData/credentials.properties");

            credentials.load(stream);
//            System.out.println(new Credentials(credentials.getProperty("username"), credentials.getProperty("password")));
            return new SimpleDatabaseService("jdbc:postgresql://localhost/scrapper", credentials.getProperty("username"), credentials.getProperty("password"));
        }
        catch (Exception ignored) {
            return null;
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
