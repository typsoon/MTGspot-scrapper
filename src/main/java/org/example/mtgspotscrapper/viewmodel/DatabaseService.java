package org.example.mtgspotscrapper.viewmodel;

import org.example.mtgspotscrapper.model.ObservableAtomicCounter;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface DatabaseService {
    Collection<CardList> getAllLists();
    CardList getCardList(String name);

    CompletableFuture<Card> addCard(String cardName);
    ObservableAtomicCounter getCurrentlyAddedCardsCounter();
    CardList addList(String listName);

    boolean deleteList(String listName);
    Card getCard(String cardName);

    Collection<String> getAllCardNames();
}