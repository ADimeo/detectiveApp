package de.hpi3d.gamepgrog.trap.android.firebase;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import de.hpi3d.gamepgrog.trap.tasks.Task;

/**
 * Parses messages from Firebase.<br>
 * Message have the following structure:
 * <pre><code>
 * call: CALL_NEW_TASKS || CALL_GET_TELEGRAM,
 * value?: string
 * </code></pre>
 *
 * <code>value</code> is set if <code>call == CALL_NEW_TASKS</code><br>
 * The value will be parse with {@link Gson#fromJson(String, Type)} to {@link Task}
 */
class FirebaseDataParser {

    public static final String CALL_NEW_TASKS = "newTasks";
    public static final String CALL_GET_TELEGRAM = "getTelegramCode";

    private static final String KEY_CALL = "call";
    private static final String KEY_VALUE = "value";

    private static final Type TYPE_TASKS = new TypeToken<ArrayList<Task>>() {}.getType();


    /**
     * @param data the map from {@link RemoteMessage#getData()}
     * @return true if the given map has a valid structure
     */
    static boolean isValid(Map<String, String> data) {
        // See class javadoc for message structure
        return data.containsKey(KEY_CALL)
                && data.containsKey(KEY_VALUE)
                && Arrays.asList(CALL_NEW_TASKS, CALL_GET_TELEGRAM).contains(data.get("call"));
    }

    static String getCall(Map<String, String> data) {
        return data.get(KEY_CALL);
    }

    static ArrayList<Task> parseTasks(Map<String, String> data) {
        return new Gson().fromJson(data.get("value"), TYPE_TASKS);
    }
}
