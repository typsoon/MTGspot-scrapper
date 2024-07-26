package org.example.mtgspotscrapper.viewmodel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public interface DatabaseService {
    Collection<CardList> getAllLists() throws SQLException;
    CardList getCardList(String name) throws SQLException;

    Card addCard(String cardName) throws SQLException, IOException;
    CardList addList(String listName) throws SQLException, IOException;

    @SuppressWarnings({"unused", "UnusedReturnValue"})
//    boolean deleteCard(String cardName) throws SQLException;
    boolean deleteList(String listName) throws SQLException;

    Card getCard(String cardName) throws SQLException;

    Collection<Card> getAllCardsData() throws SQLException;
}