package de.hpi3d.gamepgrog.trap.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;

public class UserDataPostRequestFactory {

    public static UserDataPostRequest buildWithContacts(List<Contact> contacts) {
        UserDataPostRequest pr = new UserDataPostRequest();
        pr.data.contacts.addAll(contacts);
        return pr;
    }

    public static UserDataPostRequest buildWithCalendarEvents(List<CalendarEvent> cEvents) {
        UserDataPostRequest pr = new UserDataPostRequest();
        pr.data.cEvents.addAll(cEvents);
        return pr;
    }

    public static UserDataPostRequest buildWithLocations(List<LocationData> locations) {
        UserDataPostRequest pr = new UserDataPostRequest();
        pr.data.locations.addAll(locations);
        return pr;
    }

    public static class UserDataPostRequest implements Parcelable {
        private final String origin = "app";
        private UserDataDictionary data = new UserDataDictionary();

        private UserDataPostRequest() {}

        private UserDataPostRequest(Parcel in) {
            data = in.readParcelable(UserDataDictionary.class.getClassLoader());
        }

        public static final Creator<UserDataPostRequest> CREATOR = new Creator<UserDataPostRequest>() {
            @Override
            public UserDataPostRequest createFromParcel(Parcel in) {
                return new UserDataPostRequest(in);
            }

            @Override
            public UserDataPostRequest[] newArray(int size) {
                return new UserDataPostRequest[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(data, 0);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserDataPostRequest that = (UserDataPostRequest) o;
            return Objects.equals(origin, that.origin) &&
                    Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(origin, data);
        }
    }

    private static class UserDataDictionary implements Parcelable {
        private List<Contact> contacts = new ArrayList<>();
        private List<CalendarEvent> cEvents = new ArrayList<>();
        private List<LocationData> locations = new ArrayList<>();

        private UserDataDictionary() {}

        private UserDataDictionary(Parcel in) {
            in.readList(contacts, Contact.class.getClassLoader());
            in.readList(cEvents, CalendarEvent.class.getClassLoader());
            in.readList(locations, LocationData.class.getClassLoader());
        }

        public static final Creator<UserDataDictionary> CREATOR = new Creator<UserDataDictionary>() {
            @Override
            public UserDataDictionary createFromParcel(Parcel in) {
                return new UserDataDictionary(in);
            }

            @Override
            public UserDataDictionary[] newArray(int size) {
                return new UserDataDictionary[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(contacts);
            dest.writeList(cEvents);
            dest.writeList(locations);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserDataDictionary that = (UserDataDictionary) o;
            return Objects.equals(contacts, that.contacts) &&
                    Objects.equals(cEvents, that.cEvents) &&
                    Objects.equals(locations, that.locations);
        }

        @Override
        public int hashCode() {
            return Objects.hash(contacts, cEvents, locations);
        }
    }
}
