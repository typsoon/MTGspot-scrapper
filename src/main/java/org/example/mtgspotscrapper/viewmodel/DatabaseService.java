package org.example.mtgspotscrapper.viewmodel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface DatabaseService {
    Collection<CardList> getAllLists() throws SQLException;
    CardList getCardList(String name) throws SQLException;

    CompletableFuture<Card> addCard(String cardName) throws SQLException, IOException;
    CardList addList(String listName) throws SQLException, IOException;

    boolean deleteList(String listName) throws SQLException;
    Card getCard(String cardName) throws SQLException;

}