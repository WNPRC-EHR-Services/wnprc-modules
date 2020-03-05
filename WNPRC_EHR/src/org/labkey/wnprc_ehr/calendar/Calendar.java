package org.labkey.wnprc_ehr.calendar;

import org.labkey.api.data.Container;
import org.labkey.api.security.User;

public interface Calendar
{
    void setUser(User u);
    void setContainer(Container c);
    String getCalendarEventsAsJson(String calendarId, String backgroundColor) throws Exception;
    String getRoomEventsAsJson(String roomId, String backgroundColor) throws Exception;
}