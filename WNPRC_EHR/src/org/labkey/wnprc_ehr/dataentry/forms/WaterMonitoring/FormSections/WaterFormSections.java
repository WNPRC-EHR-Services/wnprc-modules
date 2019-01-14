package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections;

import org.labkey.api.ehr.EHRService;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleGridSection;

public class WaterFormSections extends SimpleGridSection
{
    public WaterFormSections (String parentFormName)
    {
        super ("study", "watergiven", "Water Given", EHRService.FORM_SECTION_LOCATION.Body);
        this.addConfigSource("Task");
    }
}
