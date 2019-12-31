package de.hpi3d.gamepgrog.trap.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.hpi3d.gamepgrog.trap.CustomApplication;
import de.hpi3d.gamepgrog.trap.datatypes.ClueDao;
import de.hpi3d.gamepgrog.trap.datatypes.DaoSession;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * This class is where decisions about backend calls are made.
 * It offers methods which can be called from where ever, and
 * makes decisions/finds out the games state.
 * Based on that it may or may not call the backend.
 * <p>
 * Also functions as ad hoc storage for lots of stuff.
 * <p>
 * Methods in this class may be blocking.
 */

public class BackendManagerIntentService extends IntentService {


    public static final String KEY_MANAGE_TYPE = "key_manage_type";

    public static final String MANAGE_TELEGRAM_BUTTON_STATUS = "manage_telegram_button_status";

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_BOT_URL = "key_bot_url";
    public static final String KEY_SHARED_PREFERENCES = "backend_manager_preferences";
    public static final String KEY_CONVERSATION_HAS_STARTED = "key_conversation_has_started";
    public static final String KEY_SAFETY_MODE = "key_safety_mode";


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
            case MANAGE_TELEGRAM_BUTTON_STATUS:
                updatePlayerConversationStatus();
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
        Log.d("REGISTER", "REGISTERING PLAYER IF UNREGISTERED");
        int playerId = getPlayerId(getApplicationContext());
        if (-1 == playerId) {
            setNewPlayerId(getApplicationContext());
        }
    }


    /**
     * Sends an API request to the server that finds out if the player has started conversing with
     * the bot.
     */
    private void updatePlayerConversationStatus() {
        Context context = getApplicationContext();
        long playerID = getPlayerId(context);
        if (getHasPlayerStartedConversation(context)) {
            return;
        }


        if (!isInSafetyMode(context)) {
            // TODO call api, update telegram handle
        }


    }

    /**
     * Calls the ApiBuilder and sets a new player ID.
     * <p>
     * Also sets the initial link of the app with token
     *
     * @param context
     */
    private static void setNewPlayerId(final Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // TODO Update in API
    }

    public int getPlayerId() {
        return getPlayerId(getApplicationContext());
    }

    /**
     * Returns player ID if set, otherwise -1.
     *
     * @param applicationContext to access SharedPreferences
     * @return playerID or -1
     */
    public static int getPlayerId(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Log.d("PLAYER_ID", "GETTING PLAYER ID: " + preferences.getInt(KEY_USER_ID, -1));
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public static void setPlayerFBToken(Context context, String token) {
        // TODO save token in Preferences
    }

    public static String getPlayerFBToken(Context context) {
        // TODO get token from Preferences
        return "";
    }

    /**
     * Returns the saved URL used by the app to open the communication channel of the bot
     *
     * @param applicationContext
     * @return
     */
    public static String getBotUrl(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(KEY_BOT_URL, null);
    }


    /**
     * Returns the whether or not the player has tapped the "Contact Andy Abbot" Button.
     *
     * @param applicationContext
     * @return
     */
    public static boolean getHasPlayerStartedConversation(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_CONVERSATION_HAS_STARTED, false);
    }

    private static void setHasPlayerStartedConversation(Context applicationContext, boolean conversationStarted) {
        Log.d("SETTING_STUFF", "SETTING PLAYER STARTED CONVERSATION TO " + conversationStarted);
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_CONVERSATION_HAS_STARTED, conversationStarted).apply();

    }


    public static void setSafetyMode(boolean safety, Context applicationContext) {
        Log.d("SAFETY MODE", "SAFETY IS " + safety);
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_SAFETY_MODE, safety).apply();
    }


    public static boolean isInSafetyMode(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        return preferences.getBoolean(KEY_SAFETY_MODE, true);
        return false;
    }
}
