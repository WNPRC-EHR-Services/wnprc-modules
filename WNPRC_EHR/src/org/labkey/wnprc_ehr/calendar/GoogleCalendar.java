package org.labkey.wnprc_ehr.calendar;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.DbSchema;
import org.labkey.api.data.DbSchemaType;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.security.User;
import org.labkey.dbutils.api.SimplerFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GoogleCalendar implements org.labkey.wnprc_ehr.calendar.Calendar
{
    private User user;
    private Container container;

    /** Application name. */
    private static final String APPLICATION_NAME =
            "Google Calendar API Java Quickstart";

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /** Global instance of the HTTP transport. */
    private static NetHttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    private static final int MAX_EVENT_RESULTS = 2500;
    private static final long SIX_MONTHS_IN_MILLISECONDS = 1000L * 60L * 60L * 24L * 30L * 6L;
    private static final long TWO_YEARS_IN_MILLISECONDS = 1000L * 60L * 60L * 24L * 30L * 24L;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public void setUser(User u) {
        user = u;
    }

    public void setContainer(Container c) {
        container = c;
    }

    private JSONArray getJsonEventList(List<Event> events, String calendarId, String backgroundColor) {
        JSONArray jsonEvents = new JSONArray();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);

            JSONObject jsonEvent = new JSONObject();
            jsonEvent.put("title", event.getSummary());
            jsonEvent.put("start", event.getStart().getDate() != null ? event.getStart().getDate() : event.getStart().getDateTime());
            jsonEvent.put("end", event.getEnd().getDate() != null ? event.getEnd().getDate() : event.getEnd().getDateTime());
            jsonEvent.put("htmlLink", event.getHtmlLink());
            jsonEvent.put("calendarId", calendarId);
            jsonEvent.put("backgroundColor", backgroundColor);
            jsonEvent.put("eventId", i);
            jsonEvent.put("eventListSize", events.size());

            jsonEvents.put(jsonEvent);
        }

        return jsonEvents;
    }

    private InputStream mapToInputStream(Map<String, Object> map) {
        InputStream is = null;
        JSONObject json = new JSONObject();
        json.put("type", "service_account");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }

        return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an authorized HttpRequestInitializer object.
     * @return An authorized HttpRequestInitializer object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private HttpRequestInitializer getCredentials() throws Exception {
        // Load client secrets.
        SimplerFilter filter = new SimplerFilter("id", CompareType.EQUAL, "f5c49137-186d-41fb-9c93-9979b7f4c2ba");
        DbSchema schema = DbSchema.get("googledrive", DbSchemaType.Module);
        TableInfo ti = schema.getTable("service_accounts");
        TableSelector ts = new TableSelector(ti, filter, null);
        InputStream in = mapToInputStream(ts.getMap());

        GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPES);
        credentials.refreshIfExpired();

        return new HttpCredentialsAdapter(credentials);
    }

    private Calendar getCalendar() throws Exception {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private String getCalendarEvents(Calendar calendar, DateTime dateMin, DateTime dateMax, Integer maxResults, String calendarId, String backgroundColor) throws Exception {
        Events events = calendar.events().list(calendarId)
                .setMaxResults(maxResults)
                .setTimeMin(dateMin)
                .setTimeMax(dateMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return getJsonEventList(events.getItems(), calendarId, backgroundColor).toString();
    }

    public String getCalendarEventsAsJson(String calendarId, String backgroundColor) throws Exception {
        Calendar calendar = getCalendar();
        java.util.Calendar currentDate = java.util.Calendar.getInstance();
        DateTime dateMin = new DateTime(currentDate.getTimeInMillis() - SIX_MONTHS_IN_MILLISECONDS);
        DateTime dateMax = new DateTime(currentDate.getTimeInMillis() + TWO_YEARS_IN_MILLISECONDS);
        return getCalendarEvents(calendar, dateMin, dateMax, MAX_EVENT_RESULTS, calendarId, backgroundColor);
    }

    public String getRoomEventsAsJson(String roomId, String backgroundColor) throws Exception {
        return null;
    }
}