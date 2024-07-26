package org.example.mtgspotscrapper.viewmodel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public interface CardList {
    String getName();
    String getLogoPath();
    Collection<Card> getCards() throws SQLException;
    void addCardToList(Card card) throws SQLException, IOException;
    boolean deleteCardFromList(String cardName) throws SQLException;
}
