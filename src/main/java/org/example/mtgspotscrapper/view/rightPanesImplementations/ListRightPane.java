package org.example.mtgspotscrapper.view.rightPanesImplementations;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.mtgspotscrapper.App;
import org.example.mtgspotscrapper.view.AbstractRightPane;
import org.example.mtgspotscrapper.view.Addresses;
import org.example.mtgspotscrapper.view.viewEvents.guiEvents.ShowAllListsEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.AddListEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.DeleteListEvent;
import org.example.mtgspotscrapper.viewmodel.eventHandling.eventTypes.userInteractionEventTypes.ImportListEvent;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.io.IOException;

public class ListRightPane extends AbstractRightPane {
    public ListRightPane(String fxmlPath, DatabaseService databaseService) throws IOException {
        super(fxmlPath, databaseService);
    }

    public Node getRightPane() {
        return rightPane;
    }

    @SuppressWarnings("unused")
    @FXML
    private Label importList;

    @Override
    protected void initialize() {
        addLabel.setOnMouseClicked(mouseEvent -> {
            addLabel.fireEvent(new AddListEvent(addDeleteField.getText()));
            addLabel.fireEvent(new ShowAllListsEvent());
        });

        deleteLabel.setOnMouseClicked(mouseEvent -> {
            deleteLabel.fireEvent(new DeleteListEvent(addDeleteField.getText()));
            addLabel.fireEvent(new ShowAllListsEvent());
        });

        importList.setOnMouseClicked(mouseEvent -> {
            try {
                Stage popupStage = new Stage();

                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Addresses.TEXT_CAPTURE_VBOX));
                fxmlLoader.setController(new TextCaptureVBoxController(popupStage, importList));

                Parent textCaptureWindow = fxmlLoader.load();
                Scene scene = new Scene(textCaptureWindow, 600, 400);

                popupStage.setScene(scene);
                popupStage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

class TextCaptureVBoxController {
    private final Stage popupStage;
    private final Label importList;

    TextCaptureVBoxController(Stage popupStage, Label importList) {
        this.popupStage = popupStage;
        this.importList = importList;
    }

    @SuppressWarnings("unused")
    @FXML
    void initialize() {
        captureTextLabel.setOnMouseClicked(mouseEvent -> {
            importList.fireEvent(new ImportListEvent(textArea.getText(), listNameTextField.getText()));
            popupStage.close();
        });
    }

    @SuppressWarnings("unused")
    @FXML
    private Label captureTextLabel;

    @SuppressWarnings("unused")
    @FXML
    private TextField listNameTextField;

    @SuppressWarnings("unused")
    @FXML
    private TextArea textArea;
}