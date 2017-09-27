/**
 * This class is generated by jOOQ
 */
package org.labkey.deviceproxy.model.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.labkey.deviceproxy.model.jooq.Deviceproxy;
import org.labkey.deviceproxy.model.jooq.Keys;
import org.labkey.deviceproxy.model.jooq.tables.records.LeaseRecord;


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
public class Lease extends TableImpl<LeaseRecord> {

    private static final long serialVersionUID = 1124968551;

    /**
     * The reference instance of <code>deviceproxy.lease</code>
     */
    public static final Lease LEASE = new Lease();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LeaseRecord> getRecordType() {
        return LeaseRecord.class;
    }

    /**
     * The column <code>deviceproxy.lease.public_key</code>.
     */
    public final TableField<LeaseRecord, String> PUBLIC_KEY = createField("public_key", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>deviceproxy.lease.start_time</code>.
     */
    public final TableField<LeaseRecord, Timestamp> START_TIME = createField("start_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>deviceproxy.lease.createdby</code>.
     */
    public final TableField<LeaseRecord, Integer> CREATEDBY = createField("createdby", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>deviceproxy.lease.end_time</code>.
     */
    public final TableField<LeaseRecord, Timestamp> END_TIME = createField("end_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>deviceproxy.lease.endedby</code>.
     */
    public final TableField<LeaseRecord, Integer> ENDEDBY = createField("endedby", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>deviceproxy.lease.endedon</code>.
     */
    public final TableField<LeaseRecord, Timestamp> ENDEDON = createField("endedon", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * Create a <code>deviceproxy.lease</code> table reference
     */
    public Lease() {
        this("lease", null);
    }

    /**
     * Create an aliased <code>deviceproxy.lease</code> table reference
     */
    public Lease(String alias) {
        this(alias, LEASE);
    }

    private Lease(String alias, Table<LeaseRecord> aliased) {
        this(alias, aliased, null);
    }

    private Lease(String alias, Table<LeaseRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Deviceproxy.DEVICEPROXY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<LeaseRecord> getPrimaryKey() {
        return Keys.PK_LEASE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LeaseRecord>> getKeys() {
        return Arrays.<UniqueKey<LeaseRecord>>asList(Keys.PK_LEASE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<LeaseRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LeaseRecord, ?>>asList(Keys.LEASE__FK_LEASE_DEVICE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Lease as(String alias) {
        return new Lease(alias, this);
    }

    /**
     * Rename this table
     */
    public Lease rename(String name) {
        return new Lease(name, null);
    }
}
