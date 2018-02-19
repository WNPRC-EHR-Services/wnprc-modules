package org.labkey.wnprc_ehr.dataentry.forms.FoodDeprives.FormSections;

import org.labkey.api.ehr.EHRService;
import org.labkey.api.view.template.ClientDependency;
import org.labkey.wnprc_ehr.dataentry.generics.sections.SimpleGridSection;

import java.util.List;

/**
 * Created by fdnicolalde on 3/9/16.
 */
public class FoodDeprivesFormSections extends SimpleGridSection
{
    boolean _isRequest = false;
    private String formName = new String ();

    public FoodDeprivesFormSections(String parentFormName)
    {

        super("study", "foodDeprives", "Food Deprives", EHRService.FORM_SECTION_LOCATION.Body);
        this.addConfigSource("Task");
        addClientDependency(ClientDependency.fromPath("ehr/window/AddScheduledFoodDeprivesWindow.js"));
        addClientDependency(ClientDependency.fromPath("ehr/window/AddStartedFoodDeprivesWindow.js"));
        addClientDependency(ClientDependency.fromPath("wnprc_ehr/ext4/components/UWBoxSelect.css"));
        addClientDependency(ClientDependency.fromPath("ehr/buttons/husbandryButtons.js"));
        _showLocation = true;
        formName  = parentFormName;
    }

    public FoodDeprivesFormSections(boolean isRequest, EHRService.FORM_SECTION_LOCATION location)
    {
        super("study", "foodDeprives", "Food Deprives", location);
        addClientDependency(ClientDependency.fromPath("ehr/window/AddScheduledFoodDeprivesWindow.js"));
        addClientDependency(ClientDependency.fromPath("ehr/window/AddStartedFoodDeprivesWindow.js"));
        addClientDependency(ClientDependency.fromPath("wnprc_ehr/ext4/components/UWBoxSelect.css"));
        addClientDependency(ClientDependency.fromPath("ehr/buttons/husbandryButtons.js"));

        _isRequest = isRequest;
    }

    @Override
    public List<String> getTbarMoreActionButtons()
    {
        List<String> defaultButtons = super.getTbarMoreActionButtons();
        defaultButtons.add("REPEAT_SELECTED");

        return defaultButtons;
    }
    public List<String> getTbarButtons()
    {
        List<String> defaultButtons = super.getTbarButtons();

        if (formName.contains("Start")){
            defaultButtons.add(0,"ADDDEPRIVES");
            defaultButtons.add(1,"ADDSTARTTIME");
        }
        if (formName.contains("Complete")){
            defaultButtons.add(0,"ADDSTARTEDDEPRIVES");
            defaultButtons.add(1,"ADDRESTORETIME");

        }



        defaultButtons.remove("COPYFROMSECTION");
        defaultButtons.remove("TEMPLATES");

        return defaultButtons;
    }

}
