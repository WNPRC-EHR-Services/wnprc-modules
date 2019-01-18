package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring;

import org.labkey.api.ehr.dataentry.AnimalDetailsFormSection;
import org.labkey.api.ehr.dataentry.DataEntryFormContext;
import org.labkey.api.ehr.dataentry.FormSection;
import org.labkey.api.ehr.dataentry.TaskForm;
import org.labkey.api.ehr.dataentry.TaskFormSection;
import org.labkey.api.module.Module;
import org.labkey.api.view.template.ClientDependency;
import org.labkey.wnprc_ehr.WNPRCConstants;
import org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections.WaterFormSections;
import org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections.WaterWeightSection;
import org.labkey.wnprc_ehr.dataentry.forms.Weight.FormSections.WeightSection;
import org.labkey.wnprc_ehr.dataentry.generics.sections.restraintFormSection;

import java.util.Arrays;

public class EnterWater extends TaskForm
{
    public static final String NAME = "Enter Water";

    public EnterWater(DataEntryFormContext ctx, Module owner)
    {
        super(ctx, owner, NAME, "Enter " + NAME, WNPRCConstants.DataEntrySections.CLINICAL_SPI, Arrays.asList(
                new TaskFormSection(),
                new AnimalDetailsFormSection(),
                new WaterFormSections(),
                new WaterWeightSection(),
                new restraintFormSection()
        ));
        this.addClientDependency(ClientDependency.fromPath("wnprc_ehr/model/sources/Husbandry.js"));

        for (FormSection section : this.getFormSections()){
            section.addConfigSource("Husbandry");
        }

       // setStoreCollectionClass("EHR.data.WaterStore");
    }




}
