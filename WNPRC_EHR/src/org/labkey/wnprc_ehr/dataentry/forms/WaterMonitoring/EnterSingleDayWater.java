package org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring;

import org.labkey.api.ehr.dataentry.DataEntryFormContext;
import org.labkey.api.ehr.dataentry.TaskFormSection;
import org.labkey.api.module.Module;
import org.labkey.wnprc_ehr.WNPRCConstants;
import org.labkey.wnprc_ehr.dataentry.forms.WaterMonitoring.FormSections.WaterSingleDaySection;
import org.labkey.wnprc_ehr.dataentry.generics.forms.SimpleTaskForm;

import java.util.Arrays;

public class EnterSingleDayWater extends SimpleTaskForm
{
    public static final String NAME = "Enter Single Day Water";

    public EnterSingleDayWater(DataEntryFormContext ctx, Module owner){
        super (ctx, owner, NAME, NAME, WNPRCConstants.DataEntrySections.CLINICAL_SPI, Arrays.asList(
                new TaskFormSection(),
                new WaterSingleDaySection()
        ));

    }
}
