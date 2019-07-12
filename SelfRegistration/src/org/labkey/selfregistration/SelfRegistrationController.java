package org.labkey.selfregistration;

import org.apache.log4j.Logger;
import org.labkey.api.action.ApiResponse;
import org.labkey.api.action.ApiSimpleResponse;
import org.labkey.api.action.MutatingApiAction;
import org.labkey.api.action.SimpleViewAction;
import org.labkey.api.action.SpringActionController;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.TableInfo;
import org.labkey.api.module.AllowedDuringUpgrade;
import org.labkey.api.query.BatchValidationException;
import org.labkey.api.query.QueryService;
import org.labkey.api.query.QueryUpdateService;
import org.labkey.api.security.IgnoresTermsOfUse;
import org.labkey.api.security.RequiresPermission;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.view.JspView;
import org.labkey.api.view.NavTree;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.labkey.api.query.UserSchema;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SelfRegistrationController extends SpringActionController
{
    private static final DefaultActionResolver _actionResolver = new DefaultActionResolver(SelfRegistrationController.class);
    public static final String NAME = "selfregistration";
    protected static final Logger _log = Logger.getLogger(SelfRegistrationController.class);

    public SelfRegistrationController()
    {
        setActionResolver(_actionResolver);
    }

    @RequiresPermission(ReadPermission.class)
    public class BeginAction extends SimpleViewAction
    {
        public ModelAndView getView(Object o, BindException errors)
        {
            return new JspView("/org/labkey/selfregistration/view/hello.jsp");
        }

        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }
    }

    public static class SelfRegistrationForm {
        private String assignedTo;
        private String title;
        private String firstname;
        private String lastname;
        private String email;
        private String institution;
        private String reason;
        private String comment;
        private String issueDefId;
        private String containerPath;
        private String hostname;

        public String getHostname()
        {
            return hostname;
        }

        public void setHostname(String hostname)
        {
            this.hostname = hostname;
        }

        public String getAssignedTo()
        {
            return assignedTo;
        }

        public void setAssignedTo(String assignedTo)
        {
            this.assignedTo = assignedTo;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getFirstname()
        {
            return firstname;
        }

        public void setFirstname(String firstname)
        {
            this.firstname = firstname;
        }

        public String getLastname()
        {
            return lastname;
        }

        public void setLastname(String lastname)
        {
            this.lastname = lastname;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getInstitution()
        {
            return institution;
        }

        public void setInstitution(String institution)
        {
            this.institution = institution;
        }

        public String getReason()
        {
            return reason;
        }

        public void setReason(String reason)
        {
            this.reason = reason;
        }

        public String getComment()
        {
            return comment;
        }

        public void setComment(String comment)
        {
            this.comment = comment;
        }

        public String getIssueDefId()
        {
            return issueDefId;
        }

        public void setIssueDefId(String issueDefId)
        {
            this.issueDefId = issueDefId;
        }

        public void setContainerPath(String containerPath)
        {
            this.containerPath = containerPath;
        }
        public String getContainerPath()
        {
            return containerPath;
        }
    }

    @IgnoresTermsOfUse
    @AllowedDuringUpgrade
    @RequiresPermission(InsertPermission.class)
    public static class UpdateSelfRegistrationListAction extends MutatingApiAction<SelfRegistrationForm>
    {
        public static final String _issueDefName = "userregistrations";
        public static final String _issueStatus = "open";
        public static final String _schemaPath = "issues";
        @Override
        public ApiResponse execute(SelfRegistrationForm form, BindException errors)
        {
            Container container = ContainerManager.getForPath(form.getContainerPath());
            User user = getViewContext().getUser();
            saveIssue(user, container, form);
            return new ApiSimpleResponse();
        }

        //Takes the form data and inserts a new issue into the User Registrations issue tracker
        public static void saveIssue(User user, Container container, SelfRegistrationForm form)
        {
            String hostname = form.getHostname();

            Map<String, Object> row = new CaseInsensitiveHashMap<>();
            row.put("Title", form.getTitle());
            row.put("AssignedTo", form.getAssignedTo());
            row.put("Status", _issueStatus);
            row.put("firstname", form.getFirstname());
            row.put("lastname", form.getLastname());
            row.put("email", form.getEmail());
            row.put("institution", form.getInstitution());
            row.put("reason", form.getReason());

            UserSchema userSchema = QueryService.get().getUserSchema(user, container, _schemaPath);
            TableInfo table = userSchema.getTable(_issueDefName);
            QueryService.get().getSelectSQL(table,null,null,null,100,0,false);
            QueryUpdateService qus = table.getUpdateService();

            BatchValidationException batchErrors = new BatchValidationException();
            List<Map<String, Object>> results;

            try
            {
                results = qus.insertRows(user, container, Collections.singletonList(row), batchErrors, null, null);
                if (!batchErrors.hasErrors())
                {
                    assert results.size() == 1;
                    //send in the list id
                    String id;
                    Map<String,Object> formresult = results.get(0);
                    id = formresult.get("IssueId").toString();

                    SelfRegistrationNotification t = new SelfRegistrationNotification(id,hostname);
                    t.sendManually(container,user);
                }
                else
                    throw batchErrors;
            } catch (Exception e)
            {
                _log.error(e.getMessage());
                throw new RuntimeException(e);
            }


        }
    }

}