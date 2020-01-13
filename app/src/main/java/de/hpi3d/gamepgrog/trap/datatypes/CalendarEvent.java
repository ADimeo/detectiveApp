package de.hpi3d.gamepgrog.trap.datatypes;

import android.Manifest;
import android.database.Cursor;
import android.provider.CalendarContract;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import de.hpi3d.gamepgrog.trap.future.Function;

@Parcel(Parcel.Serialization.BEAN)
public class CalendarEvent implements UserData {

    private long id;
    private String title;
    private String eventLocation;
    private long startInUTCMilliseconds;
    private long endInUTCMilliseconds;


    @ParcelConstructor
    public CalendarEvent(long id, String title, String eventLocation, long startInUTCMilliseconds, long endInUTCMilliseconds) {
        this.id = id;
        this.title = title;
        this.eventLocation = eventLocation;
        this.startInUTCMilliseconds = startInUTCMilliseconds;
        this.endInUTCMilliseconds = endInUTCMilliseconds;
    }

    private CalendarEvent(Cursor c) {
        id = cursorColumn(c, CalendarContract.Events._ID, c::getLong);
        title = cursorColumn(c, CalendarContract.Events.TITLE, c::getString);
        eventLocation = cursorColumn(c, CalendarContract.Events.EVENT_LOCATION, c::getString);
        startInUTCMilliseconds = cursorColumn(c, CalendarContract.Events.DTSTART, c::getLong);
        endInUTCMilliseconds = cursorColumn(c, CalendarContract.Events.DTEND, c::getLong);
    }

    @Override
    public String[] requiredPermission() {
        return new String[]{Manifest.permission.READ_CALENDAR};
    }

    private static <T> T cursorColumn(Cursor c, String key, Function<Integer, T> getter) {
        int pos = c.getColumnIndex(key);
        return getter.apply(pos);
    }

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

    public long getStartInUTCMilliseconds() {
        return startInUTCMilliseconds;
    }

    public long getEndInUTCMilliseconds() {
        return endInUTCMilliseconds;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }
}
