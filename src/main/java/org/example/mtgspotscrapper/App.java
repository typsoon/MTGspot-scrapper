package org.example.mtgspotscrapper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.mtgspotscrapper.view.Addresses;
import org.example.mtgspotscrapper.view.LoginSceneController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App extends Application {
    public static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        LoginSceneController loginSceneController = new LoginSceneController(stage);

        FXMLLoader loginScreenLoader = new FXMLLoader(App.class.getResource(Addresses.LOGIN_SCREEN));
        loginScreenLoader.setController(loginSceneController);
        stage.setTitle("MTGspot scrapper");

        if (!loginSceneController.loggedInWithSavedCredentials()) {
            stage.setScene(new Scene(loginScreenLoader.load()));
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}