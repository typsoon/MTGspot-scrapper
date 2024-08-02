package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.example.mtgspotscrapper.App;

import java.io.IOException;


public abstract class AbstractRightPane {
    protected final Node rightPane;

    @SuppressWarnings("unused")
    @FXML
    protected TextField searchField;

    @FXML
    protected TextField addDeleteField;

    @SuppressWarnings("unused")
    @FXML
    protected Label searchLabel;

    @FXML
    protected Label addLabel;

    @FXML
    protected Label deleteLabel;

    @SuppressWarnings("unused")
    @FXML
    protected ComboBox<String> whatComboBox;

    @SuppressWarnings("unused")
    @FXML
    protected ComboBox<String> ascDescComboBox;

    @FXML
    protected GridPane gridPane;

    @SuppressWarnings("unused")
    @FXML
    protected abstract void initialize();

    protected AbstractRightPane(String fxmlPath) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlPath));
        fxmlLoader.setController(this);

        rightPane = fxmlLoader.load();
    }
}
