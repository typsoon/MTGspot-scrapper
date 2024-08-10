package org.example.mtgspotscrapper.view.sidesManagers;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a TextField which implements an "autocomplete" functionality, based on a supplied list of entries.
 * @author Caleb Brinkman
 */
public class AutoCompletionSupplier
{
    /** The existing autocomplete entries. */
    private final SortedSet<String> entries;
    /** The popup used to select an entry. */
    private final ContextMenu entriesPopup;
    private final TextField targetField;

    /** Construct a new AutoCompletionSupplier. */
    public AutoCompletionSupplier(TextField targetField) {
        this.targetField = targetField;
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();

        setListener();
    }

    private void setListener() {
        //Add "suggestions" by changing text
        targetField.textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = targetField.getText();
            //always hide suggestion if nothing has been entered (only "spacebars" are disallowed in TextFieldWithLengthLimit)
            if (enteredText == null || enteredText.isEmpty()) {
                entriesPopup.hide();
            } else {
                //filter all possible suggestions depends on "Text", case insensitive
                List<String> filteredEntries = entries.stream()
                        .filter(e -> e.toLowerCase().contains(enteredText.toLowerCase()))
                        .collect(Collectors.toList());
                //some suggestions are found
                if (!filteredEntries.isEmpty()) {
                    //build popup - list of "CustomMenuItem"
                    populatePopup(filteredEntries);
                    if (!entriesPopup.isShowing()) { //optional
                        entriesPopup.show(targetField, Side.BOTTOM, 0, 0); //position of popup
                    }
                    //no suggestions -> hide
                } else {
                    entriesPopup.hide();
                }
            }
        });

        //Hide always by focus-in (optional) and out
        targetField.focusedProperty().addListener((observableValue, oldValue, newValue) -> entriesPopup.hide());
    }

    /**
     * Get the existing set of autocomplete entries.
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() { return entries; }

    /**
     * Populate the entry set with the given search results.  Display is limited to 10 entries, for performance.
     * @param searchResult The set of matching strings.
     */

    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++)
        {
            CustomMenuItem item = getCustomMenuItem(searchResult, i);

            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
//        entriesPopup.fireEvent((new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, null, 0,
//                false, false, false, false, false, false, false, false, false, false, null)));
    }

    private CustomMenuItem getCustomMenuItem(List<String> searchResult, int i) {
        final String result = searchResult.get(i);
        Label entryLabel = new Label(result);
        CustomMenuItem item = new CustomMenuItem(entryLabel, true);
        item.setOnAction(actionEvent -> {
            targetField.setText(result);
//            targetField.positionCaret(result.length());
            entriesPopup.hide();
        });
        return item;
    }

//    private void populatePopup(List<String> searchResult, String searchReauest) {
//        //List of "suggestions"
//        List<CustomMenuItem> menuItems = new LinkedList<>();
//        //List size - 10 or founded suggestions count
//        int maxEntries = 10;
//        int count = Math.min(searchResult.size(), maxEntries);
//        //Build list as set of labels
//        for (int i = 0; i < count; i++) {
//            final String result = searchResult.get(i);
//            //label with graphic (text flow) to highlight founded subtext in suggestions
//            Label entryLabel = new Label();
////            entryLabel.setGraphic(Styles.buildTextFlow(result, searchReauest));
//            entryLabel.setPrefHeight(10);  //don't sure why it's changed with "graphic"
//            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
//            menuItems.add(item);
//
//            //if any suggestion is select set it into text and close popup
//            item.setOnAction(actionEvent -> {
//                targetField.setText(result);
//                targetField.positionCaret(result.length());
//                entriesPopup.hide();
//            });
//        }
//
//        //"Refresh" context menu
//        entriesPopup.getItems().clear();
//        entriesPopup.getItems().addAll(menuItems);
//    }

}