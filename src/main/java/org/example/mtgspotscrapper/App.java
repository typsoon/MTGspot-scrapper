package org.example.mtgspotscrapper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        stage.setTitle("MTGspot Scrapper");
//        LoginSceneController loginSceneController = new LoginSceneController(stage);
//        FXMLLoader loginScreenLoader = new FXMLLoader(App.class.getResource(FXMLAddresses.LOGIN_SCREEN));
//        loginScreenLoader.setController(loginSceneController);
//
//        if (loginSceneController.loggedInWithSavedCredentials() == null) {
//            stage.setScene(new Scene(loginScreenLoader.load()));
//            stage.show();
//        }
    }

    public static void main(String[] args) {
        launch();
    }
}