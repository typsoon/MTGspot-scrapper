package org.example.mtgspotscrapper.viewmodel;

import java.util.Collection;

public interface CardList {
    String getName();
    String getLogoPath();
    Collection<? extends Card> getCards();
    void addCardToList(Card card);
    boolean deleteCardFromList(String cardName);
}
