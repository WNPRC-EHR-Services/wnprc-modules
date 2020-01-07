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

            if (item.cfg.xtype === 'wnprc_billing-investigatorfield' || item.cfg.xtype === 'wnprc_billing-chargeitemfield' || item.cfg.xtype === 'wnprc_billing-chargetypeentryfield') {
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

                item.on('afterrender', function(field){

                    var projectVal = field.up("form").getForm().findField("project").value;
                    var filter = LABKEY.Filter.create('project', projectVal, LABKEY.Filter.Types.EQUAL);
                    field.store.filterArray = [filter];
                    field.store.load();
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

                item.valueField = 'rowId';
                item.displayField = 'name';

                item.on('afterrender', function(field){

                    var chargeTypeVal = field.up("form").getForm().findField("chargetype").value;
                    var filter = LABKEY.Filter.create('departmentCode', chargeTypeVal, LABKEY.Filter.Types.EQUAL);
                    field.store.filterArray = [filter];
                    field.store.load();
                });
            }

            newItems.push(item);
        }, this);

        return newItems;
    },
});