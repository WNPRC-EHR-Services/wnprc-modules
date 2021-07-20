/**
 * This class is generated by jOOQ
 */
package org.labkey.gringotts.model.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
import org.labkey.gringotts.model.jooq.tables.VaultIntValues;


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
public class VaultIntValuesRecord extends UpdatableRecordImpl<VaultIntValuesRecord> implements Record5<String, String, String, String, Integer> {

    private static final long serialVersionUID = 1432414626;

    /**
     * Setter for <code>gringotts.vault_int_values.vaultid</code>.
     */
    public void setVaultid(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>gringotts.vault_int_values.vaultid</code>.
     */
    public String getVaultid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>gringotts.vault_int_values.recordid</code>.
     */
    public void setRecordid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>gringotts.vault_int_values.recordid</code>.
     */
    public String getRecordid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>gringotts.vault_int_values.columnid</code>.
     */
    public void setColumnid(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>gringotts.vault_int_values.columnid</code>.
     */
    public String getColumnid() {
        return (String) get(2);
    }

    /**
     * Setter for <code>gringotts.vault_int_values.transactionid</code>.
     */
    public void setTransactionid(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>gringotts.vault_int_values.transactionid</code>.
     */
    public String getTransactionid() {
        return (String) get(3);
    }

    /**
     * Setter for <code>gringotts.vault_int_values.value</code>.
     */
    public void setValue(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>gringotts.vault_int_values.value</code>.
     */
    public Integer getValue() {
        return (Integer) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record4<String, String, String, String> key() {
        return (Record4) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, String, String, String, Integer> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<String, String, String, String, Integer> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return VaultIntValues.VAULT_INT_VALUES.VAULTID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return VaultIntValues.VAULT_INT_VALUES.RECORDID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return VaultIntValues.VAULT_INT_VALUES.COLUMNID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return VaultIntValues.VAULT_INT_VALUES.TRANSACTIONID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return VaultIntValues.VAULT_INT_VALUES.VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getVaultid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getRecordid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getColumnid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getTransactionid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VaultIntValuesRecord value1(String value) {
        setVaultid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VaultIntValuesRecord value2(String value) {
        setRecordid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VaultIntValuesRecord value3(String value) {
        setColumnid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VaultIntValuesRecord value4(String value) {
        setTransactionid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VaultIntValuesRecord value5(Integer value) {
        setValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VaultIntValuesRecord values(String value1, String value2, String value3, String value4, Integer value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached VaultIntValuesRecord
     */
    public VaultIntValuesRecord() {
        super(VaultIntValues.VAULT_INT_VALUES);
    }

    /**
     * Create a detached, initialised VaultIntValuesRecord
     */
    public VaultIntValuesRecord(String vaultid, String recordid, String columnid, String transactionid, Integer value) {
        super(VaultIntValues.VAULT_INT_VALUES);

        set(0, vaultid);
        set(1, recordid);
        set(2, columnid);
        set(3, transactionid);
        set(4, value);
    }
}