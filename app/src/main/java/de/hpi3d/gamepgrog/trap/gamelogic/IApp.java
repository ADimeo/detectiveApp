package de.hpi3d.gamepgrog.trap.gamelogic;

import android.os.Parcelable;

import java.util.List;
import java.util.function.Consumer;

import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;

public interface IApp {

    int PERMISSION_CALENDAR = 1, PERMISSION_CONTACTS = 2, PERMISSION_LOCATION = 4;

    /**
     * Registers a new User if none was present
     * Returns [User, boolean (Was the user new registered)]
     */
    String CALL_REGISTER_OR_GET_USER = "register";

    /**
     * returns UserStatus
     */
    String CALL_USER_STATUS = "user_status";

    /**
     * no response. Posts given Data (UserDataPostRequest) to the Api
     */
    String CALL_ADD_DATA = "add_data";

    /**
     * Returns Clue[]
     */
    String CALL_CLUES = "clues";

    boolean hasPermission(int permission);

    List<CalendarEvent> getCalendarEvents() throws NoPermissionsException;

    List<Contact> getContacts() throws NoPermissionsException;

    List<LocationData> getLocation() throws NoPermissionsException;

    String getLanguage();

    // TODO Replace Parcable with ...
    void executeApiCall(String call, Consumer<Parcelable> callback);

    // TODO Send UserDataPostRequest to the Service
    void executeApiCall(String call, UserDataPostRequestFactory.UserDataPostRequest pr, Runnable callback);
}
