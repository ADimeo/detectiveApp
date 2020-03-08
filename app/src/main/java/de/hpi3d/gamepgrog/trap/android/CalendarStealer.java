package de.hpi3d.gamepgrog.trap.android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.List;

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

    public static void insertCalendarEvents(Context context, List<CalendarEvent> events) {
        for (CalendarEvent event : events) {
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            event.enrichWithCalendarData(calIntent);
            context.startActivity(calIntent);
        }
    }
}
