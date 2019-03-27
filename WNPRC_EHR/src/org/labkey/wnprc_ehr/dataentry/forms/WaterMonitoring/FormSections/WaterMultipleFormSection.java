package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections;

import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleGridSection;

public class WaterMultipleFormSection extends SimpleGridSection
{
    public WaterMultipleFormSection (){

        super("study", "watergiven", "Water Given");
        setClientStoreClass("WNPRC.ext.data.SingleAnimal.WaterClientStore");
        setAllowBulkAdd(true);


    }
}
