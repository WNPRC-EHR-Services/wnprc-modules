var WNPRC = require("wnprc_ehr/WNPRC").WNPRC;
var console = require("console");
var LABKEY = require("labkey");
require("ehr/triggers").initScript(this);

function onInit(event, helper){
    helper.setScriptOptions({
        allowDatesInDistantPast: true
    });
}

function beforeInsert(row, errors){
    if (this.extraContext.targetQC) {
        row.QCStateLabel = this.extraContext.targetQC;
    }
}

function onUpsert(helper, scriptErrors, row, oldRow){
    //updates the project field if its an actual project
    if (!isNaN(row.optionalproject)){
        row.project = row.optionalproject
    }

    //sanity checks for date fields
    if (row.anticipatedstartdate > row.anticipatedenddate) {
        EHR.Server.Utils.addError(scriptErrors, 'anticipatedenddate', 'Anticipated end date is before anticipated start date.')
    }

    //sanitize 'animalidstooffer' field
    var subjectArray = row.animalidstooffer;
    if (!subjectArray){
        return;
    }

    var subjectArray = WNPRC.Utils.splitIds(subjectArray);
    //after split, check if unique
    if (!WNPRC.Utils.unique(subjectArray))
        EHR.Server.Utils.addError(scriptErrors, 'animalidstooffer', 'Contains duplicate animal ids.', 'INFO');

    for (var i = 0; i < subjectArray.length; i++) {
        var id = subjectArray[i];
        EHR.Server.Utils.findDemographics({
            participant: id,
            helper: helper,
            scope: this,
            callback: function (data) {
                if (data) {
                    if (data['calculated_status'] && data.calculated_status !== 'Alive') {
                        EHR.Server.Utils.addError(scriptErrors, 'animalidstooffer', 'This animal (' + id + ') is not alive', 'INFO');
                    }
                    if (data['calculated_status'] == null) {
                        EHR.Server.Utils.addError(scriptErrors, 'animalidstooffer', 'This animal (' + id + ') does not exist', 'ERROR');
                    }
                }
            },
            failure: function (d) {
                EHR.Server.Utils.addError(scriptErrors, 'animalidstooffer', 'Communication error', 'ERROR');
            }
        });
    }
    row.animalidstooffer = subjectArray.join(";");
}

function onAfterInsert(helper,errors,row){
    var rowid = row.rowId;
    var hostName = 'https://' + LABKEY.serverName;
    console.log ("animal_requests.js: New request submitted, rowid: "+ rowid);
    var threadId = WNPRC.Utils.getJavaHelper().setUpMessageBoardThread(row, "/WNPRC/WNPRC_Units/Animal_Services/Assigns/Private/");
    WNPRC.Utils.getJavaHelper().updateRow(row, threadId, "wnprc", "animal_requests", "internalthreadrowid");
    var threadIdExternal = WNPRC.Utils.getJavaHelper().setUpMessageBoardThread(row, "/WNPRC/WNPRC_Units/Animal_Services/Assigns/Restricted/");
    WNPRC.Utils.getJavaHelper().updateRow(row, threadIdExternal, "wnprc", "animal_requests", "externalthreadrowid");
    WNPRC.Utils.getJavaHelper().sendAnimalRequestNotification(rowid, hostName);
}

function onAfterUpdate(helper,errors,row,oldRow){
    var rowid = row.rowId;
    var hostName = 'https://' + LABKEY.serverName;
    console.log ("animal_requests.js: New request updated, rowid: "+ rowid);
    WNPRC.Utils.getJavaHelper().sendAnimalRequestNotificationUpdate(rowid, row, oldRow, hostName);
}
