package org.example.mtgspotscrapper.model.listImpl;

import org.example.mtgspotscrapper.model.ObservableAtomicCounter;
import org.example.mtgspotscrapper.model.cardImpl.CardManager;
import org.example.mtgspotscrapper.model.cardImpl.CardData;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.jooq.DSLContext;

import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SimpleCardList implements CardList {
    private final ListData listData;
    private final DSLContext dslContext;
    private final CardManager cardManager;

    private final ObservableAtomicCounter currentlyAddedCardsCounter;

    public SimpleCardList(ListData listData, DSLContext dslContext, CardManager cardManager) {
        this.listData = listData;
        this.dslContext = dslContext;
        this.cardManager = cardManager;

        currentlyAddedCardsCounter = cardManager.getCurrentlyAddedCardsCounter();
    }

    @Override
    public String getName() {
        return listData.name();
    }

    @Override
    public String getLogoPath() {
        return listData.logoPath();
    }

    @Override
    public Collection<? extends Card> getCards() {
        return dslContext.select(FULLLISTDATA.MULTIVERSE_ID, FULLLISTDATA.CARD_NAME, FULLLISTDATA.IMAGE_URL, FULLLISTDATA.LOCAL_ADDRESS)
                .from(FULLLISTDATA)
                .where(FULLLISTDATA.LIST_NAME.eq(listData.name()))
                .stream().map(cardData -> cardManager.getCard(
                        new CardData(cardData.getValue(FULLLISTDATA.MULTIVERSE_ID), cardData.getValue(FULLLISTDATA.CARD_NAME), cardData.getValue(FULLLISTDATA.IMAGE_URL)),
                        CompletableFuture.completedFuture(cardData.getValue(FULLLISTDATA.LOCAL_ADDRESS)), dslContext)).toList();
    }

    @Override
    public void addCardToList(Card card) {
        if (card == null) {
            return;
        }

        currentlyAddedCardsCounter.increment();

        dslContext.insertInto(FULLLISTDATA, FULLLISTDATA.LIST_ID, FULLLISTDATA.MULTIVERSE_ID)
                .values(listData.id(), card.getCardData().multiverseId())
                .execute();

        currentlyAddedCardsCounter.decrement();
    }

    @Override
    public boolean deleteCardFromList(String cardName) {
        Card card = cardManager.getCard(cardName);
        if (card == null) {
            return false;
        }

        var result = dslContext.select(FULLLISTDATA.MULTIVERSE_ID)
                .from(FULLLISTDATA)
                .where(FULLLISTDATA.LIST_ID.eq(listData.id()))
                .and(FULLLISTDATA.MULTIVERSE_ID.eq(card.getCardData().multiverseId()))
                .fetch();

        if(result.isEmpty()) {
            return false;
        }

        dslContext.deleteFrom(FULLLISTDATA)
                .where(FULLLISTDATA.LIST_ID.eq(listData.id()))
                .and(FULLLISTDATA.CARD_NAME.eq(cardName))
                .execute();

        return true;
    }

    @Override
    public String toString() {
        return listData.toString();
    }
}
