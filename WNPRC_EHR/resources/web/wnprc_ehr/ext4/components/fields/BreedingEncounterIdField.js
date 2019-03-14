/*
 * Copyright (c) 2013-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * This field is used to display EHR projects.  It contains a custom template for the combo list which displays both the project and protocol.
 * It also listens for participantchange events and will display only the set of allowable projects for the selected animal.
 *
 * @cfg includeDefaultProjects defaults to true
 */
Ext4.define('WNPRC.form.field.BreedingEncounterIdField', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.wnprc-breedingencounteridfield',

    fieldLabel: 'breedingencounterid',
    typeAhead: true,
    forceSelection: true, //NOTE: has been re-enabled, but it is important to let the field get set prior to loading
    emptyText:'',
    disabled: false,
    matchFieldWidth: false,
    includeDefaultProjects: true,

    initComponent: function(){
        var ctx = EHR.Utils.getEHRContext();

        this.allBreedingEncounterStore = new LABKEY.ext4.data.Store({
            type: 'labkey-store',
            containerPath: ctx ? ctx['EHRStudyContainer'] : null,
            schemaName: 'study',
            queryName: 'breeding_encounters',
            columns: 'lsid,sireid,date,enddate',
            //filterArray: [LABKEY.Filter.create('enddate', null, LABKEY.Filter.Types.ISBLANK)],
            sort: '-date',
            storeId: ['study', 'breeding_encounters', 'Id', 'lsid'].join('||'),
            autoLoad: true
        });

        this.trigger2Cls = Ext4.form.field.ComboBox.prototype.triggerCls;
        this.onTrigger2Click = Ext4.form.field.ComboBox.prototype.onTriggerClick;

        Ext4.apply(this, {
            displayField: 'sireid',
            valueField: 'lsid',
            queryMode: 'local',
            validationDelay: 500,
            //NOTE: unless i have this empty store an error is thrown
            store: {
                type: 'labkey-store',
                schemaName: 'study',
                sql: this.makeSql(),
                sort: '-date',
                autoLoad: false,
                loading: true,
                listeners: {
                    scope: this,
                    delay: 50,
                    load: function(store){
                        this.resolveBreedingEncounterFromStore();
                        this.getPicker().refresh();
                    }
                }
            },
            listeners: {
                scope: this,
                beforerender: function(field){
                    var target = field.up('form');
                    if (!target)
                        target = field.up('grid');

                    LDK.Assert.assertNotEmpty('Unable to find form or grid', target);
                    if (target) {
                        field.mon(target, 'animalchange', field.getBreedingEncounters, field);
                    }
                    else {
                        console.error('Unable to find target');
                    }

                    //attempt to load for the bound Id
                    this.getBreedingEncounters();
                }
            }
        });

        this.listConfig = this.listConfig || {};
        Ext4.apply(this.listConfig, {
            innerTpl: this.getInnerTpl(),
            getInnerTpl: function(){
                return this.innerTpl;
            },
            style: 'border-top-width: 1px;' //this was added in order to restore the border above the boundList if it is wider than the field
        });

        this.callParent(arguments);
    },

    getInnerTpl: function(){
        return ['Sire: {[values["sireid"] + "<br>" + values["date"] + " to<br>" + values["enddate"]]}'];
    },

    trigger1Cls: 'x4-form-search-trigger',

    onTrigger1Click: function(){
        var boundRecord = EHR.DataEntryUtils.getBoundRecord(this);
        if (!boundRecord){
            Ext4.Msg.alert('Error', 'Unable to locate associated animal Id');
            return;
        }

        var id = boundRecord.get('Id');
        if (!id){
            Ext4.Msg.alert('Error', 'No Animal Id Provided');
            return;
        }

        this.getBreedingEncounters(id);
    },

    getDisallowedProtocols: function(){
        return null;
    },

    makeSql: function(id){
        if (!id && !this.includeDefaultProjects)
            return;

        //avoid unnecessary reloading
        var key = id;
        if (this.loadedKey == key){
            return;
        }
        this.loadedKey = key;

        var sql = 'select lsid,sireid,to_char(date, \'Mon DD, YYYY HH24:MI\') as date,to_char(enddate, \'Mon DD, YYYY HH24:MI\') as enddate \
                    from breeding_encounters be \
                    where Id = \'' + id + '\' \
                    and not exists (select * \
                                    from pregnancies p \
                                    where p.breedingencounterid = be.lsid) \
                    order by date desc \
                    limit 5'.replace(/\s+/g, ' ');
        return sql;
    },

    getBreedingEncounters : function(id){
        var boundRecord = EHR.DataEntryUtils.getBoundRecord(this);
        if (!boundRecord){
            console.warn('no bound record found');
        }

        if (boundRecord && boundRecord.store){
            LDK.Assert.assertNotEmpty('BreedingEncounterIdField is being used on a store that lacks an Id field: ' + boundRecord.store.storeId, boundRecord.fields.get('Id'));
        }

        if (!id && boundRecord)
            id = boundRecord.get('Id');

        this.emptyText = 'Select breeding encounter...';
        var sql = this.makeSql(id);
        if (sql){
            this.store.loading = true;
            this.store.sql = sql;
            this.store.removeAll();
            this.store.load();
        }
    },

    setValue: function(val){
        var rec;
        if (Ext4.isArray(val)){
            val = val[0];
        }

        if (val && Ext4.isPrimitive(val)){
            rec = this.store.findRecord('breedingencounterid', val);

            if (!rec){
                rec = this.resolveBreedingEncounter(val);
            }
        }

        if (rec){
            val = rec;
        }

        // NOTE: if the store is loading, Combo will set this.value to be the actual model.
        // this causes problems downstream when other code tries to convert that into the raw datatype
        if (val && val.isModel){
            val = val.get(this.valueField);
        }

        this.callParent([val]);
    },

    resolveBreedingEncounterFromStore: function(){
        var val = this.getValue();
        if (!val || this.isDestroyed)
            return;

        LDK.Assert.assertNotEmpty('Unable to find store in BreedingEncounterIdField', this.store);
        var rec = this.store ? this.store.findRecord('breedingencounterid', val) : null;
        if (rec){
            return;
        }

        rec = this.allBreedingEncounterStore.findRecord('breedingencounterid', val);
        if (rec){
            var newRec = this.store.createModel({});
            newRec.set({
                lsid: rec.data.lsid,
                sireid: rec.data.sireid,
                date: rec.data.date,
                enddate: rec.data.enddate,
                fromClient: true
            });

            this.store.insert(0, newRec);

            return newRec;
        }
    },

    resolveBreedingEncounter: function(val){
        if (this.allBreedingEncounterStore.isLoading()){
            this.allBreedingEncounterStore.on('load', function(store){
                var newRec = this.resolveBreedingEncounterFromStore();
                if (newRec)
                    this.setValue(val);
            }, this, {single: true});
        }
        else {
            this.resolveBreedingEncounterFromStore();
        }
    }
});