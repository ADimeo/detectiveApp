package de.hpi3d.gamepgrog.trap.gamelogic;

import android.Manifest;

import de.hpi3d.gamepgrog.trap.api.ApiService;

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
    public void update() {
        // List<Task> tasks = getTasks();
        // app.showTasks(tasks)
        // for each task in tasks
        //
    }

    private void handleDataRequest(String dataNeeded) {
//        if (app.hasPermission(permissionFor(dataNeeded))) {
//            try {
//                UserData.UserDataPostRequest pr = getDataFromApp(dataNeeded);
//                if (pr != null)
//                    app.postUserData(BackendManagerIntentService.CALL_ADD_DATA,
//                            pr, this::update);
//            } catch (NoPermissionsException ignored) {}
//        } else {
//            app.setPermission(permissionFor(dataNeeded), (isSet) -> handleDataRequest(dataNeeded));
//        }
    }

//    private UserData.UserDataPostRequest getDataFromApp(String dataNeeded)
//            throws NoPermissionsException {
//        switch (dataNeeded) {
//            case "calendar":
//                return UserData.buildWithCalendarEvents(app.getCalendarEvents());
//            case "location":
//                return UserData.buildWithLocations(app.getLocation());
//            default:
//                return null;
//        }
//    }

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
