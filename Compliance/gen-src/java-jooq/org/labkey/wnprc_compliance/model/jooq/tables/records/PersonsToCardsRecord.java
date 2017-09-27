/**
 * This class is generated by jOOQ
 */
package org.labkey.wnprc_compliance.model.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;
import org.labkey.wnprc_compliance.model.jooq.tables.PersonsToCards;


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
public class PersonsToCardsRecord extends UpdatableRecordImpl<PersonsToCardsRecord> implements Record7<String, String, String, Integer, Timestamp, Integer, Timestamp> {

    private static final long serialVersionUID = 1926964121;

    /**
     * Setter for <code>wnprc_compliance.persons_to_cards.personid</code>.
     */
    public void setPersonid(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>wnprc_compliance.persons_to_cards.personid</code>.
     */
    public String getPersonid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>wnprc_compliance.persons_to_cards.cardid</code>.
     */
    public void setCardid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>wnprc_compliance.persons_to_cards.cardid</code>.
     */
    public String getCardid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>wnprc_compliance.persons_to_cards.container</code>.
     */
    public void setContainer(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>wnprc_compliance.persons_to_cards.container</code>.
     */
    public String getContainer() {
        return (String) get(2);
    }

    /**
     * Setter for <code>wnprc_compliance.persons_to_cards.createdby</code>.
     */
    public void setCreatedby(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>wnprc_compliance.persons_to_cards.createdby</code>.
     */
    public Integer getCreatedby() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>wnprc_compliance.persons_to_cards.created</code>.
     */
    public void setCreated(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>wnprc_compliance.persons_to_cards.created</code>.
     */
    public Timestamp getCreated() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>wnprc_compliance.persons_to_cards.modifiedby</code>.
     */
    public void setModifiedby(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>wnprc_compliance.persons_to_cards.modifiedby</code>.
     */
    public Integer getModifiedby() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>wnprc_compliance.persons_to_cards.modified</code>.
     */
    public void setModified(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>wnprc_compliance.persons_to_cards.modified</code>.
     */
    public Timestamp getModified() {
        return (Timestamp) get(6);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record3<String, String, String> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, String, String, Integer, Timestamp, Integer, Timestamp> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<String, String, String, Integer, Timestamp, Integer, Timestamp> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return PersonsToCards.PERSONS_TO_CARDS.PERSONID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return PersonsToCards.PERSONS_TO_CARDS.CARDID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return PersonsToCards.PERSONS_TO_CARDS.CONTAINER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return PersonsToCards.PERSONS_TO_CARDS.CREATEDBY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return PersonsToCards.PERSONS_TO_CARDS.CREATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return PersonsToCards.PERSONS_TO_CARDS.MODIFIEDBY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return PersonsToCards.PERSONS_TO_CARDS.MODIFIED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getPersonid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getCardid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getContainer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getCreatedby();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getCreated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getModifiedby();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getModified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord value1(String value) {
        setPersonid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord value2(String value) {
        setCardid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord value3(String value) {
        setContainer(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord value4(Integer value) {
        setCreatedby(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord value5(Timestamp value) {
        setCreated(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord value6(Integer value) {
        setModifiedby(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord value7(Timestamp value) {
        setModified(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonsToCardsRecord values(String value1, String value2, String value3, Integer value4, Timestamp value5, Integer value6, Timestamp value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PersonsToCardsRecord
     */
    public PersonsToCardsRecord() {
        super(PersonsToCards.PERSONS_TO_CARDS);
    }

    /**
     * Create a detached, initialised PersonsToCardsRecord
     */
    public PersonsToCardsRecord(String personid, String cardid, String container, Integer createdby, Timestamp created, Integer modifiedby, Timestamp modified) {
        super(PersonsToCards.PERSONS_TO_CARDS);

        set(0, personid);
        set(1, cardid);
        set(2, container);
        set(3, createdby);
        set(4, created);
        set(5, modifiedby);
        set(6, modified);
    }
}
