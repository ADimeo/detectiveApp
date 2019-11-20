package de.hpi3d.gamepgrog.trap.datatypes;

import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class CalendarEvent {

    private long id;
    private String title;
    private String eventLocation;
    private long startInUTCMilliseconds;
    private long endInUTCMilliseconds;


    CalendarEvent(long id, String title, String eventLocation, long startInUTCMilliseconds, long endInUTCMilliseconds) {
        this.id = id;
        this.title = title;
        this.eventLocation = eventLocation;
        this.startInUTCMilliseconds = startInUTCMilliseconds;
        this.endInUTCMilliseconds = endInUTCMilliseconds;
    }

    private CalendarEvent(){
        this.id = -1;
        this.title = null;
        this.eventLocation = null;
        this.startInUTCMilliseconds = -1;
        this.endInUTCMilliseconds = -1;
    }


    public static ArrayList<CalendarEvent> createFromCursor(Cursor cursor) {


        ArrayList<CalendarEvent> extractedEvents = new ArrayList<>();

        if (null != cursor && cursor.moveToFirst()) {
            int posOfId = cursor.getColumnIndex(CalendarContract.Events._ID);
            int posOfTitle = cursor.getColumnIndex(CalendarContract.Events.TITLE);
            int posOfLocation = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
            int posOfDTStart = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
            int posOfDTEnd = cursor.getColumnIndex(CalendarContract.Events.DTEND);

            do {
                CalendarEvent cEvent = new CalendarEvent();
                cEvent.setId(cursor.getLong(posOfId));
                cEvent.setTitle(cursor.getString(posOfTitle));
                cEvent.setEventLocation(cursor.getString(posOfLocation));
                cEvent.setStartInUTCMilliseconds(cursor.getLong(posOfDTStart));
                cEvent.setEndInUTCMilliseconds(cursor.getLong(posOfDTEnd));

                extractedEvents.add(cEvent);
            } while (cursor.moveToNext());
            cursor.close();
        }


        return extractedEvents;
    }


    @NonNull
    @Override
    public String toString() {
        return this.title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setStartInUTCMilliseconds(long startInUTCMilliseconds) {
        this.startInUTCMilliseconds = startInUTCMilliseconds;
    }

    public void setEndInUTCMilliseconds(long endInUTCMilliseconds) {
        this.endInUTCMilliseconds = endInUTCMilliseconds;
    }
}
