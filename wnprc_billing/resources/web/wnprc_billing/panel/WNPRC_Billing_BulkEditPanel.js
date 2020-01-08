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

                item.on('change', function(field, newValue){
                     var investigatorField = field.up("form").getForm().findField("investigator");
                     investigatorField.setValue(null);
                        var filter = LABKEY.Filter.create('project', newValue, LABKEY.Filter.Types.EQUAL);
                        investigatorField.store.filterArray = [filter];
                        investigatorField.store.load();
                        investigatorField.store.on('load', function(){
                            investigatorField.store.fireEvent('datachanged');
                            investigatorField.bindStore(investigatorField.store);
                            investigatorField.setDisabled(false);
                        }, this, {single: true});
                    });
            }

            if (item.name === 'investigator') {

                item.on('beforerender', function(field){

                    var projectField = field.up("form").getForm().findField("project");
                    if (projectField) {
                        var filter = LABKEY.Filter.create('project', projectField.value, LABKEY.Filter.Types.EQUAL);
                        field.store.filterArray = [filter];
                        field.store.load();
                    }

                    var debitedAcctField = field.up("form").getForm().findField("debitedAccount");
                    if (debitedAcctField) {
                        var filter = LABKEY.Filter.create('alias', debitedAcctField.value, LABKEY.Filter.Types.EQUAL);
                        field.store.filterArray = [filter];
                        field.store.load();
                    }

                });
            }

            if (item.name === 'chargetype') {

                item.on('change', function(field, newValue){
                    var chargeIdField = field.up("form").getForm().findField("chargeId");
                    var filter = LABKEY.Filter.create('departmentCode', newValue, LABKEY.Filter.Types.EQUAL);
                    chargeIdField.store.filterArray = [filter];
                    chargeIdField.store.load();
                    chargeIdField.store.on('load', function(){
                        chargeIdField.store.fireEvent('datachanged');
                        chargeIdField.bindStore(chargeIdField.store);
                        chargeIdField.setDisabled(false);
                    }, this, {single: true});
                });
            }

            if (item.name === 'chargeId') {

                item.valueField = 'rowid';
                item.displayField = 'name';

                item.on('beforerender', function (field) {

                    var chargeTypeVal = field.up("form").getForm().findField("chargetype").value;
                    var filter = LABKEY.Filter.create('departmentCode', chargeTypeVal, LABKEY.Filter.Types.EQUAL);
                    field.store.filterArray = [filter];
                    field.store.load();
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
});