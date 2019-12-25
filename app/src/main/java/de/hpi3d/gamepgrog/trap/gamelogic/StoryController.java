package de.hpi3d.gamepgrog.trap.gamelogic;

import de.hpi3d.gamepgrog.trap.api.BackendManagerIntentService;
import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;

public class StoryController {

    private static final String REQUEST_CALENDAR_STORY_POINT = "calendar_point";
    private static final String REQUEST_LOCATION_STORY_POINT = "location_point";


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

//    /**
//     * Fetches the clues from the server and sends it to the callback when it arrives
//     */
//    public void requestClues(Consumer<List<Clue>> callback) {
//        app.postUserData(IApp.CALL_CLUES, parcelable -> {
//            List<Clue> clues = new ArrayList<>();  // = parcelable.toClueList();
//            callback.accept(clues);
//        });
//    }
//
//    /**
//     * Registers the User if not already done so.
//     * Returns the User and if a Registration happened
//     */
//    public void registerIfUnregistered(BiConsumer<User, Boolean> callback) {
//        app.postUserData(IApp.CALL_REGISTER_OR_GET_USER, parcelable -> {
//            boolean firstRegistered = false;
//            User user = null;
//            callback.accept(user, firstRegistered);
//        });
//    }

    private void handleDataRequest(String storyPoint) {
        try {
            UserDataPostRequestFactory.UserDataPostRequest pr = getDataFromApp(storyPoint);
            if (pr != null)
                app.postUserData(BackendManagerIntentService.MANAGE_ADD_DATA,
                        pr, this::doStoryActionIfNeeded);
        } catch (NoPermissionsException ignored) {}
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
}
