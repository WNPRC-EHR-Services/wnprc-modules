package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections;

import org.labkey.api.view.template.ClientDependency;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleGridSection;

import java.util.List;

public class WaterOrderFormSection extends SimpleGridSection
{
    public WaterOrderFormSection(){
        super ("study", "waterOrders", "Water Daily Amount");
        this.setAllowBulkAdd(true);
        addClientDependency(ClientDependency.fromPath("wnprc_ehr/data/HusbandryClientStore.js"));
        addClientDependency(ClientDependency.fromPath("wnprc_ehr/ext4/windows/AddWaterWindow.js"));
        setClientStoreClass("WNPRC.ext.data.SingleAnimal.WaterClientStore");
    }
    public WaterOrderFormSection(String sectionTitle){
        super ("study", "waterOrders", sectionTitle);
        this.setAllowBulkAdd(true);
        addClientDependency(ClientDependency.fromPath("wnprc_ehr/data/HusbandryClientStore.js"));
        addClientDependency(ClientDependency.fromPath("wnprc_ehr/ext4/windows/AddWaterWindow.js"));
        setClientStoreClass("WNPRC.ext.data.SingleAnimal.WaterClientStore");
    }
    public List<String> getTbarButtons(){
        List<String> defaultButtons = super.getTbarButtons();

        defaultButtons.add("ADDWATERS");

        return defaultButtons;

    }
}
