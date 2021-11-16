EHR.model.DataModelManager.registerMetadata('MultipleSurgeryProcedureRequest', {
    byQuery: {
        'ehr.requests': {
            sendemail: {
                editorConfig: {
                    checked: true
                }
            },
            priority: {
                hidden: true
            },
            QCState: {
                hidden: true
            }
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
            procedurename: {
                xtype: "wnprc-groupedcheckcombo",
                lookup: {
                    schemaName: 'wnprc',
                    queryName: 'procedure_names_with_headers',
                    displayColumn: 'displayname',
                    keyColumn: 'name',
                    columns: 'displayname,name,category/displayname,firstCategoryItem',
                    sort: 'category,displayname'
                }
            },
            procedureunit: {

            },
            date: {
                defaultValue: Ext4.Date.add(new Date()),//'2018-12-05 11:00 AM',
                editorConfig: {
                    dateConfig: {
                        minValue: Ext4.Date.add(new Date(), Ext4.Date.DAY, 1)
                    }
                }//,
                // setInitialValue: function(v){
                //     var date = (new Date()).add(Date.DAY, 2);
                //     date.setHours(9);
                //     date.setMinutes(30);
                //     return v || date;
                // }
            },
            startTableTime: {
                xtype: 'xdatetime',
                editorConfig: {
                    dateFormat: 'Y-m-d',
                    timeFormat: 'H:i'
                },
            },
            // enddate: {
            //     editorConfig: {
            //         dateConfig: {
            //             minValue: Ext4.Date.add(new Date(), Ext4.Date.DAY, 1)
            //         },
            //         timeConfig: {
            //             minValue: '10:00 AM'
            //         }
            //     }//,
            //     // setInitialValue: function(v){
            //     //     var date = (new Date()).add(Date.DAY, 1);
            //     //     date.setHours(10);
            //     //     date.setMinutes(0);
            //     //     return v || date;
            //     // }
            // },
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
            Id: {
                shownInGrid: false,
                hidden: true
            },
            project: {
                shownInGrid: false,
                hidden: true
            },
            account: {
                shownInGrid: false,
                hidden: true
            },
            code: {
                header: 'Drug',
                //shownInGrid: false,
                editorConfig: {
                    xtype: 'ehr-snomedcombo',
                    defaultSubset: 'Drugs and Procedures'
                }
            }
        },
        'wnprc.procedure_scheduled_rooms': {
            objectid: {
                hidden: true
            },
            event_id: {
                hidden: true
            },
            created: {
                hidden: true
            },
            modified: {
                hidden: true
            },
            date: {
            },
            enddate: {
            }
        },
        'study.foodDeprives': {
            Id: {
                shownInGrid: false,
                hidden: true
            },
            project: {
                shownInGrid: false,
                hidden: true
            },
            account: {
                shownInGrid: false,
                hidden: true
            },
            date: {
                getInitialValue: function(val, record){
                    if (val){
                        return val;
                    }

                    let collectionId = record.storeCollection.collectionId;
                    let storeName = collectionId + "-" + "surgery_procedure";
                    let store = Ext4.StoreManager.get(storeName);
                    let procedureRecord = null;
                    if(store) {
                        procedureRecord = store.getAt(0);
                    }
                    let date = null;
                    if(procedureRecord) {
                        date = procedureRecord.get('date');
                    }

                    return date;
                }
            }
        }
    }
});