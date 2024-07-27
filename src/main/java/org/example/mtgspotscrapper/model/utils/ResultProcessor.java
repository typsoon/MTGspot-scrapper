package org.example.mtgspotscrapper.model.utils;

import org.example.mtgspotscrapper.model.cardImpl.SimpleCard;
import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.viewmodel.Card;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public final class ResultProcessor {
    public Collection<Card> getCardsFromResultSet(ResultSet resultSet, Connection connection) throws SQLException {
        Collection<Card> answer = new ArrayList<>();
        while (resultSet.next()) {
            answer.add(new SimpleCard(connection,
                    new CardData(resultSet.getInt("multiverse_id"), resultSet.getString("card_name"), resultSet.getString("image_url")),
                    CompletableFuture.completedFuture(resultSet.getString("local_address"))));
        }
        return answer;
    }
}
