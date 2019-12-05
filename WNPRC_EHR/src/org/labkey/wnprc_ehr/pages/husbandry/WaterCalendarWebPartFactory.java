package org.labkey.wnprc_ehr.pages.husbandry;

import org.labkey.api.view.HttpView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.Portal;
import org.labkey.api.view.BaseWebPartFactory;
import org.labkey.api.view.ViewContext;
import org.labkey.api.view.WebPartView;
import org.labkey.webutils.WebUtilsServiceImpl;
import org.labkey.webutils.view.JspPage;

import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class WaterCalendarWebPartFactory extends BaseWebPartFactory{
        static private String _packagePathDir = WebUtilsServiceImpl.getPackageDirFromClass(JspPage.class);



    private Portal.WebPart _webPart;

    public WaterCalendarWebPartFactory(){super ("Water Calendar");}

    public WebPartView getWebPartView (@NotNull ViewContext portalCtx, @NotNull Portal.WebPart webpart){
        JspView view = new JspView("/org/labkey/wnprc_ehr/pages/husbandry/WaterCalendar.jsp");
        view.setTitle("Water Calendar");
        view.setFrame(WebPartView.FrameType.PORTAL);

        JspPage page = new JspPage(view);
        page.setFrame(WebPartView.FrameType.PORTAL);

        /*// Add some Knockout templates.
        List<String> templates = Arrays.asList(
                "lk-table",
                "lk-querytable",
                "lk-input-textarea"
        );
        for( String name: templates ) {
            page.getModelBean().addTemplate(new JspView<>(_packagePathDir + "knockout_components/" + name + ".jsp"));
        }*/
        return page;
    }
}
