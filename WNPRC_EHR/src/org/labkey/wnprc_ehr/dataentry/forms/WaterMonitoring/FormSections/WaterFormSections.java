package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections;

import org.labkey.api.ehr.EHRService;
import org.labkey.api.ehr.dataentry.SimpleFormPanelSection;
import org.labkey.api.view.template.ClientDependency;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleFormSection;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleGridSection;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SlaveFormSection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WaterFormSections extends SimpleFormSection
{
    public WaterFormSections ()
    {
        super ("study", "watergiven", "Water Given");

        setClientStoreClass("WNPRC.ext.data.SingleAnimal.WaterClientStore");


       /* this.addConfigSource("Task");
        _showLocation = true;*/

    }

}
