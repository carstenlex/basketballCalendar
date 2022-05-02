package de.carstenlex;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GoogleCalendar {


    private final Calendar service;

    public GoogleCalendar() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    /**
     * Es werden NUR diejenigen Termine gelöscht, die auch den @see Configuration.AUTO_SYNC_MARKER in der description enthalten
     * @param calendarBasketball
     * @throws IOException
     */
    public void clear(CalendarListEntry calendarBasketball) throws IOException {
        //clear geht nur bei primary calendar, daher hier umständlich
        String pageToken = null;
        do {
            Events events = service.events().list(calendarBasketball.getId()).setPageToken(pageToken).execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                if (istAutoSyncEvent(event)) {
                    service.events().delete(calendarBasketball.getId(), event.getId()).execute();
                }
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);
    }

    private boolean istAutoSyncEvent(Event event) {
        return event.getDescription()!=null && event.getDescription().contains(Configuration.AUTO_SYNC_MARKER);
    }

    public CalendarListEntry findCalendarBasketball() throws IOException {
        CalendarList calendarList = service.calendarList().list().setPageToken(null).execute();
        List<CalendarListEntry> items = calendarList.getItems();

        //items.forEach(System.out::println);
        return items.stream().filter(calendar -> calendar.getSummary().equalsIgnoreCase(CALENDAR_BASKETBALL_NAME)).findFirst().get();
    }


    public void erstelleTermineInKalender( CalendarListEntry calendarBasketball, List<Spiel> spiele) throws IOException {
        for (Spiel spiel : spiele) {
            createEvent(calendarBasketball.getId(), spiel);
        }
    }




    public void createEvent( String calendarId, Spiel spiel) throws IOException {
        Event event = new Event()
                .setSummary(spiel.getTeamHeim()+"-"+spiel.getTeamAuswaerts())
                .setLocation(spiel.getHalle())
                .setDescription(spiel.toString());

        //DateTime startDateTime = new DateTime("2020-09-17T09:00:00-07:00");
        DateTime startDateTime = new DateTime(spiel.getStartzeitForCalendar());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(TIMEZONE_GMT);
        event.setStart(start);

        DateTime endDateTime = new DateTime(spiel.getEndeZeitForCalendar());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(TIMEZONE_GMT);
        event.setEnd(end);

        /*String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));*/

       /* EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail("lpage@example.com"),
                new EventAttendee().setEmail("sbrin@example.com"),
        };
        event.setAttendees(Arrays.asList(attendees));*/

        /*EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);*/


        event = service.events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());

    }



    private static void listEventsInSpecialCalendar(Calendar service, String requestedCalendarId) throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        String calendarId = PRIMARY;
        if (requestedCalendarId != null) {
            calendarId = requestedCalendarId;
        }
        Events events = service.events().list(requestedCalendarId)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }


    private static final String APPLICATION_NAME = "Google Calendar API Java BasketballSync";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_EVENTS, CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    public static final String PRIMARY = "primary";
    public static final String TIMEZONE_EUROPE_ZURICH = "Europe/Zurich";
    public static final String TIMEZONE_GMT = "GMT";
    public static final String CALENDAR_BASKETBALL_NAME = "Basketballspiele";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the GCLOUD_credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = BasketballCalendarSync.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                //.setApprovalPrompt("consent")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}
