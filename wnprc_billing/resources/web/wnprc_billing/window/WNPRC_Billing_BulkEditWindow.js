/**
 * @cfg formConfig
 * @cfg targetStore
 * @cfg records
 * @cfg suppressConfirmMsg
 */
Ext4.define('WNPRC_Billing.window.BulkEditWindow', {
    extend: 'EHR.window.BulkEditWindow',

    initComponent: function(){

        this.callParent();
    },

    getItemCfg: function(){
        return [{
            html: 'This helper allows you to bulk edit records.  You can choose which fields to edit.  Initially, all fields are greyed out.  To set a value on a field, click the label.  You can disable it by clicking it again.  Please note that any enabled field will be set on the record, even if you did not enter a value.  This is sometimes useful if you want to clear a field across many records.',
            style: 'padding-bottom: 10px;',
            border: false
        },{
            xtype: 'wnprc_billing-bulkeditpanel',
            title: null,
            suppressConfirmMsg: this.suppressConfirmMsg,
            insertIndex: this.insertIndex,
            formConfig: this.formConfig,
            targetStore: this.targetStore,
            records: this.records,
            getButtons: Ext4.emptyFn,
            listeners: {
                scope: this,
                bulkeditcomplete: this.onBulkEditComplete
            }
        }]
    },
});

EHR.DataEntryUtils.registerGridButton('WNPRC_BILLING_BULKEDIT', function(config){
    return Ext4.Object.merge({
        text: 'Bulk Edit',
        tooltip: 'Click to edit the selected rows in bulk',
        handler: function(btn){
            var grid = btn.up('gridpanel');
            var selected = grid.getSelectionModel().getSelection();
            if (!selected || !selected.length) {
                Ext4.Msg.alert('Error', 'No records selected');
                return;
            }
            Ext4.create('WNPRC_Billing.window.BulkEditWindow', {
                targetStore: grid.store,
                formConfig: grid.formConfig,
                records: selected
            }).show();
        }
    }, config);
});