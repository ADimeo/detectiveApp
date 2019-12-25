package de.hpi3d.gamepgrog.trap.datatypes;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;

public class CalendarEvent implements Parcelable {

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

    private CalendarEvent(){
        this.id = -1;
        this.title = null;
        this.eventLocation = null;
        this.startInUTCMilliseconds = -1;
        this.endInUTCMilliseconds = -1;
    }


    private CalendarEvent(Parcel in) {
        id = in.readLong();
        title = in.readString();
        eventLocation = in.readString();
        startInUTCMilliseconds = in.readLong();
        endInUTCMilliseconds = in.readLong();
    }

    public static final Creator<CalendarEvent> CREATOR = new Creator<CalendarEvent>() {
        @Override
        public CalendarEvent createFromParcel(Parcel in) {
            return new CalendarEvent(in);
        }

        @Override
        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(eventLocation);
        dest.writeLong(startInUTCMilliseconds);
        dest.writeLong(endInUTCMilliseconds);
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
