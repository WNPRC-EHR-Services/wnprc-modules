/**
 * This class is generated by jOOQ
 */
package org.labkey.gringotts.model.jooq;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.labkey.gringotts.model.jooq.tables.Records;
import org.labkey.gringotts.model.jooq.tables.Transactions;
import org.labkey.gringotts.model.jooq.tables.VaultColumns;
import org.labkey.gringotts.model.jooq.tables.VaultDatetimeValues;
import org.labkey.gringotts.model.jooq.tables.VaultIntValues;
import org.labkey.gringotts.model.jooq.tables.VaultLinks;
import org.labkey.gringotts.model.jooq.tables.VaultTextValues;
import org.labkey.gringotts.model.jooq.tables.Vaults;


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
public class Gringotts extends SchemaImpl {

    private static final long serialVersionUID = -1063924478;

    /**
     * The reference instance of <code>gringotts</code>
     */
    public static final Gringotts GRINGOTTS = new Gringotts();

    /**
     * The table <code>gringotts.records</code>.
     */
    public final Records RECORDS = org.labkey.gringotts.model.jooq.tables.Records.RECORDS;

    /**
     * This is the basic unit of the ledger.  Every update to the database is associated with a transaction.
     */
    public final Transactions TRANSACTIONS = org.labkey.gringotts.model.jooq.tables.Transactions.TRANSACTIONS;

    /**
     * The table <code>gringotts.vault_columns</code>.
     */
    public final VaultColumns VAULT_COLUMNS = org.labkey.gringotts.model.jooq.tables.VaultColumns.VAULT_COLUMNS;

    /**
     * The table <code>gringotts.vault_datetime_values</code>.
     */
    public final VaultDatetimeValues VAULT_DATETIME_VALUES = org.labkey.gringotts.model.jooq.tables.VaultDatetimeValues.VAULT_DATETIME_VALUES;

    /**
     * The table <code>gringotts.vault_int_values</code>.
     */
    public final VaultIntValues VAULT_INT_VALUES = org.labkey.gringotts.model.jooq.tables.VaultIntValues.VAULT_INT_VALUES;

    /**
     * The table <code>gringotts.vault_links</code>.
     */
    public final VaultLinks VAULT_LINKS = org.labkey.gringotts.model.jooq.tables.VaultLinks.VAULT_LINKS;

    /**
     * The table <code>gringotts.vault_text_values</code>.
     */
    public final VaultTextValues VAULT_TEXT_VALUES = org.labkey.gringotts.model.jooq.tables.VaultTextValues.VAULT_TEXT_VALUES;

    /**
     * The table <code>gringotts.vaults</code>.
     */
    public final Vaults VAULTS = org.labkey.gringotts.model.jooq.tables.Vaults.VAULTS;

    /**
     * No further instances allowed
     */
    private Gringotts() {
        super("gringotts", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Records.RECORDS,
            Transactions.TRANSACTIONS,
            VaultColumns.VAULT_COLUMNS,
            VaultDatetimeValues.VAULT_DATETIME_VALUES,
            VaultIntValues.VAULT_INT_VALUES,
            VaultLinks.VAULT_LINKS,
            VaultTextValues.VAULT_TEXT_VALUES,
            Vaults.VAULTS);
    }
}
