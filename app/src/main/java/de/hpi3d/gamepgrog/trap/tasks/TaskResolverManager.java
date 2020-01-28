package de.hpi3d.gamepgrog.trap.tasks;

import android.Manifest;

import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.Language;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;

public class TaskResolverManager {

    private final static TaskResolver<Contact> contactTaskResolver =
            new SyncTaskResolver<>(
                    "contact",
                    new String[]{Manifest.permission.READ_CONTACTS},
                    DataStealer::takeContactData);

    private final static TaskResolver<CalendarEvent> calendarTaskResolver =
            new SyncTaskResolver<>(
                    "calendar",
                    new String[]{Manifest.permission.READ_CALENDAR},
                    DataStealer::takeCalendarData);


    private final static TaskResolver<Language> languageResolver =
            new SyncTaskResolver<>(
                    "language",
                    new String[]{},
                    DataStealer::takeLanguage);

    private final static TaskResolver<LocationData> locationTaskResolver =
            new AsyncTaskResolver<>(
                    "location",
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    (a, b) -> {
                    });


    public static TaskResolver<? extends UserData> getResolverFor(Task task) {
        if (contactTaskResolver.applicableFor(task)) return contactTaskResolver;
        else if (calendarTaskResolver.applicableFor(task)) return calendarTaskResolver;
        else if (languageResolver.applicableFor(task)) return languageResolver;
        else if (locationTaskResolver.applicableFor(task)) return locationTaskResolver;
        throw new UnsupportedOperationException("There is no Resolver for a task with datatype: "
                + task.getDatatype());
    }
}
