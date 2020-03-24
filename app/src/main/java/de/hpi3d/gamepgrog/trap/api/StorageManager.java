package de.hpi3d.gamepgrog.trap.api;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.greenrobot.greendao.AbstractDao;

import java.util.Collections;
import java.util.List;

import de.hpi3d.gamepgrog.trap.CustomApplication;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.future.Function;
import de.hpi3d.gamepgrog.trap.future.TriConsumer;
import de.hpi3d.gamepgrog.trap.future.TriFunction;
import de.hpi3d.gamepgrog.trap.tasks.DaoSession;
import de.hpi3d.gamepgrog.trap.tasks.Task;


/**
 * Wrapper around everything persistence.
 * A typical call looks like:
 * <pre>
 * {@code
 * StorageManager.with(application).userid.set(42);
 * }
 * </pre>
 * Can be reset
 */
public class StorageManager {


    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_BOT_URL = "key_bot_url";
    private static final String KEY_SHARED_PREFERENCES = "backend_manager_preferences";
    private static final String KEY_CONVERSATION_HAS_STARTED = "key_conversation_has_started";
    private static final String KEY_SAFETY_MODE = "key_safety_mode"; // Also defined in strings.xml
    private static final String KEY_SERVER_URL = "key_server_url";
    private static final String KEY_MOCK_API = "key_mock_api";
    private static final String KEY_PHONE_NUMBER = "key_phone_number";

    private static final String KEY_FIREBASE_KEY = "key_firebase_key";

    public static final String DEFAULT_SERVER_URL = "http://78.47.11.229:8080";

    public final Preference<Integer> userid;
    public final Preference<String> fbtoken;
    public final Preference<String> botUrl;
    public final Preference<Boolean> conversationStarted;
    public final Preference<Boolean> safetyMode;
    public final Preference<Boolean> useMockApi;
    public final Preference<String> phoneNumber;
    public final Preference<String> serverUrl;
    public final DaoPreferences<Task> tasks;
    public final DaoPreferences<LocationData> locations;

    private StorageManager(Application app) {
        userid = new Preference<>(
                app, -1, KEY_USER_ID,
                SharedPreferences::getInt,
                SharedPreferences.Editor::putInt);
        fbtoken = new Preference<>(
                app, "", KEY_FIREBASE_KEY,
                SharedPreferences::getString,
                SharedPreferences.Editor::putString);
        botUrl = new Preference<>(
                app, "", KEY_BOT_URL,
                SharedPreferences::getString,
                SharedPreferences.Editor::putString);
        conversationStarted = new Preference<>(
                app, false, KEY_CONVERSATION_HAS_STARTED,
                SharedPreferences::getBoolean,
                SharedPreferences.Editor::putBoolean);
        safetyMode = new Preference<>(
                app, true, KEY_SAFETY_MODE,
                SharedPreferences::getBoolean,
                SharedPreferences.Editor::putBoolean);
        serverUrl = new Preference<>(
                app, DEFAULT_SERVER_URL, KEY_SERVER_URL,
                SharedPreferences::getString,
                SharedPreferences.Editor::putString);
        useMockApi = new Preference<>(
                app, false, KEY_MOCK_API,
                SharedPreferences::getBoolean,
                SharedPreferences.Editor::putBoolean);
        phoneNumber = new Preference<>(
                app, "", KEY_PHONE_NUMBER,
                SharedPreferences::getString,
                SharedPreferences.Editor::putString);
        tasks = new DaoPreferences<>(app, DaoSession::getTaskDao);
        locations = new DaoPreferences<>(app, DaoSession::getLocationDataDao);
    }

    public static StorageManager with(Application app) {
        return new StorageManager(app);
    }

    public static StorageManager with(Service service) {
        return with(service.getApplication());
    }

    public static StorageManager with(Activity activity) {
        return with(activity.getApplication());
    }

    public static void reset(Application app) {
        StorageManager storage = with(app);
        storage.userid.reset();
        storage.conversationStarted.reset();
        storage.tasks.reset();
        storage.botUrl.reset();
        Log.d("StorageManager", "Reset Storage");
    }

    @SuppressWarnings("WeakerAccess")
    public static class Preference<T> {

        private Context c;
        private T defaultValue;
        private String key;
        private TriFunction<SharedPreferences, String, T, T> getter;
        private TriConsumer<SharedPreferences.Editor, String, T> setter;

        private Preference(Context c, T defaultValue, String key,
                           TriFunction<SharedPreferences, String, T, T> getter,
                           TriConsumer<SharedPreferences.Editor, String, T> setter) {
            this.c = c;
            this.defaultValue = defaultValue;
            this.key = key;
            this.getter = getter;
            this.setter = setter;
        }

        /**
         * @return the value stored or a defaultValue if nothing is stored
         */
        public T get() {
            return getOrDefault(defaultValue);
        }


        /**
         * @param defaultValue The value to return if storage is empty
         * @return the value stored or defaultValue if nothing is stored
         */
        public T getOrDefault(T defaultValue) {
            try {
                return getter.apply(getPreferences(), key, defaultValue);
            } catch (NullPointerException e) {
                return defaultValue;
            }

        }

        /**
         * @param value the value to set
         */
        public void set(T value) {
            SharedPreferences.Editor editor = getPreferences().edit();
            setter.accept(editor, key, value);
            editor.apply();
        }

        /**
         * @return is something stored (is the value not the default value)
         */
        public boolean exists() {
            return !get().equals(defaultValue);
        }

        /**
         * Resets to the default value
         */
        public void reset() {
            set(defaultValue);
        }

        private SharedPreferences getPreferences() {
            return c.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
    }

    public class DaoPreferences<T> {

        private Application app;
        private Function<DaoSession, AbstractDao<T, Long>> getDao;

        private DaoPreferences(Application app, Function<DaoSession, AbstractDao<T, Long>> getDao) {
            this.app = app;
            this.getDao = getDao;
        }

        /**
         * @return a list of all values stored. Empty if nothing is stored
         */
        public List<T> get() {
            return getDao().queryBuilder().list();
        }

        /**
         * Deletes the stored values and stores the given list
         *
         * @param list list to store
         */
        public void set(List<T> list) {
            reset();
            add(list);
        }

        /**
         * @param value value to add to the existing values
         */
        public void add(T value) {
            add(Collections.singletonList(value));
        }

        /**
         * @param values values to add to the existing values
         */
        public void add(List<T> values) {
            getDao().insertOrReplaceInTx(values);
        }

        /**
         * @param value value to remove from storage if present
         */
        public void remove(T value) {
            remove(Collections.singletonList(value));
        }

        /**
         * @param values values to remove from storage if present
         */
        public void remove(List<T> values) {
            getDao().deleteInTx(values);
        }

        /**
         * Removes all values
         */
        public void reset() {
            getDao().deleteAll();
        }

        private AbstractDao<T, Long> getDao() {
            return getDao.apply(getSession());
        }

        private DaoSession getSession() {
            return ((CustomApplication) app).getDaoSession();
        }
    }
}
