require("ehr/triggers").initScript(this);
var WNPRC = require("wnprc_ehr/WNPRC").WNPRC;

function onInit(event, scriptContext){
    scriptContext.quickValidation = true;
}

function onInsert(helper, scriptErrors, row, oldRow) {
    if (row.alternateIdentifier){
        console.log('Searching for record in temporary');
        LABKEY.Query.selectRows({
            schemaName:'study',
            queryName:'chemistryResultsTemp',
            filterArray:[
                LABKEY.Filter.create('alternateIdentifier', row.alternateIdentifier, LABKEY.Filter.Types.EQUAL),
            ],
            scope:this,
            success: function(data){
                if (data && data.rows && data.rows.length) {
                    var toUpdate = []
                    //toUpdate.push({lsid: data.rows[0].lsid, qcstate:EHR.Security.getQCStateByLabel('In Progress')});
                    toUpdate.push({lsid: data.rows[0].lsid, QCState: '2'});
                    LABKEY.Query.updateRows({
                       schemaName: 'study',
                       queryName: 'chemistryResultsTemp',
                       scope: this,
                       rows: toUpdate,
                       success: function(data){
                           console.log('Record on temporary table switch to InProgress');
                       },
                       failure: EHR.Server.Utils.onFailure
                   });
                } else {
                    console.log('No records to update in temporary table');
                }
            }
        });
    }
}

function onBecomePublic(errors,helper,row, oldRow){
    console.log('Calling onBecomingPublic');
    if (row && oldRow && row.alternateIdentifier){
        LABKEY.Query.selectRows({
            schemaName: 'study',
            queryName: 'chemistryResultsTemp',
            filterArray: [
                LABKEY.Filter.create('alternateIdentifier', row.alternateIdentifier, LABKEY.Filter.Types.EQUAL),
            ],
            scope: this,
            success: function (data) {
                console.log("LSID of row " + data.rows[0].lsid);
                let recordsToDelete = [];
                /*recordsToDelete.push({
                    lsid: data.rows[0].lsid,
                    QCState: EHR.Security.getQCStateByLabel('Delete Requested')
                });*/
                recordsToDelete.push({lsid:data.rows[0].lsid,QCState:'6'});
                if (recordsToDelete) {
                    console.log("records to delete " + recordsToDelete.length);
                    LABKEY.Query.updateRows({
                        schemaName: 'study',
                        queryName: 'chemistryResultsTemp',
                        scope: this,
                        rows: recordsToDelete,
                        success: function (data) {
                            console.log('Record on temporary table labeled for deletion');
                        },
                        failure: EHR.Server.Utils.onFailure
                    });
                }
            }
        });
    }
}
