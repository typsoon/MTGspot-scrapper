package org.example.mtgspotscrapper.view.cardLogoAndNameImpl;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.view.Addresses;
import org.example.mtgspotscrapper.view.CardLogoAndNameController;
import org.example.mtgspotscrapper.view.viewEvents.guiEvents.ShowListEvent;
import org.example.mtgspotscrapper.viewmodel.CardList;

import java.util.Objects;

public class ListItemController extends CardLogoAndNameController {
    private final CardList cardList;

    public ListItemController(CardList cardList) {
        this.cardList = cardList;
    }

    @Override
    protected void initialize() {
        Image tempImage = new Image(Objects.requireNonNull(App.class.getResource(cardList.getLogoPath() != null ? cardList.getLogoPath() : Addresses.DEFAULT_LOGO)).toExternalForm());
        imageView.setImage(tempImage);
        label.setText(cardList.getName());

        vBox.setCursor(Cursor.HAND);
        vBox.setOnMouseClicked(mouseEvent -> {
            ShowListEvent showListEventEvent = new ShowListEvent(cardList);
            vBox.fireEvent(showListEventEvent);
        });
    }
}
