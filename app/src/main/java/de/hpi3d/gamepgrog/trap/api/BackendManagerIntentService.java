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
import android.widget.Toast;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.hpi3d.gamepgrog.trap.CustomApplication;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.datatypes.ClueDao;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
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

    public static final String MANAGE_PLAYER_REGISTRATION = "manage_player_registration";
    public static final String MANAGE_TELEGRAM_BUTTON_STATUS = "manage_telegram_button_status";
    public static final String MANAGE_CLUE_DOWNLOAD = "manage_clue_download";
    public static final String MANAGE_GET_USER_STATUS = "manage_get_user_status";
    public static final String MANAGE_ADD_DATA = "manage_add_data";
    public static final String MANAGE_NEEDS_DATA = "manage_needs_data";
    public static final String MANAGE_FB_TOKEN = "manage_fb_token";

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_BOT_URL = "key_bot_url";
    private static final String KEY_FB_TOKEN = "key_fb_token";
    public static final String KEY_SHARED_PREFERENCES = "backend_manager_preferences";
    public static final String KEY_CONVERSATION_HAS_STARTED = "key_conversation_has_started";
    public static final String KEY_SAFETY_MODE = "key_safety_mode";

    private ApiBuilder.API api;


    public BackendManagerIntentService() {
        super("BackendManagerIntentService");
        api = ApiBuilder.build();
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
            case MANAGE_TELEGRAM_BUTTON_STATUS:
                updatePlayerConversationStatus();
                break;
            case MANAGE_CLUE_DOWNLOAD:
                downloadAndSaveAllClues();
                break;
            case MANAGE_ADD_DATA:
                uploadUserData(intent);
                break;
            case MANAGE_GET_USER_STATUS:
                loadUserStatus(intent);
                break;
            case MANAGE_NEEDS_DATA:
                needsUserData(intent);
                break;
            case MANAGE_FB_TOKEN:
                sendFBToken(intent);
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
            api.getUserStatus(playerID).subscribe(user -> {
                if (user != null && user.telegramHandle != null) {
                    setHasPlayerStartedConversation(context, true);
                }
            });
        }


    }

    private void downloadAndSaveAllClues() {
        int playerId = getPlayerId(getApplicationContext());

        api.getClues(playerId).subscribe(clueList -> {
            DaoSession daoSession = ((CustomApplication) getApplication()).getDaoSession();
            ClueDao clueDao = daoSession.getClueDao();
            clueDao.insertOrReplaceInTx(clueList);
        });

    }

    private void needsUserData(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        Bundle b = new Bundle();
        int code = -1;

        try {
            Response<ResponseBody> res = api.needsData(getPlayerId()).execute();
            if (res.body() != null) {
                b.putString("value", res.body().string());
                code = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (receiver != null) {
            receiver.send(code, b);
        }
    }

    private void uploadUserData(Intent intent) {
        UserDataPostRequestFactory.UserDataPostRequest pr = intent.getParcelableExtra("postRequest");

        if (pr != null) {
            Response res = null;
            try {
                res = api.addData(getPlayerId(), pr).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendToReceiver(intent, res);
        }
    }

    private void loadUserStatus(Intent intent) {
        UserStatus status = api.getUserStatus(getPlayerId()).blockingLast();
        sendToReceiver(intent, status);
    }

    private void sendFBToken(Intent intent) {
        String token = intent.getStringExtra("token");

        if (token != null && !token.equals("")) {
            setPlayerFBToken(getApplicationContext(), token);
            Response res = null;
            try {
                res = api.sendFBToken(getPlayerId(), token).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendToReceiver(intent, res);
        }
    }

    private void sendToReceiver(Intent intent, Response res) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");

        if (receiver != null) {
            int code = res != null ? res.code() : -1;
            receiver.send(code, new Bundle());
        }
    }

    private void sendToReceiver(Intent intent, Parcelable value) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");

        if (receiver != null) {
            if (value == null) {
                receiver.send(-1, new Bundle());
            } else {
                Bundle b = new Bundle();
                b.putParcelable("value", value);
                receiver.send(0, b);
            }
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
        ApiBuilder.build().register().subscribe(user -> {
            if (user != null) {
                Log.d("USER_OBJECT", user.toString());
                Log.d("PLAYER_ID", "SETTING PLAYER ID TO " + user.userId);
                preferences.edit().putInt(KEY_USER_ID, user.userId).apply();
                preferences.edit().putString(KEY_BOT_URL, user.registerURL).apply();
            }
        });
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

    public static BackendIntent buildIntent(Context context) {
        return new BackendIntent(context, new Intent(context, BackendManagerIntentService.class));
    }

    public static class BackendIntent {
        private Context context;
        private Intent intent;

        private BackendIntent(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        public BackendIntent modify(Consumer<Intent> consumer) {
            if (intent != null)
                consumer.accept(intent);
            return this;
        }

        public BackendIntent onReceive(BiConsumer<Integer, Bundle> receiver) {
            if (intent != null) {
                intent.putExtra("receiver", new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        receiver.accept(resultCode, resultData);
                    }
                });
            }
            return this;
        }

        public BackendIntent onReceive(Runnable callback) {
            return onReceive((code, bundle) -> callback.run());
        }

        public BackendIntent put(String key, Parcelable value) {
            if (intent != null)
                intent.putExtra(key, value);
            return this;
        }

        public BackendIntent put(String key, String value) {
            if (intent != null)
                intent.putExtra(key, value);
            return this;
        }

        public BackendIntent type(String type) {
            return put(KEY_MANAGE_TYPE, type);
        }

        public void start() {
            context.startService(intent);
        }

        public Intent build() {
            return intent;
        }
    }
}
