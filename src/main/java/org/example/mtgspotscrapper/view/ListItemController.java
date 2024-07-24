package org.example.mtgspotscrapper.view;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.model.CardList;

import java.util.Objects;

public class ListItemController extends CardLogoAndNameController {
    private final CardList cardList;

    ListItemController(CardList cardList, ScreenManager screenManager) {
        super(screenManager);
        this.cardList = cardList;
    }

    @Override
    protected void initialize() {
        Image tempImage = new Image(Objects.requireNonNull(App.class.getResource(cardList.getLogoPath() != null ? cardList.getLogoPath() : Addresses.DEFAULT_LOGO)).toExternalForm());
        imageView.setImage(tempImage);
        label.setText(cardList.getName());

        vBox.setCursor(Cursor.HAND);
        vBox.setOnMouseClicked(mouseEvent -> screenManager.displayList(cardList));
    }
}
