package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public abstract class CardLogoAndNameController {
    protected final ScreenManager screenManager;

    @SuppressWarnings("unused")
    @FXML
    protected ImageView imageView;

    @SuppressWarnings("unused")
    @FXML
    protected Label label;

    @SuppressWarnings("unused")
    @FXML
    protected VBox vBox;

    @SuppressWarnings("unused")
    @FXML
    protected abstract void initialize();

    CardLogoAndNameController(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }
}
