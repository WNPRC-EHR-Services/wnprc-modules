EHR.DataEntryUtils.registerGridButton('ADDANIMALS_WITH_WNPRC_BILLING_BULK_EDIT', function(config){
    return Ext4.Object.merge({
        text: 'Add Batch',
        tooltip: 'Click to add a batch of animals, either as a list or by location',
        handler: function(btn){
            var grid = btn.up('gridpanel');

            Ext4.create('EHR.window.AddAnimalsWindow', {
                targetStore: grid.store,
                formConfig: grid.formConfig,
                bulkEditCheckDisabled: false,
                customBulkEditWindow: 'WNPRC_Billing.window.BulkEditWindow'
            }).show();
        }
    }, config);
});