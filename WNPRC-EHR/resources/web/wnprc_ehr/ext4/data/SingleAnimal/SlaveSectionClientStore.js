Ext4.define('WNPRC.ext.data.SingleAnimal.SlaveSectionClientStore', {
    extend: 'EHR.data.DataEntryClientStore',

    constructor: function() {
        this.callParent(arguments);

        // Ensure that the following fields exist and are of the proper form.
        this.slaveFields = [];
        if (!Ext4.isArray(this.slaveFieldsToInclude)) { this.slaveFieldsToInclude = []; }
        if (!Ext4.isArray(this.slaveFieldsToExclude)) { this.slaveFieldsToExclude = []; }

        this.configureSlaveFieldsList();
    },

    insert: function(index, records) {
        var self = this;
        var masterClientStore = this.getMasterClientStore();

        if (!_.isArray(records)) {
            records = [records];
        }

        if (masterClientStore !== null) {
            var masterRecord = masterClientStore.getRange()[0];

            if (masterRecord !== null) {
                jQuery.each(records, function(i, record) {
                    record.beginEdit();
                    jQuery.each(self.getSlaveFields(), function(i, fieldName) {
                        record.set(fieldName, masterRecord.get(fieldName));
                    });
                    record.endEdit();
                });
            }
        }

        return this.callParent(arguments);
    },

    getMasterClientStore: function() {
        var masterClientStore = null;

        if (this.storeCollection) {
            this.storeCollection.clientStores.each(function(store) {
                if (WNPRC.ExtUtils.isInstanceOf(store, WNPRC.ext.data.SingleAnimal.MasterSectionClientStore)) {
                    masterClientStore = store;
                    return false;
                }
            });
        }

        return masterClientStore;
    },

    getSlaveFields: function() {
        return this.slaveFields;
    },

    /**
     * This is split out as a seperate method, so that child classes can reset their slave fields.  Note that this
     * won't trigger a master client store to update it's subscriptions, so you'd need to do that.
     */
    configureSlaveFieldsList: function() {
        // Add included slave fields.
        var self = this;
        this.slaveFields = [];
        var tempSlaveFields = [].concat(this.slaveFieldsToInclude);

        var exclusionLookup = {};
        jQuery.each(this.slaveFieldsToExclude, function(i, fieldName) {
            exclusionLookup[fieldName] = true;
        });

        // Remove excluded slave fields.
        jQuery.each(tempSlaveFields, function(i, fieldName) {
            if (!(fieldName in exclusionLookup)) {
                self.slaveFields.push(fieldName);
            }
        });
    }
});