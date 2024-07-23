package org.example.mtgspotscrapper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.mtgspotscrapper.view.FXMLAddresses;
import org.example.mtgspotscrapper.view.LoginSceneController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App extends Application {
    public static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//
//        stage.setTitle("MTGspot Scrapper");
        LoginSceneController loginSceneController = new LoginSceneController(stage);

        FXMLLoader loginScreenLoader = new FXMLLoader(App.class.getResource(FXMLAddresses.LOGIN_SCREEN));
        loginScreenLoader.setController(loginSceneController);

        if (!loginSceneController.loggedInWithSavedCredentials()) {
            stage.setScene(new Scene(loginScreenLoader.load()));
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}