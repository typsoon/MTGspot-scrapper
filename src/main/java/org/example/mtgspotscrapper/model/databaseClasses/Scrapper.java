/*
 * This file is generated by jOOQ.
 */
package org.example.mtgspotscrapper.model.databaseClasses;


import java.util.Arrays;
import java.util.List;

import org.example.mtgspotscrapper.model.databaseClasses.tables.Allcardsview;
import org.example.mtgspotscrapper.model.databaseClasses.tables.Cardsimagesaddresses;
import org.example.mtgspotscrapper.model.databaseClasses.tables.Cardswithprices;
import org.example.mtgspotscrapper.model.databaseClasses.tables.Fulldownloadedcarddata;
import org.example.mtgspotscrapper.model.databaseClasses.tables.Fulllistdata;
import org.example.mtgspotscrapper.model.databaseClasses.tables.Listswithlogos;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Scrapper extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>scrapper</code>
     */
    public static final Scrapper SCRAPPER = new Scrapper();

    /**
     * The table <code>scrapper.allcardsview</code>.
     */
    public final Allcardsview ALLCARDSVIEW = Allcardsview.ALLCARDSVIEW;

    /**
     * The table <code>scrapper.cardsimagesaddresses</code>.
     */
    public final Cardsimagesaddresses CARDSIMAGESADDRESSES = Cardsimagesaddresses.CARDSIMAGESADDRESSES;

    /**
     * The table <code>scrapper.cardswithprices</code>.
     */
    public final Cardswithprices CARDSWITHPRICES = Cardswithprices.CARDSWITHPRICES;

    /**
     * The table <code>scrapper.fulldownloadedcarddata</code>.
     */
    public final Fulldownloadedcarddata FULLDOWNLOADEDCARDDATA = Fulldownloadedcarddata.FULLDOWNLOADEDCARDDATA;

    /**
     * The table <code>scrapper.fulllistdata</code>.
     */
    public final Fulllistdata FULLLISTDATA = Fulllistdata.FULLLISTDATA;

    /**
     * The table <code>scrapper.listswithlogos</code>.
     */
    public final Listswithlogos LISTSWITHLOGOS = Listswithlogos.LISTSWITHLOGOS;

    /**
     * No further instances allowed
     */
    private Scrapper() {
        super("scrapper", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Allcardsview.ALLCARDSVIEW,
            Cardsimagesaddresses.CARDSIMAGESADDRESSES,
            Cardswithprices.CARDSWITHPRICES,
            Fulldownloadedcarddata.FULLDOWNLOADEDCARDDATA,
            Fulllistdata.FULLLISTDATA,
            Listswithlogos.LISTSWITHLOGOS
        );
    }
}