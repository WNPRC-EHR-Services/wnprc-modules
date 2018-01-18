package org.labkey.breeding.dataentry;

import org.labkey.api.ehr.dataentry.AnimalDetailsFormSection;
import org.labkey.api.ehr.dataentry.DataEntryFormContext;
import org.labkey.api.ehr.dataentry.SimpleFormPanelSection;
import org.labkey.api.ehr.dataentry.TaskForm;
import org.labkey.api.ehr.dataentry.TaskFormSection;
import org.labkey.api.module.Module;

import java.util.Arrays;

@SuppressWarnings("unused") // loaded in BreedingModule via Reflections
public class UltrasoundForm extends TaskForm
{
    private static final String NAME = "Ultrasound";

    public UltrasoundForm(DataEntryFormContext ctx, Module owner)
    {
        super(ctx, owner, NAME, NAME, $Constants.FORM_CATEGORY, Arrays.asList(
                new TaskFormSection(),
                new AnimalDetailsFormSection(),
                new SimpleFormPanelSection("study", "ultrasounds", NAME)));
    }
}
