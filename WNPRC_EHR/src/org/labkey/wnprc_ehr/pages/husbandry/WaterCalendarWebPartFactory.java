package org.labkey.wnprc_ehr.pages.husbandry;

import org.labkey.api.view.HttpView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.Portal;
import org.labkey.api.view.BaseWebPartFactory;
import org.labkey.api.view.ViewContext;
import org.labkey.api.view.WebPartView;

import javax.validation.constraints.NotNull;
import java.io.PrintWriter;

public class WaterCalendarWebPartFactory extends BaseWebPartFactory

{
    private Portal.WebPart _webPart;

    public WaterCalendarWebPartFactory(){super ("Water Calendar");}

    public WebPartView getWebPartView (@NotNull ViewContext portalCtx, @NotNull Portal.WebPart webpart){
        JspView view = new JspView("/org/labkey/wnprc_ehr/pages/husbandry/WaterCalendar.jsp");
        view.setTitle("Water Calendar");
        view.setFrame(WebPartView.FrameType.PORTAL);
        return view;
    }
}
