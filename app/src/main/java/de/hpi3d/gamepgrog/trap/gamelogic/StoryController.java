package de.hpi3d.gamepgrog.trap.gamelogic;

import android.Manifest;
import android.app.DownloadManager;

import java.security.Permission;

import de.hpi3d.gamepgrog.trap.api.BackendManagerIntentService;
import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;

public class StoryController {

    private static final String REQUEST_CALENDAR_STORY_POINT = "left_point";
    private static final String REQUEST_LOCATION_STORY_POINT = "right_point";

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
        app.executeApiCall(BackendManagerIntentService.MANAGE_GET_USER_STATUS,
                (code, bundle) -> {
            UserStatus status = bundle.getParcelable("value");
            if (status != null)
                handleDataRequest(status.currentStoryPoint);
        });
    }

    private void handleDataRequest(String storyPoint) {
        if (app.hasPermission(permissionFor(storyPoint))) {
            try {
                UserDataPostRequestFactory.UserDataPostRequest pr = getDataFromApp(storyPoint);
                if (pr != null)
                    app.postUserData(BackendManagerIntentService.MANAGE_ADD_DATA,
                            pr, this::doStoryActionIfNeeded);
            } catch (NoPermissionsException ignored) {}
        } else {
            app.setPermission(permissionFor(storyPoint), (isSet) -> handleDataRequest(storyPoint));
        }
    }

    private UserDataPostRequestFactory.UserDataPostRequest getDataFromApp(String storyPoint)
            throws NoPermissionsException {
        switch (storyPoint) {
            case REQUEST_CALENDAR_STORY_POINT:
                return UserDataPostRequestFactory.buildWithCalendarEvents(app.getCalendarEvents());
            case REQUEST_LOCATION_STORY_POINT:
                return UserDataPostRequestFactory.buildWithLocations(app.getLocation());
            default:
                return null;
        }
    }

    private String permissionFor(String storyPoint) {
        switch (storyPoint) {
            case REQUEST_CALENDAR_STORY_POINT:
                return Manifest.permission.READ_CALENDAR;
            case REQUEST_LOCATION_STORY_POINT:
                return Manifest.permission.ACCESS_COARSE_LOCATION;
            default:
                return "";
        }
    }
}
