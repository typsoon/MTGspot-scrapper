/*
 * This file is generated by jOOQ.
 */
package org.example.mtgspotscrapper.model.databaseClasses.tables.records;


import java.math.BigDecimal;

import org.example.mtgspotscrapper.model.databaseClasses.tables.Cardswithprices;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CardswithpricesRecord extends TableRecordImpl<CardswithpricesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>scrapper.cardswithprices.multiverse_id</code>.
     */
    public void setMultiverseId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>scrapper.cardswithprices.multiverse_id</code>.
     */
    public Integer getMultiverseId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>scrapper.cardswithprices.previous_price</code>.
     */
    public void setPreviousPrice(BigDecimal value) {
        set(1, value);
    }

    /**
     * Getter for <code>scrapper.cardswithprices.previous_price</code>.
     */
    public BigDecimal getPreviousPrice() {
        return (BigDecimal) get(1);
    }

    /**
     * Setter for <code>scrapper.cardswithprices.actual_price</code>.
     */
    public void setActualPrice(BigDecimal value) {
        set(2, value);
    }

    /**
     * Getter for <code>scrapper.cardswithprices.actual_price</code>.
     */
    public BigDecimal getActualPrice() {
        return (BigDecimal) get(2);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CardswithpricesRecord
     */
    public CardswithpricesRecord() {
        super(Cardswithprices.CARDSWITHPRICES);
    }

    /**
     * Create a detached, initialised CardswithpricesRecord
     */
    public CardswithpricesRecord(Integer multiverseId, BigDecimal previousPrice, BigDecimal actualPrice) {
        super(Cardswithprices.CARDSWITHPRICES);

        setMultiverseId(multiverseId);
        setPreviousPrice(previousPrice);
        setActualPrice(actualPrice);
        resetChangedOnNotNull();
    }
}