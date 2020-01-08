Ext4.define('WNPRC_Billing.form.field.MiscChargesDateField', {
    extend: 'Ext.form.field.Date',
    alias: 'widget.wnprc_billing-miscchargesdatefield',

    initComponent: function(){

        this.addListener({
            scope:this,
            change: function (combo, val) {

                //For Bulk Edit window
                if (this.up("form") && this.up("form").getForm()) {

                    var unitCostField = this.up("form").getForm().findField("unitCost");
                    if (unitCostField) {
                        unitCostField.setValue(null);
                    }

                    var chargeIdField = this.up("form").getForm().findField("chargeId");
                    if (chargeIdField) {
                        chargeIdField.setValue(null);
                    }

                    var chargetypeField = this.up("form").getForm().findField("chargetype");
                    if (chargetypeField) {
                        chargetypeField.setValue(null);
                    }

                }
                //for data entry grid
                else {
                    //if date is changed, reset below values since unitCost is retrieved from ehr_billing.chargeRates based on the date entered along with its coupled selections (chargetype (labeled as Charge Unit), which is coupled with chargeId (labeled as Charge Item))
                    EHR.DataEntryUtils.setSiblingFields(combo, {
                        chargetype: null,
                        chargeId: null,
                        unitCost: null
                    });
                }

            }});
        this.callParent();
    }
});