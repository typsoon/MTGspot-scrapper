package org.example.mtgspotscrapper.viewmodel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface DatabaseService {
    Collection<CardList> getAllLists();
    CardList getCardList(String name);

    CompletableFuture<Card> addCard(String cardName);
    CardList addList(String listName);

    boolean deleteList(String listName);
    Card getCard(String cardName);
}