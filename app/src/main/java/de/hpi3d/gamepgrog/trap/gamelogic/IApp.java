package de.hpi3d.gamepgrog.trap.gamelogic;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;

public interface IApp {

    int PERMISSION_CALENDAR = 1, PERMISSION_CONTACTS = 2, PERMISSION_LOCATION = 4;

    /**
     * Returns Clue[]
     */
    String CALL_CLUES = "clues";

    boolean hasPermission(int permission);

    List<CalendarEvent> getCalendarEvents() throws NoPermissionsException;

    List<Contact> getContacts() throws NoPermissionsException;


    /**
     * Returns current location of the device, if applicable. May return null if no location
     * can be detected. This may be due to location being turned off on the device.
     *
     * @return
     * @throws NoPermissionsException
     */
    List<LocationData> getLocation() throws NoPermissionsException;

    String getLanguage();

    void executeApiCall(String call, BiConsumer<Integer, Bundle> callback);

    void postUserData(String call, UserDataPostRequestFactory.UserDataPostRequest pr, Runnable callback);
}
