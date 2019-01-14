package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring;

import org.labkey.api.ehr.dataentry.DataEntryFormContext;
import org.labkey.api.ehr.dataentry.FormSection;
import org.labkey.api.ehr.dataentry.TaskForm;
import org.labkey.api.ehr.dataentry.TaskFormSection;
import org.labkey.api.module.Module;
import org.labkey.api.view.template.ClientDependency;
import org.labkey.wnprc_ehr.WNPRCConstants;
import org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections.WaterFormSections;

import java.util.Arrays;

public class EnterWater extends TaskForm
{
    public static final String NAME = "Enter Water";

    public EnterWater(DataEntryFormContext ctx, Module owner)
    {
        super(ctx, owner, NAME, "Enter " + NAME, WNPRCConstants.DataEntrySections.CLINICAL_SPI, Arrays.asList(
                new TaskFormSection(),
                new WaterFormSections("Water")
        ));
        this.addClientDependency(ClientDependency.fromPath("wnprc_ehr/model/sources/Husbandry.js"));

        for (FormSection section : this.getFormSections()){
            section.addConfigSource("Husbandry");
        }

       // setStoreCollectionClass("EHR.data.WaterStore");
    }




}
