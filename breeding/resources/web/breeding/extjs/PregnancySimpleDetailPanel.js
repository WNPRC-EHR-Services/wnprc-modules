// noinspection JSUnresolvedVariable: Ext4 is pulled in by other code in the browser
(function (Ext4) {
    // noinspection JSUnusedGlobalSymbols: the functions are called by ExtJS
    Ext4.define('WNPRC.breeding.PregnancySimpleDetailPanel', {
        alias: 'widget.wnprc-pregnancysimpledetail',
        extend: 'Ext.form.Panel',
        initComponent: function () {
            // noinspection JSUnusedGlobalSymbols, JSUnresolvedExtXType: render is real, xtypes are just fine
            Ext4.apply(this, {
                bodyStyle: 'padding: 5px;'
            });

            // call the parent to set up the form panel
            this.callParent(arguments);
        },

        loadFromStore: function (store) {
            var children = [];
            var fields = store.getFields();
            var record = store.getAt(0);
            fields.each(function (field) {
                // skip any fields that are hidden or otherwise should not show in the view
                // noinspection JSUnresolvedFunction, JSUnresolvedVariable: defined in the LabKey libraries
                if (!LABKEY.ext4.Util.shouldShowInDetailsView(field))
                    return;

                // noinspection JSUnresolvedVariable, JSUnresolvedFunction: extFormatFn defined on the field (sometimes)
                var value = field.extFormatFn ? field.extFormatFn(record.get(field.name)) : record.get(field.name);
                if (record.raw && record.raw[field.name] && record.raw[field.name].url)
                    value = '<a href="' + record.raw[field.name].url + '" target="new">' + value + '</a>';

                // noinspection JSUnresolvedExtXType: displayfield is a form element type
                var child = {
                    fieldLabel: field.label || field.caption || field.name,
                    labelWidth: 200,
                    name: field.name,
                    value: value,
                    xtype: 'displayfield'
                };
                switch (field.inputType) {
                    case 'textarea':
                        // noinspection JSUnresolvedExtXType: textareafield is a form element type
                        child = Ext4.apply(child, {
                            anchor: '100%',
                            labelAlign: 'top',
                            readOnly: true,
                            xtype: 'textareafield'
                        });
                        break;
                    default:
                        // no-op
                        break;
                }
                children.push(child);
            }, this);

            // cycle the children of the panel to show the new fields
            // noinspection JSUnresolvedFunction: defined in Ext.AbstractComponent
            this.removeAll();
            this.add(children);
        }
    });
})(Ext4);
