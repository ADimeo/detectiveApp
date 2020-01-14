package de.hpi3d.gamepgrog.trap.api;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.greendao.AbstractDao;

import java.util.Collections;
import java.util.List;

import de.hpi3d.gamepgrog.trap.CustomApplication;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
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
    private static final String KEY_SAFETY_MODE = "key_safety_mode";
    private static final String KEY_FIREBASE_KEY = "key_firebase_key";

    public final Preference<Integer> userid;
    public final Preference<String> fbtoken;
    public final Preference<String> botUrl;
    public final Preference<Boolean> conversationStarted;
    public final Preference<Boolean> safetyMode;
    public final DaoPreferences<Task> tasks;
    public final DaoPreferences<Clue> clues;

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
        tasks = new DaoPreferences<>(app, DaoSession::getTaskDao);
        clues = new DaoPreferences<>(app, DaoSession::getClueDao);
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
        storage.clues.reset();
        storage.tasks.reset();
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

        public T get() {
            return getOrDefault(defaultValue);
        }

        public T getOrDefault(T defaultValue) {
            return getter.apply(getPreferences(), key, defaultValue);
        }

        public void set(T value) {
            SharedPreferences.Editor editor = getPreferences().edit();
            setter.accept(editor, key, value);
            editor.apply();
        }

        public boolean exists() {
            return !get().equals(defaultValue);
        }

        public void reset() {
            set(defaultValue);
        }

        private SharedPreferences getPreferences() {
            return c.getSharedPreferences(KEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public class DaoPreferences<T> {

        private Application app;
        private Function<DaoSession, AbstractDao<T, Long>> getDao;

        private DaoPreferences(Application app, Function<DaoSession, AbstractDao<T, Long>> getDao) {
            this.app = app;
            this.getDao = getDao;
        }

        public List<T> get() {
            return getDao().queryBuilder().list();
        }

        public void set(List<T> list) {
            reset();
            add(list);
        }

        public void add(T value) {
            add(Collections.singletonList(value));
        }

        public void add(List<T> values) {
            getDao().insertOrReplaceInTx(values);
        }

        public void remove(T value) {
            remove(Collections.singletonList(value));
        }

        public void remove(List<T> values) {
            getDao().deleteInTx(values);
        }

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
