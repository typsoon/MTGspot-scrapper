package org.example.mtgspotscrapper.model;

import org.example.mtgspotscrapper.model.cardImpl.SimpleCard;
import org.example.mtgspotscrapper.model.records.CardData;
import org.example.mtgspotscrapper.model.records.ListData;
import org.example.mtgspotscrapper.viewmodel.Card;
import org.example.mtgspotscrapper.viewmodel.CardList;
import org.jooq.DSLContext;
import org.jooq.Record1;

import static org.example.mtgspotscrapper.model.databaseClasses.Tables.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SimpleCardList implements CardList {
    private final ListData listData;
    private final DSLContext dslContext;

    public SimpleCardList(ListData listData, DSLContext dslContext) {
        this.listData = listData;
        this.dslContext = dslContext;
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
        return dslContext.select(LISTCARDS.MULTIVERSE_ID, FULLCARDDATA.PREVIOUS_PRICE, FULLCARDDATA.CARD_NAME, FULLCARDDATA.IMAGE_URL, FULLCARDDATA.LOCAL_ADDRESS)
                .from(FULLCARDDATA).join(LISTCARDS)
                .using(FULLCARDDATA.MULTIVERSE_ID)
                .join(LISTS)
                .using(LISTS.LIST_ID)
                .where(LISTS.LIST_NAME.eq(listData.name()))
                .stream().map(cardData -> new SimpleCard(
                        new CardData(cardData.getValue(FULLCARDDATA.MULTIVERSE_ID), cardData.getValue(FULLCARDDATA.CARD_NAME), cardData.getValue(FULLCARDDATA.IMAGE_URL)),
                        CompletableFuture.completedFuture(cardData.getValue(FULLCARDDATA.LOCAL_ADDRESS)), dslContext)).toList();
    }

    @Override
    public void addCardToList(Card card) {
        if (card == null) {
            return;
        }

        dslContext.insertInto(LISTCARDS, LISTCARDS.LIST_ID, LISTCARDS.MULTIVERSE_ID)
                .values(listData.id(), card.getCardData().multiverseId())
                .execute();
    }

    @Override
    public boolean deleteCardFromList(String cardName) {
        Record1<Integer> wrappedCardId = dslContext.select(CARDS.MULTIVERSE_ID)
                .from(CARDS)
                .where(CARDS.CARD_NAME.eq(cardName))
                .fetchOne();

        if (wrappedCardId == null) {
            return false;
        }

        dslContext.deleteFrom(LISTCARDS)
                .where(LISTCARDS.MULTIVERSE_ID.eq(wrappedCardId.getValue(CARDS.MULTIVERSE_ID)))
                .execute();
        return true;
    }

    @Override
    public String toString() {
        return listData.toString();
    }
}
