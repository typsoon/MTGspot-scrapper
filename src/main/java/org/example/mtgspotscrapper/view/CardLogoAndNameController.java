package org.example.mtgspotscrapper.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.mtgspotscrapper.viewmodel.DownloaderService;

public abstract class CardLogoAndNameController {
    protected final ScreenManager screenManager;
    protected final DownloaderService downloaderService;

    @FXML
    protected ImageView imageView;

    @FXML
    protected Label label;

    @FXML
    protected VBox vBox;

    @SuppressWarnings("unused")
    @FXML
    protected abstract void initialize();

    protected CardLogoAndNameController(ScreenManager screenManager, DownloaderService downloaderService) {
        this.screenManager = screenManager;
        this.downloaderService = downloaderService;
    }
}
