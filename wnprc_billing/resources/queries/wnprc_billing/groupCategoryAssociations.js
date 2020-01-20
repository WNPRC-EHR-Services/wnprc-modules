require("ehr/triggers").initScript(this);

var chargeableItemCategories = {};
var groups = {};

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

    LABKEY.Query.selectRows({
        requiredVersion: 9.1,
        schemaName: 'ehr_billing',
        queryName: 'chargeUnits',
        columns: ['groupName'],
        scope: this,
        success: function (results) {

            var rows = results.rows;

            for(var i=0; i< rows.length; i++) {
                var row = rows[i];
                groups[row["groupName"]["value"]] = row["groupName"]["value"];
            }
        },
        failure: function (error) {
            console.log("Error getting data from ehr_billing.chargeUnits while uploading groupCategoryAssociations data: " + error);
        }
    });
}

function onInsert(helper, scriptErrors, row, oldRow) {

    var category = row.chargeCategoryId; //Text values under 'Category' header are getting set to chargeCategoryId field.

    if (!chargeableItemCategories[category]) {

        EHR.Server.Utils.addError(scriptErrors, 'chargeCategoryId', "'" + category + "' is not a valid category. If this is a new category, please add to ehr_billing.chargeableItemCategories table by going to 'CHARGEABLE ITEM CATEGORIES' link on the main Finance page.", 'ERROR');
        return false;
    }

    if (!groups[row.chargeGroupName]) {

        EHR.Server.Utils.addError(scriptErrors, 'chargeGroupName', "'" + row.chargeGroupName + "' is not a valid group. If this is a new group, please add to ehr_billing.chargeUnits table by going to 'GROUPS' link on the main Finance page.", 'ERROR');
        return false;
    }

    row.chargeCategoryId = chargeableItemCategories[category]; //set chargeCategoryId with expected rowid value
}