package org.example.mtgspotscrapper.view.rightPanesImplementations;

import javafx.scene.Node;
import org.example.mtgspotscrapper.view.AbstractRightPane;
import org.example.mtgspotscrapper.view.ScreenManager;
import org.example.mtgspotscrapper.viewmodel.DatabaseService;

import java.io.IOException;

public class ListRightPane extends AbstractRightPane {
    public ListRightPane(String fxmlPath, DatabaseService databaseService, ScreenManager screenManager) throws IOException {
        super(fxmlPath, databaseService, screenManager);
    }

    public Node getRightPane() {
        return rightPane;
    }

    @Override
    protected void initialize() {

    }
}
