/*
 * This file is generated by jOOQ.
 */
package org.example.mtgspotscrapper.model.databaseClasses.tables.records;


import org.example.mtgspotscrapper.model.databaseClasses.tables.Listswithlogos;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class ListswithlogosRecord extends TableRecordImpl<ListswithlogosRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>scrapper.listswithlogos.logo_id</code>.
     */
    public void setLogoId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>scrapper.listswithlogos.logo_id</code>.
     */
    public Integer getLogoId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>scrapper.listswithlogos.list_id</code>.
     */
    public void setListId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>scrapper.listswithlogos.list_id</code>.
     */
    public Integer getListId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>scrapper.listswithlogos.list_name</code>.
     */
    public void setListName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>scrapper.listswithlogos.list_name</code>.
     */
    public String getListName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>scrapper.listswithlogos.logo_path</code>.
     */
    public void setLogoPath(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>scrapper.listswithlogos.logo_path</code>.
     */
    public String getLogoPath() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ListswithlogosRecord
     */
    public ListswithlogosRecord() {
        super(Listswithlogos.LISTSWITHLOGOS);
    }

    /**
     * Create a detached, initialised ListswithlogosRecord
     */
    public ListswithlogosRecord(Integer logoId, Integer listId, String listName, String logoPath) {
        super(Listswithlogos.LISTSWITHLOGOS);

        setLogoId(logoId);
        setListId(listId);
        setListName(listName);
        setLogoPath(logoPath);
        resetChangedOnNotNull();
    }
}
