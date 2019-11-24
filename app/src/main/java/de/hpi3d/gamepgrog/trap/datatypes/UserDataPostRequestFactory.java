package de.hpi3d.gamepgrog.trap.datatypes;

import java.util.ArrayList;
import java.util.List;

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

    public static class UserDataPostRequest {
        private final String origin = "app";
        private UserDataDictionary data = new UserDataDictionary();
    }

    private static class UserDataDictionary {
        private List<Contact> contacts = new ArrayList<>();
        private List<CalendarEvent> cEvents = new ArrayList<>();
    }
}
