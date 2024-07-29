package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public abstract class CardLogoAndNameController {
    protected final ScreenManager screenManager;

    @FXML
    protected ImageView imageView;

    @FXML
    protected Label label;

    @FXML
    protected VBox vBox;

    @SuppressWarnings("unused")
    @FXML
    protected abstract void initialize();

    protected CardLogoAndNameController(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }
}
