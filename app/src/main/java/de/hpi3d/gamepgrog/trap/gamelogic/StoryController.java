package de.hpi3d.gamepgrog.trap.gamelogic;

import android.Manifest;
import android.app.DownloadManager;

import java.security.Permission;

import de.hpi3d.gamepgrog.trap.api.BackendManagerIntentService;
import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;

public class StoryController {

    private IApp app;


    public StoryController(IApp app) {
        this.app = app;
    }

    /**
     * Asks the api for the current story point.
     * If the story point requests user data, fetch the data and send it to the api.
     * Check again if action is needed
     */
    public void doStoryActionIfNeeded() {
        app.executeApiCall(BackendManagerIntentService.MANAGE_NEEDS_DATA,
                (code, bundle) -> {
            String needed = bundle.getString("value");
            if (code == 0 && needed != null) {
                handleDataRequest(needed);
            }
        });
    }

    private void handleDataRequest(String dataNeeded) {
        if (app.hasPermission(permissionFor(dataNeeded))) {
            try {
                UserDataPostRequestFactory.UserDataPostRequest pr = getDataFromApp(dataNeeded);
                if (pr != null)
                    app.postUserData(BackendManagerIntentService.MANAGE_ADD_DATA,
                            pr, this::doStoryActionIfNeeded);
            } catch (NoPermissionsException ignored) {}
        } else {
            app.setPermission(permissionFor(dataNeeded), (isSet) -> handleDataRequest(dataNeeded));
        }
    }

    private UserDataPostRequestFactory.UserDataPostRequest getDataFromApp(String dataNeeded)
            throws NoPermissionsException {
        switch (dataNeeded) {
            case "calendar":
                return UserDataPostRequestFactory.buildWithCalendarEvents(app.getCalendarEvents());
            case "location":
                return UserDataPostRequestFactory.buildWithLocations(app.getLocation());
            default:
                return null;
        }
    }

    private String permissionFor(String dataNeeded) {
        switch (dataNeeded) {
            case "calendar":
                return Manifest.permission.READ_CALENDAR;
            case "location":
                return Manifest.permission.ACCESS_COARSE_LOCATION;
            default:
                return "";
        }
    }
}
