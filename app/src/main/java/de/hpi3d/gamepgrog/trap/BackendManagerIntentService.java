package de.hpi3d.gamepgrog.trap;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import de.hpi3d.gamepgrog.trap.datatypes.ClueDao;
import de.hpi3d.gamepgrog.trap.datatypes.DaoSession;

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
        int playerId = getPlayerId(getApplicationContext());
        if (-1 == playerId) {
            setNewPlayerId(getApplicationContext().getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        }
    }

    private void downloadAllClues() {
        int playerId = getPlayerId(getApplicationContext());
        APIBuilder.build().getClues(playerId).subscribe(clueList -> {

            DaoSession daoSession = ((CustomApplication) getApplication()).getDaoSession();
            ClueDao clueDao = daoSession.getClueDao();

            clueDao.insertOrReplaceInTx(clueList);
        });
    }

    /**
     * Calls the APIBuilder and sets a new player ID.
     *
     * @param preferences
     */
    private static void setNewPlayerId(final SharedPreferences preferences) {
        preferences.edit().putInt(KEY_USER_ID, 42).apply();
        APIBuilder.build().register().subscribe(user -> {
            if (user != null) {
                preferences.edit().putInt(KEY_USER_ID, user.id).apply();
            }
        });
    }

    /**
     * Returns player ID if set, otherwise -1.
     *
     * @param applicationContext to access SharedPreferences
     * @return playerID or -1
     */
    public static int getPlayerId(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_USER_ID, -1);
    }


}
