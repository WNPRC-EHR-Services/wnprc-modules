Ext4.define('WNPRC_Billing.panel.BulkEditPanel', {
    extend: 'EHR.panel.BulkEditPanel',
    alias: 'widget.wnprc_billing-bulkeditpanel',

    initComponent: function(){

        this.callParent(arguments);
    },

    getRawFieldConfigs: function(){
        var items = this.callParent(arguments);
        Ext4.Array.forEach(items, function(item){
            item.cfg.allowBlank = true;

            if (item.cfg.xtype === 'wnprc_billing-investigatorfield' || item.cfg.xtype === 'wnprc_billing-chargeitemfield' || item.cfg.xtype === 'ehr_billingRowObserverEntryField') {
                item.cfg.xtype = 'combo';
            }

        }, this);

        return items;
    },

    getFieldConfigs: function(){
        var fields = this.callParent(arguments);
        var newItems = [];
        Ext4.Array.forEach(fields, function(item){

            item = Ext4.widget(item);

            if (item.name === 'project') {

                item.on('change', function (field, newValue) {
                    this.loadStoreOnValueChange(field, "investigator", "project", newValue);
                }, this);
            }
            else if (item.name === 'debitedaccount') {

                item.on('change', function(field, newValue){
                    this.loadStoreOnValueChange(field, "investigator", "alias", newValue);
                }, this);
            }
            else if (item.name === 'chargetype') {

                item.on('change', function(field, newValue){
                    this.loadStoreOnValueChange(field, "chargeId", "departmentCode", newValue);
                }, this);
            }
            else if (item.name === 'investigator') {

                item.on('beforerender', function(field){

                    var projectField = field.up("form").getForm().findField("project");
                    if (projectField) {
                        var filter = LABKEY.Filter.create('project', projectField.value, LABKEY.Filter.Types.EQUAL);
                        field.store.filterArray = [filter];
                        field.store.load();
                    }

                    var debitedAcctField = field.up("form").getForm().findField("debitedaccount");
                    if (debitedAcctField) {
                        var aliasFilter = LABKEY.Filter.create('alias', debitedAcctField.value, LABKEY.Filter.Types.EQUAL);
                        field.store = Ext4.create('LABKEY.ext4.data.Store', {
                            type: 'labkey-store',
                            schemaName: 'ehr_billing_public',
                            queryName: 'aliases',
                            columns: 'alias, investigatorId, investigatorName',
                            sort: 'alias',
                            filterArray: aliasFilter
                        });
                        field.store.load();
                    }
                });
            }
            else if (item.name === 'chargeId') {

                item.valueField = 'rowid';
                item.displayField = 'name';

                item.on('beforerender', function (field) {

                    var chargeTypeField = field.up("form").getForm().findField("chargetype");

                    if (chargeTypeField) {
                        var filter = LABKEY.Filter.create('departmentCode', chargeTypeField.value, LABKEY.Filter.Types.EQUAL);
                        field.store.filterArray = [filter];
                        field.store.load();
                    }
                });

                item.on('select', function (combo, recs) {

                    if (recs && recs[0] && recs[0].data) {

                        var chargeId = recs[0].data.rowid;
                        var chargeDateValue = item.up("form").getForm().findField("date").value;

                        LABKEY.Query.selectRows({
                            schemaName: 'ehr_billing_public',
                            queryName: 'chargeRates',
                            filterArray: [
                                LABKEY.Filter.create('chargeId', chargeId, LABKEY.Filter.Types.EQUAL),
                                LABKEY.Filter.create('startDate', chargeDateValue.format("Y-m-d"), LABKEY.Filter.Types.DATE_LESS_THAN_OR_EQUAL),
                                LABKEY.Filter.create('endDate', chargeDateValue.format("Y-m-d"), LABKEY.Filter.Types.DATE_GREATER_THAN_OR_EQUAL)
                            ],
                            columns: 'chargeId, unitCost',
                            failure: LDK.Utils.getErrorCallback(),
                            scope: this,
                            success: function (results) {
                                var unitCostField = item.up("form").getForm().findField("unitCost");
                                unitCostField.disabled = false;
                                unitCostField.setValue(results.rows[0] != null ? results.rows[0].unitCost : null);
                            }
                        });
                    }
                });
            }

            newItems.push(item);
        }, this);

        return newItems;
    },

    loadStoreOnValueChange : function (sourceField, targetFieldName, filterOn, newValue) {
        var targetField = sourceField.up("form").getForm().findField(targetFieldName);
        targetField.setValue(null);//reset
        var filter = LABKEY.Filter.create(filterOn, newValue, LABKEY.Filter.Types.EQUAL);
        targetField.store.filterArray = [filter];
        targetField.store.load();
    }
});