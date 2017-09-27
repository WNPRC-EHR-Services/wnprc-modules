/**
 * This class is generated by jOOQ
 */
package org.labkey.wnprc_compliance.model.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.labkey.wnprc_compliance.model.jooq.Keys;
import org.labkey.wnprc_compliance.model.jooq.WnprcCompliance;
import org.labkey.wnprc_compliance.model.jooq.tables.records.AccessReportsRecord;


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
public class AccessReports extends TableImpl<AccessReportsRecord> {

    private static final long serialVersionUID = 486223751;

    /**
     * The reference instance of <code>wnprc_compliance.access_reports</code>
     */
    public static final AccessReports ACCESS_REPORTS = new AccessReports();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AccessReportsRecord> getRecordType() {
        return AccessReportsRecord.class;
    }

    /**
     * The column <code>wnprc_compliance.access_reports.report_id</code>.
     */
    public final TableField<AccessReportsRecord, String> REPORT_ID = createField("report_id", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>wnprc_compliance.access_reports.date</code>.
     */
    public final TableField<AccessReportsRecord, Timestamp> DATE = createField("date", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

    /**
     * The column <code>wnprc_compliance.access_reports.container</code>.
     */
    public final TableField<AccessReportsRecord, String> CONTAINER = createField("container", org.jooq.impl.SQLDataType.VARCHAR.length(36).nullable(false), this, "");

    /**
     * The column <code>wnprc_compliance.access_reports.createdby</code>.
     */
    public final TableField<AccessReportsRecord, Integer> CREATEDBY = createField("createdby", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>wnprc_compliance.access_reports.created</code>.
     */
    public final TableField<AccessReportsRecord, Timestamp> CREATED = createField("created", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>wnprc_compliance.access_reports.modifiedby</code>.
     */
    public final TableField<AccessReportsRecord, Integer> MODIFIEDBY = createField("modifiedby", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>wnprc_compliance.access_reports.modified</code>.
     */
    public final TableField<AccessReportsRecord, Timestamp> MODIFIED = createField("modified", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * Create a <code>wnprc_compliance.access_reports</code> table reference
     */
    public AccessReports() {
        this("access_reports", null);
    }

    /**
     * Create an aliased <code>wnprc_compliance.access_reports</code> table reference
     */
    public AccessReports(String alias) {
        this(alias, ACCESS_REPORTS);
    }

    private AccessReports(String alias, Table<AccessReportsRecord> aliased) {
        this(alias, aliased, null);
    }

    private AccessReports(String alias, Table<AccessReportsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return WnprcCompliance.WNPRC_COMPLIANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AccessReportsRecord> getPrimaryKey() {
        return Keys.PK_ACCESS_REPORTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AccessReportsRecord>> getKeys() {
        return Arrays.<UniqueKey<AccessReportsRecord>>asList(Keys.PK_ACCESS_REPORTS, Keys.ACCESS_REPORTS_DATE_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessReports as(String alias) {
        return new AccessReports(alias, this);
    }

    /**
     * Rename this table
     */
    public AccessReports rename(String name) {
        return new AccessReports(name, null);
    }
}
