package de.hpi3d.gamepgrog.trap.gamelogic;

import android.os.Bundle;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.hpi3d.gamepgrog.trap.datatypes.CalendarEvent;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;

public interface IApp {

    void setPermission(String datatype, Consumer<Boolean> callback);

    boolean hasPermission(String datatype);

    void getUserData(String datatype, Consumer<UserData> data) throws NoPermissionsException;

    void executeApiCall(String call, BiConsumer<Integer, Bundle> callback);

    void postUserData(String datatype, UserData data, BiConsumer<Integer, Bundle> callback);
}
