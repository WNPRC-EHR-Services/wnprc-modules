package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections;

import org.labkey.api.view.template.ClientDependency;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleGridSection;

import java.util.List;

public class WaterMultipleFormSection extends SimpleGridSection
{
    public WaterMultipleFormSection (){

        super("study", "watergiven", "Water Given");
        setClientStoreClass("WNPRC.ext.data.SingleAnimal.WaterClientStore");
        setAllowBulkAdd(true);

        addClientDependency(ClientDependency.fromPath("wnprc_ehr/ext4/windows/AddScheduleWaterWindow.js"));
        _showLocation = true;


    }
    @Override
    public List<String> getTbarButtons(){

        List<String> defaultButtons = super.getTbarButtons();

        defaultButtons.add("ADDSCHEDULEDWATERS");

        return defaultButtons;
    }
}