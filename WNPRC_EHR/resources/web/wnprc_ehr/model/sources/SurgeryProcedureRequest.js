EHR.model.DataModelManager.registerMetadata('SurgeryProcedureRequest', {
    byQuery: {
        'ehr.requests': {
            sendemail: {
                editorConfig: {
                    checked: true
                }
            },
        },
        'study.surgery_procedure': {
            QCState: {
                hidden: true
            },
            Id: {
                editorConfig: {
                    plugins: ['wnprc-animalfield']
                }
            },
            apptid: {
                hidden: true
            },
            procedurecategory: {
                editorConfig: {
                    plugins: ['wnprc-procedurefield']
                }
            },
            procedurename: {
                xtype: 'wnprc-surgeryprocedurenamefield'
            },
            location: {
                xtype: 'wnprc-surgeryprocedureroomfield'
            },
            date: {
                defaultValue: Ext4.Date.add(new Date()),//'2018-12-05 11:00 AM',
                editorConfig: {
                    dateConfig: {
                        minValue: Ext4.Date.add(new Date(), Ext4.Date.DAY, 1)
                    }
                }//,
                // setInitialValue: function(v){
                //     var date = (new Date()).add(Date.DAY, 1);
                //     date.setHours(8);
                //     date.setMinutes(0);
                //     return v || date;
                // }
            },
            enddate: {
                editorConfig: {
                    dateConfig: {
                        minValue: Ext4.Date.add(new Date(), Ext4.Date.DAY, 1)
                    },
                    timeConfig: {
                        minValue: '10:00 AM'
                    }
                }//,
                // setInitialValue: function(v){
                //     var date = (new Date()).add(Date.DAY, 1);
                //     date.setHours(10);
                //     date.setMinutes(0);
                //     return v || date;
                // }
            },
            project: {
                xtype: 'wnprc-projectentryfield',
            },
            account: {
                //nothing
            },
            consultRequest: {
                //nothing
            },
            biopsyNeeded: {
                //nothing
            },
            surgerytechneeded: {
                //nothing
            },
            spineeded: {
                //nothing
            },
            vetneeded: {
                //nothing
            },
            vetneededreason: {
                height: 100,
                width: 400
            },
            equipment: {
                height: 100,
                width: 400
            },
            drugslab: {
                height: 100,
                width: 400
            },
            drugssurgery: {
                height: 100,
                width: 400
            },
            comments: {
                height: 100,
                width: 400
            },
            statuschangereason: {
                hidden: true
            }
        },
        'study.drug': {
            code: {
                //shownInGrid: false,
                editorConfig: {
                    xtype: 'ehr-snomedcombo',
                    defaultSubset: 'Drugs and Procedures'
                }
            }
        }
    }
});