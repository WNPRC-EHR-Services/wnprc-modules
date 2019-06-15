/*
 * Copyright (c) 2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */

Ext4.namespace('WNPRC_BILLING.Invoices');

WNPRC_BILLING.Invoices = new function(){
    return {

        downloadInvoicesBtn: function(dataRegion){

            var selected = dataRegion.getSelected();

            LABKEY.Ajax.request({
                url: LABKEY.ActionURL.buildURL('wnprc_billing', 'DownloadInvoices.api'),
                method: 'POST',
                success: function (request) {

                }
            });

        }

    }
}
