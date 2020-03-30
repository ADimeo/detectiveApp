package de.hpi3d.gamepgrog.trap.datatypes;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.CalendarContract;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import de.hpi3d.gamepgrog.trap.future.Function;

/**
 * Corresponds to a single calendar event
 */
@Parcel(Parcel.Serialization.BEAN)
public class CalendarEvent implements UserData {

    /**
     * used for storing it with GreenDao
     */
    private long id;
    private String title;
    private String eventLocation;
    private long startInUtcSeconds;
    private long endInUtcSeconds;


    @ParcelConstructor
    public CalendarEvent(long id, String title, String eventLocation, long startInUtcSeconds, long endInUtcSeconds) {
        this.id = id;
        this.title = title;
        this.eventLocation = eventLocation;
        this.startInUtcSeconds = startInUtcSeconds;
        this.endInUtcSeconds = endInUtcSeconds;
    }

    private CalendarEvent(Cursor c) {
        id = cursorColumn(c, CalendarContract.Events._ID, c::getLong);
        title = cursorColumn(c, CalendarContract.Events.TITLE, c::getString);
        eventLocation = cursorColumn(c, CalendarContract.Events.EVENT_LOCATION, c::getString);
        startInUtcSeconds = cursorColumn(c, CalendarContract.Events.DTSTART, c::getLong) / 1000;
        endInUtcSeconds = cursorColumn(c, CalendarContract.Events.DTEND, c::getLong) / 1000;
    }

    private static <T> T cursorColumn(Cursor c, String key, Function<Integer, T> getter) {
        int pos = c.getColumnIndex(key);
        return getter.apply(pos);
    }

    /**
     * Creates an array list of CalendarEvents from a cursor. Each column of the
     * cursor corresponds to one event. The cursor must be open for this to be possible,
     * and a result of a query of event data. Additionally, all the following fields must be queried:
     * - id
     * - title
     * - location
     * - dtstart (starttime)
     * - dtend (endtime)
     *
     * @param cursor cursor of query
     * @return ArrayList of events
     */
    public static ArrayList<CalendarEvent> createFromCursor(Cursor cursor) {
        ArrayList<CalendarEvent> extractedEvents = new ArrayList<>();

        if (null != cursor && cursor.moveToFirst()) {
            do {
                extractedEvents.add(new CalendarEvent(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return extractedEvents;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public long getStartInUtcSeconds() {
        return startInUtcSeconds;
    }

    public long getEndInUtcSeconds() {
        return endInUtcSeconds;
    }

    /**
     * Returns this CalendarEvents data in ContentValues format
     *
     * @return ContentValues with relevant data
     */
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Events.DTSTART, startInUtcSeconds * 1000);
        contentValues.put(CalendarContract.Events.DTEND, endInUtcSeconds * 1000);
        contentValues.put(CalendarContract.Events.TITLE, title);
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 0);
        return contentValues;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }
}
