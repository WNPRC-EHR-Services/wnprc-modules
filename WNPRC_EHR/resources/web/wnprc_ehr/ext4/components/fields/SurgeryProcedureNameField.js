/**
 * This field is used to display a list of pregnancies that the entry can be linked with.
 */
Ext4.define('WNPRC.ext.components.SurgeryProcedureNameField', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.wnprc-surgeryprocedurenamefield',

    fieldLabel: 'procedurename',
    typeAhead: true,
    forceSelection: true, //NOTE: has been re-enabled, but it is important to let the field get set prior to loading
    emptyText:'',
    disabled: false,
    matchFieldWidth: false,

    initComponent: function() {
        let ctx = EHR.Utils.getEHRContext();

        this.allSurgeryProcedureNameStore = new LABKEY.ext4.data.Store({
            type: 'labkey-store',
            containerPath: ctx ? ctx['EHRStudyContainer'] : null,
            schemaName: 'wnprc',
            queryName: 'surgery_procedure_name',
            columns: 'displayname,name,type',
            sort: 'displayname',
            storeId: ['wnprc', 'surgery_procedure_name', 'name', 'type'].join('||'),
            autoLoad: true
        });

        this.trigger2Cls = Ext4.form.field.ComboBox.prototype.triggerCls;
        this.onTrigger2Click = Ext4.form.field.ComboBox.prototype.onTriggerClick;

        Ext4.apply(this, {
            displayField: 'displayname',
            valueField: 'name',
            queryMode: 'local',
            //forceSelection: true,
            //matchFieldWidth: true,
            //anyMatch: true,
            //caseSensitive: false,
            //plugins: [Ext4.create('LDK.plugin.UserEditableCombo', {
            //    allowChooseOther: false,
            //})],
            validationDelay: 500,
            //NOTE: unless i have this empty store an error is thrown
            store: {
                type: 'labkey-store',
                schemaName: 'wnprc',
                sql: this.makeSql(),
                sort: 'name',
                autoLoad: false,
                loading: true,
                listeners: {
                    scope: this,
                    delay: 50,
                    load: function(store) {
                        this.resolveProcedureNameFromStore();
                        this.getPicker().refresh();
                    }
                }
            },
            listeners: {
                scope: this,
                beforerender: function(field) {
                    let target = field.up('form');
                    if (!target)
                        target = field.up('grid');

                    LDK.Assert.assertNotEmpty('Unable to find form or grid', target);
                    if (target) {
                        field.mon(target, 'procedurechange', field.getProcedureName, field);
                        field.mon(target, 'animalchange', field.getProcedureNameFromAnimalChange, field);
                    }
                    else {
                        console.error('Unable to find target');
                    }

                    //attempt to load for the bound Id
                    this.getProcedureName();
                }
            }
        });

        this.listConfig = this.listConfig || {};
        Ext4.apply(this.listConfig, {
            innerTpl: this.getInnerTpl(),
            getInnerTpl: function() {
                return this.innerTpl;
            },
            style: 'border-top-width: 1px;' //this was added in order to restore the border above the boundList if it is wider than the field
        });

        this.callParent(arguments);
    },

    getInnerTpl: function() {
        return ['{[values["displayname"]]}'];
    },

    trigger1Cls: 'x4-form-search-trigger',

    onTrigger1Click: function() {
        let boundRecord = EHR.DataEntryUtils.getBoundRecord(this);
        if (!boundRecord) {
            Ext4.Msg.alert('Error', 'Unable to locate associated animal Id');
            return;
        }

        let procedureType = boundRecord.get('procedureType');
        if (!procedureType) {
            Ext4.Msg.alert('Error', 'No Animal Id Provided');
            return;
        }

        this.getProcedureName(procedureType);
    },

    makeSql: function(procedure_type) {
        if (!procedure_type) {
            return;
        }

        //avoid unnecessary reloading
        let key = procedure_type;
        if (this.loadedKey === key) {
            return;
        }
        this.loadedKey = key;

        let sql = 'select name,displayname from wnprc.surgery_procedure_name';
        if (procedure_type && procedure_type.length > 0) {
            sql += ' where type = \'' + procedure_type.toLowerCase() + '\'';
        } else {
            sql += ' where type = \'\'';
        }
        return sql;
    },

    getProcedureNameFromAnimalChange: function() {
        let boundRecord = EHR.DataEntryUtils.getBoundRecord(this);
        if (!boundRecord) {
            console.warn('no bound record found');
        }

        if (boundRecord && boundRecord.store) {
            LDK.Assert.assertNotEmpty('SurgeryProcedureNameField is being used on a store that lacks an Id field: ' + boundRecord.store.storeId, boundRecord.fields.get('Id'));
        }

        let procedureType = boundRecord.get('procedureType');

        this.getProcedureName(procedureType);
    },

    getProcedureName: function(procedure_type) {
        let boundRecord = EHR.DataEntryUtils.getBoundRecord(this);
        if (!boundRecord) {
            console.warn('no bound record found');
        }

        if (boundRecord && boundRecord.store) {
            LDK.Assert.assertNotEmpty('SurgeryProcedureNameField is being used on a store that lacks an Id field: ' + boundRecord.store.storeId, boundRecord.fields.get('Id'));
        }

        if (!procedure_type && boundRecord) {
            procedure_type = boundRecord.get('procedureType');
        }

        this.emptyText = 'Select procedure...';

        let sql = this.makeSql(procedure_type);
        if (sql) {
            this.store.loading = true;
            this.store.sql = sql;
            this.store.removeAll();
            this.store.load();
        }
    },

    setValue: function(val) {
        let rec;
        if (Ext4.isArray(val)) {
            val = val[0];
        }

        if (val && Ext4.isPrimitive(val)) {
            rec = this.store.findRecord('procedurename', val);
            if (!rec) {
                rec = this.resolveProcedureName(val);
            }
        }

        if (rec) {
            val = rec;
        }

        // NOTE: if the store is loading, Combo will set this.value to be the actual model.
        // this causes problems downstream when other code tries to convert that into the raw datatype
        if (val && val.isModel) {
            val = val.get(this.valueField);
        }

        this.callParent([val]);
    },

    resolveProcedureNameFromStore: function() {
        let val = this.getValue();
        if (!val || this.isDestroyed)
            return;

        LDK.Assert.assertNotEmpty('Unable to find store in PregnancyIdField', this.store);
        let rec = this.store ? this.store.findRecord('procedurename', val) : null;
        if (rec) {
            return;
        }

        rec = this.allSurgeryProcedureNameStore.findRecord('procedurename', val);
        if (rec) {
            let newRec = this.store.createModel({})
            newRec.set({
                displayname: rec.data.displayname,
                name: rec.data.name,
                type: rec.data.type,
                fromClient: true
            });

            this.store.insert(0, newRec);
            return newRec;
        }
    },

    resolveProcedureName: function(val) {
        if (this.allSurgeryProcedureNameStore.isLoading()) {
            this.allSurgeryProcedureNameStore.on('load', function(store) {
                let newRec = this.resolveProcedureNameFromStore();
                if (newRec) {
                    this.setValue(val);
                }
            }, this, {single: true});
        }
        else {
            this.resolveProcedureNameFromStore();
        }
    }
});