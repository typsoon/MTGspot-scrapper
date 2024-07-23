package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.model.records.PrevPriceAndCardData;

import java.sql.SQLException;
import java.util.Collection;

public interface DatabaseService {
    Collection<ListData> getListsNames() throws SQLException;

    Collection<PrevPriceAndCardData> getCardData(String listName) throws SQLException;
    Collection<PrevPriceAndCardData> getAllCardData() throws SQLException;
}