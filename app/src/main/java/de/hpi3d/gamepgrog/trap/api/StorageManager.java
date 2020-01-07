package de.hpi3d.gamepgrog.trap.api;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Wrapper around everything persistence.
 */

public class StorageManager {


    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_BOT_URL = "key_bot_url";
    private static final String KEY_SHARED_PREFERENCES = "backend_manager_preferences";
    private static final String KEY_CONVERSATION_HAS_STARTED = "key_conversation_has_started";
    private static final String KEY_SAFETY_MODE = "key_safety_mode";
    private static final String KEY_FIREBASE_KEY = "key_firebase_key";

    /**
     * Returns player ID if set, otherwise -1.
     */
    public static int getPlayerId(Context applicationContext) {  // TODO rename to getUserId
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public static void setPlayerId(Context context, int userid) {
        SharedPreferences prefereces = context.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        prefereces.edit().putInt(KEY_USER_ID, userid).apply();
    }

    public static boolean hasRegisteredUser(Context context) {
        return getPlayerId(context) != -1;
    }

    public static void setPlayerFBToken(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_FIREBASE_KEY, token).apply();
    }

    public static String getPlayerFBToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(KEY_FIREBASE_KEY, null);
    }

    /**
     * Returns the saved URL used by the app to open the communication channel of the bot
     */
    public static String getBotUrl(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(KEY_BOT_URL, null);
    }


    /**
     * Returns the whether or not the player has tapped the "Contact Andy Abbot" Button.
     */
    public static boolean getHasPlayerStartedConversation(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_CONVERSATION_HAS_STARTED, false);
    }

    private static void setHasPlayerStartedConversation(Context applicationContext, boolean conversationStarted) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_CONVERSATION_HAS_STARTED, conversationStarted).apply();

    }


    public static void setSafetyMode(boolean safety, Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(KEY_SAFETY_MODE, safety).apply();
    }


    public static boolean isInSafetyMode(Context applicationContext) {
        SharedPreferences preferences = applicationContext.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_SAFETY_MODE, true);
    }
}
