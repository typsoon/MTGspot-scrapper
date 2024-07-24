package org.example.mtgspotscrapper.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public interface DatabaseService {
    Collection<CardList> getAllLists() throws SQLException;
    CardList getCardList(String name) throws SQLException;
    Card addCard(String cardName) throws SQLException, IOException;

    Collection<Card> getAllCardsData() throws SQLException;
}