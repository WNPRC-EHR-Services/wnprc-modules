/**
 * This class is generated by jOOQ
 */
package org.labkey.apikey.model.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
import org.labkey.apikey.model.jooq.tables.Apikeys;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ApikeysRecord extends UpdatableRecordImpl<ApikeysRecord> implements Record6<String, String, Boolean, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = 1753368739;

    /**
     * Setter for <code>apikey.apikeys.apikey</code>.
     */
    public void setApikey(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>apikey.apikeys.apikey</code>.
     */
    public String getApikey() {
        return (String) get(0);
    }

    /**
     * Setter for <code>apikey.apikeys.note</code>.
     */
    public void setNote(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>apikey.apikeys.note</code>.
     */
    public String getNote() {
        return (String) get(1);
    }

    /**
     * Setter for <code>apikey.apikeys.issuperkey</code>.
     */
    public void setIssuperkey(Boolean value) {
        set(2, value);
    }

    /**
     * Getter for <code>apikey.apikeys.issuperkey</code>.
     */
    public Boolean getIssuperkey() {
        return (Boolean) get(2);
    }

    /**
     * Setter for <code>apikey.apikeys.owner</code>.
     */
    public void setOwner(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>apikey.apikeys.owner</code>.
     */
    public Integer getOwner() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>apikey.apikeys.starts</code>.
     */
    public void setStarts(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>apikey.apikeys.starts</code>.
     */
    public Timestamp getStarts() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>apikey.apikeys.expires</code>.
     */
    public void setExpires(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>apikey.apikeys.expires</code>.
     */
    public Timestamp getExpires() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, String, Boolean, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, String, Boolean, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Apikeys.APIKEYS.APIKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Apikeys.APIKEYS.NOTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field3() {
        return Apikeys.APIKEYS.ISSUPERKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Apikeys.APIKEYS.OWNER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return Apikeys.APIKEYS.STARTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return Apikeys.APIKEYS.EXPIRES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getApikey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getNote();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value3() {
        return getIssuperkey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getOwner();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getStarts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getExpires();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApikeysRecord value1(String value) {
        setApikey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApikeysRecord value2(String value) {
        setNote(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApikeysRecord value3(Boolean value) {
        setIssuperkey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApikeysRecord value4(Integer value) {
        setOwner(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApikeysRecord value5(Timestamp value) {
        setStarts(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApikeysRecord value6(Timestamp value) {
        setExpires(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApikeysRecord values(String value1, String value2, Boolean value3, Integer value4, Timestamp value5, Timestamp value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ApikeysRecord
     */
    public ApikeysRecord() {
        super(Apikeys.APIKEYS);
    }

    /**
     * Create a detached, initialised ApikeysRecord
     */
    public ApikeysRecord(String apikey, String note, Boolean issuperkey, Integer owner, Timestamp starts, Timestamp expires) {
        super(Apikeys.APIKEYS);

        set(0, apikey);
        set(1, note);
        set(2, issuperkey);
        set(3, owner);
        set(4, starts);
        set(5, expires);
    }
}
