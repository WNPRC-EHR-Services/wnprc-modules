Ext4.define('WNPRC_Billing.form.field.ChargeGroupEntryField', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.wnprc_billing-chargegroupentryfield',
    initComponent: function(){

        this.addListener({
            scope:this,
            select: function (combo, recs) {

                //if chargeGroup (labeled as Group) is changed, reset its coupled column chargeId (labeled as Charge Item), and unitCost (which is coupled with chargeId).
                EHR.DataEntryUtils.setSiblingFields(combo, {
                    chargeId: null,
                    unitCost: null
                });
            }});
        this.callParent();
    }
});