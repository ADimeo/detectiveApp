package de.hpi3d.gamepgrog.trap.datatypes;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import androidx.annotation.NonNull;

import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;

public class CalendarEvent extends ApiDataType {

    private long id;
    private String title;
    private String eventLocation;
    private long startInUTCMilliseconds;
    private long endInUTCMilliseconds;


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

    private static <T> T cursorColumn(Cursor c, String key, Function<Integer, T> getter) {
        int pos = c.getColumnIndex(key);
        return getter.apply(pos);
    }

    @Override
    public Parcel toParcel() {
        Parcel dest = Parcel.obtain();
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(eventLocation);
        dest.writeLong(startInUTCMilliseconds);
        dest.writeLong(endInUTCMilliseconds);
        return dest;
    }

    @Override
    protected void fromParcel(Parcel p) {
        id = p.readLong();
        title = p.readString();
        eventLocation = p.readString();
        startInUTCMilliseconds = p.readLong();
        endInUTCMilliseconds = p.readLong();
    }

    @Override
    public void appendToPR(UserDataPostRequestFactory.UserDataPostRequest pr) {

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


    @NonNull
    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarEvent that = (CalendarEvent) o;
        return id == that.id &&
                startInUTCMilliseconds == that.startInUTCMilliseconds &&
                endInUTCMilliseconds == that.endInUTCMilliseconds &&
                Objects.equals(title, that.title) &&
                Objects.equals(eventLocation, that.eventLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, eventLocation, startInUTCMilliseconds, endInUTCMilliseconds);
    }
}
