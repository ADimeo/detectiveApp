package de.hpi3d.gamepgrog.trap.gamelogic;

import android.os.Bundle;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;

public interface IApp {

    void setPermission(String permission, Consumer<Boolean> callback);

    boolean hasPermission(String permission);

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

    void postUserData(String call, UserData.UserDataPostRequest pr, Runnable callback);
}
