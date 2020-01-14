/*
 * Copyright (c) 2018 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * This field is used to display WNPRC Investigator.
 */
Ext4.define('WNPRC_BILLING.form.field.InvestigatorField', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.wnprc_billing-investigatorfield',
    containerPath: LABKEY.moduleContext.ehr['EHRStudyContainer'],

    displayField: 'investigator',
    valueField: 'investigator',

    initComponent: function() {
        this.callParent(arguments);
        this.addListener({
            scope: this,
            focus: function() {
                //for data entry grid
                if (this.up("grid")) {
                    var projectVal = EHR.DataEntryUtils.getSiblingValue(this, "project");
                    var filter = undefined;
                    if (projectVal) {
                        filter = LABKEY.Filter.create('project', projectVal, LABKEY.Filter.Types.EQUAL)
                    }
                    var debitedAcctVal = EHR.DataEntryUtils.getSiblingValue(this, "debitedaccount");
                    if (debitedAcctVal) {
                       filter = LABKEY.Filter.create('alias', debitedAcctVal, LABKEY.Filter.Types.EQUAL);
                    }
                    this.store.filterArray = [filter];
                    this.store.load();
                }

                //for bulk edit window
                var form = this.up("form") ? this.up("form").getForm() : undefined;
                if (form) {

                    //for charges form with animal Ids, get investigator based on project selection
                    var projectField = form.findField("project");

                    if (projectField) {
                        var projectFilter = LABKEY.Filter.create('project', projectField.value, LABKEY.Filter.Types.EQUAL);
                        this.store = Ext4.create('LABKEY.ext4.data.Store', {
                            type: 'labkey-store',
                            schemaName: 'ehr',
                            queryName: 'projectsWithInvestigators',
                            sort: 'project',
                            filterArray: projectFilter,
                        });
                        this.store.load();
                    }
                    var debitedAcctField = form.findField("debitedaccount");

                    //for charges form without animal Ids, get investigator based on debited account selection
                    if (debitedAcctField) {
                        var aliasFilter = LABKEY.Filter.create('alias', debitedAcctField.value, LABKEY.Filter.Types.EQUAL);
                        this.store = Ext4.create('LABKEY.ext4.data.Store', {
                            type: 'labkey-store',
                            schemaName: 'ehr',
                            queryName: 'aliasesWithInvestigators',
                            columns: 'alias, investigatorId, investigatorName',
                            filterArray: aliasFilter,
                        });
                        this.store.load();
                    }
                }
            }
        });
        this.callParent();
    }
});