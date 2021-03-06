Ext4.define('WNPRC.ext.data.NecropsyStoreCollection', {
    extend: 'WNPRC.ext.data.TaskStoreCollection',

    //Set a custom type (wnprc-necropsyserverstore) for the storeConfig
    addServerStoreFromConfig: function(config){
        var storeConfig = Ext4.apply({}, config);
        Ext4.apply(storeConfig, {
            type: 'wnprc-necropsyserverstore',
            autoLoad: false,
            storeId: this.collectionId + '||' + LABKEY.ext4.Util.getLookupStoreId({lookup: config})
        });

        var store = this.serverStores.get(storeConfig.storeId);
        if (store){
            console.log('Store already defined: ' + store.storeId);
            return store;
        }

        store = Ext4.create('WNPRC.ext.data.NecropsyServerStore', storeConfig);

        this.addServerStore(store);

        return store;
    }
});