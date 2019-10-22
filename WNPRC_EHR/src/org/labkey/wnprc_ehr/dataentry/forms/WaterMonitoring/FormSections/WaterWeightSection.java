package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections;

import org.labkey.api.view.template.ClientDependency;
import org.labkey.api.ehr.dataentry.SimpleFormPanelSection;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleFormSection;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SlaveFormSection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WaterWeightSection extends SlaveFormSection
{
    public WaterWeightSection() {

        super("study", "Weight", "Weight");

        maxItemsPerColumn = 1;
        //setClientStoreClass("wnprc.ext.data.HusbandryServerStore");
       //this.addClientDependency(ClientDependency.fromPath("wnprc_ehr/data/HusbandryServerStore.js"));
    }
    @Override
    public Set<String> getSlaveFields(){
        Set<String> fields = new HashSet<>();

        fields.add("Id");
        fields.add("date");

        return fields;
    }
    @Override
    protected List<String> getFieldNames(){return Arrays.asList("Id", "date", "weight", "remark");}

}