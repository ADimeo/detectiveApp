package de.hpi3d.gamepgrog.trap.gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
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
        app.executeApiCall(IApp.CALL_USER_STATUS, parcelable -> {
            UserStatus status = null;
            handleDataRequest(status.currentStoryPoint);
        });
    }

    /**
     * Fetches the clues from the server and sends it to the callback when it arrives
     */
    public void requestClues(Consumer<List<Clue>> callback) {
        app.executeApiCall(IApp.CALL_CLUES, parcelable -> {
            List<Clue> clues = new ArrayList<>();  // = parcelable.toClueList();
            callback.accept(clues);
        });
    }

    /**
     * Registers the User if not already done so.
     * Returns the User and if a Registration happened
     */
    public void registerIfUnregistered(BiConsumer<User, Boolean> callback) {
        app.executeApiCall(IApp.CALL_REGISTER_OR_GET_USER, parcelable -> {
            boolean firstRegistered = false;
            User user = null;
            callback.accept(user, firstRegistered);
        });
    }

    private void handleDataRequest(String storyPoint) {
        try {
            UserDataPostRequestFactory.UserDataPostRequest pr = getDataFromApp(storyPoint);
            app.executeApiCall(IApp.CALL_ADD_DATA, pr, this::doStoryActionIfNeeded);
        } catch (NoPermissionsException e) {
            // TODO What to do here?
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
                throw new IllegalStateException("You should have never come here!");
        }
    }
}
