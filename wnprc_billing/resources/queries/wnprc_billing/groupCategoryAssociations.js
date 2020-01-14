require("ehr/triggers").initScript(this);

var chargeableItemCategories = {};

function onInit(event, helper){

    LABKEY.Query.selectRows({
        requiredVersion: 9.1,
        schemaName: 'ehr_billing',
        queryName: 'chargeableItemCategories',
        columns: ['rowId, name'],
        scope: this,
        success: function (results) {

            var rows = results.rows;

            for(var i=0; i< rows.length; i++) {
                var row = rows[i];
                chargeableItemCategories[row["name"]["value"]] = row["rowId"]["value"];
            }
        },
        failure: function (error) {
            console.log("Error getting data from ehr_billing.chargeableItemCategories while uploading groupCategoryAssociations data: " + error);
        }
    });
}

function onInsert(helper, scriptErrors, row, oldRow) {

    if (!chargeableItemCategories[row.chargeCategoryName]) {

        EHR.Server.Utils.addError(scriptErrors, 'chargeCategoryName', "'" + row.chargeCategoryName + "' is not a valid category. If this is a new category, please ehr_billing.chargeableItemCategories table by going to 'CHARGEABLE ITEM CATEGORIES' link on the main Finance page.", 'ERROR');
        return false;
    }

    row.chargeCategoryId = chargeableItemCategories[row.chargeCategoryName];
}