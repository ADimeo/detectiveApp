package de.hpi3d.gamepgrog.trap.api;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.List;

import androidx.annotation.Nullable;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A Service which calls the Server API.
 * Never call this class directly, use {@link ApiIntent} to create a call.
 */
public class ApiService extends IntentService {

    private static final String PRE = "de.hpi3d.gameprog.trap.api.";
    private static final String NAME = PRE + "api_service";

    public static final String KEY_USER_ID = PRE + "userid";
    public static final String KEY_TASK_ID = PRE + "taskid";
    public static final String KEY_RESULT = PRE + "result";
    public static final String KEY_DATA_TYPE = PRE + "datatype";
    public static final String KEY_DATA = PRE + "data";
    public static final String KEY_TOKEN = PRE + "token";
    public static final String KEY_RECEIVER = PRE + "receiver";
    public static final String KEY_CALL = PRE + "manager";
    public static final String KEY_SAFETY = PRE + "safety";

    public static final int ERROR_EXCEPTION = -2;
    public static final int SUCCESS = 200;

    /**
     * Registers a new user.
     * <br>
     * Returns a {@link ResultReceiver} in {@link ApiService#KEY_RECEIVER} with
     * a HTTP error/success code and a {@link User} in {@link ApiService#KEY_RESULT}
     */
    public static final String CALL_REGISTER = PRE + "register";

    /**
     * Fetches the Status of a given User
     * <br>
     * Param: Userid (int) in {@link ApiService#KEY_USER_ID}<br>
     * Returns a {@link android.os.ResultReceiver} in {@link ApiService#KEY_RECEIVER} with
     * a HTTP error/success code and a {@link UserStatus} in {@link ApiService#KEY_RESULT}
     */
    public static final String CALL_GET_USER_STATUS = PRE + "get_user_status";

    /**
     * Updates the Firebase Token for a given User
     * <br>
     * Param: Userid (int) in {@link ApiService#KEY_USER_ID}<br>
     * Param: Firebase Token (String) in {@link ApiService#KEY_TOKEN}<br>
     * Returns a {@link android.os.ResultReceiver} in {@link ApiService#KEY_RECEIVER} with
     * a HTTP error/success code
     */
    public static final String CALL_SEND_FB_TOKEN = PRE + "send_fb_token";

    /**
     * Checks if the given task is finished
     * <br>
     * Param: UserId (int) in {@link ApiService#KEY_USER_ID}<br>
     * Param: TaskId (int) in {@link ApiService#KEY_TASK_ID}<br>
     * Returns a {@link android.os.ResultReceiver} in {@link ApiService#KEY_RECEIVER} with
     * a HTTP error/success or {@link ApiService#ERROR_EXCEPTION} code and
     * a {@link Boolean} in {@link ApiService#KEY_RESULT}
     */
    public static final String CALL_IS_TASK_FINISHED = PRE + "task_finished";

    /**
     * Fetches Tasks for the User
     * <br>
     * Param: Userid (int) in {@link ApiService#KEY_USER_ID}<br>
     * Returns a {@link android.os.ResultReceiver} in {@link ApiService#KEY_RECEIVER} with
     * a HTTP error/success code and
     * a {@link List} of {@link Task}s in {@link ApiService#KEY_RESULT}
     */
    public static final String CALL_FETCH_TASKS = PRE + "fetch_tasks";

    /**
     * Adds Data for the User
     * <br>
     * Param: Userid (int) in {@link ApiService#KEY_USER_ID}<br>
     * Param: Datatype (String) in {@link ApiService#KEY_DATA_TYPE}<br>
     * Param: Data ({@link List} of {@link UserData}) in {@link ApiService#KEY_DATA}<br>
     * Returns a {@link android.os.ResultReceiver} in {@link ApiService#KEY_RECEIVER} with
     * a HTTP error/success code
     */
    public static final String CALL_ADD_DATA = PRE + "add_data";

    /**
     * Fetches Clues for the User
     * <br>
     * Param: Userid (int) in {@link ApiService#KEY_USER_ID}<br>
     * Returns a {@link android.os.ResultReceiver} in {@link ApiService#KEY_RECEIVER} with
     * a HTTP error/success code and
     * a {@link List} of {@link Clue}s in {@link ApiService#KEY_RESULT}
     */
    public static final String CALL_GET_CLUES = PRE + "get_clues";

    private ApiBuilder.API api;

    public ApiService() {
        super(NAME);
        api = ApiBuilder.build();
    }

    private void register(ApiIntent intent) {
        Response<User> result = execute(api.register());
        intent.sendBack(result);
    }

    private void getUserStatus(ApiIntent intent) {
        int userid = intent.getExtra(KEY_USER_ID);
        Response<UserStatus> status = execute(api.getUserStatus(userid));
        intent.sendBack(status);
    }

    private void sendFBToken(ApiIntent intent) {
        int userid = intent.getExtra(KEY_USER_ID);
        String token = intent.getExtra(KEY_TOKEN);
        Response<ResponseBody> response = execute(api.sendFBToken(userid, token));
        intent.sendBack(response.code());
    }

    private void isTaskFinished(ApiIntent intent) {
        int userid = intent.getExtra(KEY_USER_ID);
        long taskid = intent.getExtra(KEY_TASK_ID);
        Response<Boolean> response = execute(api.isTaskFinished(userid, taskid));
        intent.sendBack(response);
    }

    private void fetchTasks(ApiIntent intent) {
        int userid = intent.getExtra(KEY_USER_ID);
        Response<List<Task>> tasks = execute(api.fetchTasks(userid));
        intent.sendBack(tasks);
    }

    private void addData(ApiIntent intent) {
        List<UserData> data = intent.getExtra(KEY_DATA);
        String type = intent.getExtra(KEY_DATA_TYPE);
        int userid = intent.getExtra(KEY_USER_ID);

        Response<ResponseBody> result = execute(api.addData(userid, type, data));
        intent.sendBack(result.code());
    }

    private void getClues(ApiIntent intent) {
        int userid = intent.getExtra(KEY_USER_ID);
        Response<List<Clue>> clues = execute(api.getClues(userid));
        intent.sendBack(clues);
    }

    private <T> Response<T> execute(Call<T> call) {
        Response<T> res = null;
        try {
            res = call.execute();
        } catch (IOException ignored) {
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        if (res == null) {
            return Response.error(-1, ResponseBody.create(null, "IO Exception"));
        }
        return res;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            ApiIntent bIntent = new ApiIntent(intent);
            String type = bIntent.getManagerName();
            boolean safety = StorageManager.isInSafetyMode(getApplicationContext());
            run(getManager(type), bIntent, safety);
        }
    }

    private void run(Consumer<ApiIntent> manager, ApiIntent intent, boolean safety) {
        ApiBuilder.API oldApi = api;
        api = safety ? new NoUploadApi(api) : api;
        manager.accept(intent);
        api = oldApi;
    }

    private Consumer<ApiIntent> getManager(String manager) {
        switch (manager) {
            case CALL_REGISTER:
                return this::register;
            case CALL_GET_USER_STATUS:
                return this::getUserStatus;
            case CALL_ADD_DATA:
                return this::addData;
            case CALL_FETCH_TASKS:
                return this::fetchTasks;
            case CALL_GET_CLUES:
                return this::getClues;
            case CALL_IS_TASK_FINISHED:
                return this::isTaskFinished;
            case CALL_SEND_FB_TOKEN:
                return this::sendFBToken;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
