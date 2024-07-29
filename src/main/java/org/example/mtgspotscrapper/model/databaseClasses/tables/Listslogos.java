/*
 * This file is generated by jOOQ.
 */
package org.example.mtgspotscrapper.model.databaseClasses.tables;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.example.mtgspotscrapper.model.databaseClasses.Keys;
import org.example.mtgspotscrapper.model.databaseClasses.Public;
import org.example.mtgspotscrapper.model.databaseClasses.tables.Lists.ListsPath;
import org.example.mtgspotscrapper.model.databaseClasses.tables.records.ListslogosRecord;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Listslogos extends TableImpl<ListslogosRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.listslogos</code>
     */
    public static final Listslogos LISTSLOGOS = new Listslogos();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ListslogosRecord> getRecordType() {
        return ListslogosRecord.class;
    }

    /**
     * The column <code>public.listslogos.logo_id</code>.
     */
    public final TableField<ListslogosRecord, Integer> LOGO_ID = createField(DSL.name("logo_id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.listslogos.logo_path</code>.
     */
    public final TableField<ListslogosRecord, String> LOGO_PATH = createField(DSL.name("logo_path"), SQLDataType.VARCHAR.nullable(false), this, "");

    private Listslogos(Name alias, Table<ListslogosRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Listslogos(Name alias, Table<ListslogosRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.listslogos</code> table reference
     */
    public Listslogos(String alias) {
        this(DSL.name(alias), LISTSLOGOS);
    }

    /**
     * Create an aliased <code>public.listslogos</code> table reference
     */
    public Listslogos(Name alias) {
        this(alias, LISTSLOGOS);
    }

    /**
     * Create a <code>public.listslogos</code> table reference
     */
    public Listslogos() {
        this(DSL.name("listslogos"), null);
    }

    public <O extends Record> Listslogos(Table<O> path, ForeignKey<O, ListslogosRecord> childPath, InverseForeignKey<O, ListslogosRecord> parentPath) {
        super(path, childPath, parentPath, LISTSLOGOS);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class ListslogosPath extends Listslogos implements Path<ListslogosRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> ListslogosPath(Table<O> path, ForeignKey<O, ListslogosRecord> childPath, InverseForeignKey<O, ListslogosRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private ListslogosPath(Name alias, Table<ListslogosRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public ListslogosPath as(String alias) {
            return new ListslogosPath(DSL.name(alias), this);
        }

        @Override
        public ListslogosPath as(Name alias) {
            return new ListslogosPath(alias, this);
        }

        @Override
        public ListslogosPath as(Table<?> alias) {
            return new ListslogosPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<ListslogosRecord, Integer> getIdentity() {
        return (Identity<ListslogosRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<ListslogosRecord> getPrimaryKey() {
        return Keys.LISTSLOGOS_PKEY;
    }

    @Override
    public List<UniqueKey<ListslogosRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.LISTSLOGOS_LOGO_PATH_KEY);
    }

    private transient ListsPath _lists;

    /**
     * Get the implicit to-many join path to the <code>public.lists</code> table
     */
    public ListsPath lists() {
        if (_lists == null)
            _lists = new ListsPath(this, null, Keys.LISTS__LISTS_LOGO_ID_FKEY.getInverseKey());

        return _lists;
    }

    @Override
    public Listslogos as(String alias) {
        return new Listslogos(DSL.name(alias), this);
    }

    @Override
    public Listslogos as(Name alias) {
        return new Listslogos(alias, this);
    }

    @Override
    public Listslogos as(Table<?> alias) {
        return new Listslogos(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Listslogos rename(String name) {
        return new Listslogos(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Listslogos rename(Name name) {
        return new Listslogos(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Listslogos rename(Table<?> name) {
        return new Listslogos(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Listslogos where(Condition condition) {
        return new Listslogos(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Listslogos where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Listslogos where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Listslogos where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Listslogos where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Listslogos where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Listslogos where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Listslogos where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Listslogos whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Listslogos whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}