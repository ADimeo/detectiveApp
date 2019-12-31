package de.hpi3d.gamepgrog.trap.api;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.view.KeyEventDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ApiService extends IntentService {

    private static final String PRE = "de.hpi3d.gameprog.trap.api.";
    private static final String NAME = PRE + "api_service";

    public static final String KEY_USER_ID = PRE + "userid";
    public static final String KEY_RESULT = PRE + "result";
    public static final String KEY_DATA_TYPE = PRE + "datatype";
    public static final String KEY_DATA = PRE + "data";
    public static final String KEY_TOKEN = PRE + "token";
    public static final String KEY_RECEIVER = PRE + "receiver";
    public static final String KEY_MANAGER = PRE + "manager";

    public static final String MANAGE_REGISTER = PRE + "register";
    public static final String MANAGE_GET_USER_STATUS = PRE + "get_user_status";
    public static final String MANAGE_SEND_FB_TOKEN = PRE + "send_fb_token";
    public static final String MANAGE_NEEDS_DATA = PRE + "needs_data";
    public static final String MANAGE_FETCH_TASKS = PRE + "fetch_tasks";
    public static final String MANAGE_ADD_DATA = PRE + "add_data";
    public static final String MANAGE_GET_CLUES = PRE + "get_clues";

    private ApiBuilder.API api;

    public ApiService() {
        super(NAME);
        api = ApiBuilder.build();
    }

    public void register(BackendIntent intent) {
        Response<User> result = execute(api.register());
        intent.sendBack(result);
    }

    public void getUserStatus(BackendIntent intent) {
        // TODO
    }

    public void sendFBToken(BackendIntent intent) {
        // TODO
    }

    public void needsData(BackendIntent intent) {
        // TODO
    }

    public void fetchTasks(BackendIntent intent) {
        // TODO
    }

    public void addData(BackendIntent intent) {
        List<UserData> data = intent.getExtra(KEY_DATA);
        String type = intent.getExtra(KEY_DATA_TYPE);
        int userid = intent.getExtra(KEY_USER_ID);

        Response<ResponseBody> result = execute(api.addData(userid, type, data));
        intent.sendBack(result);
    }

    public void getClues(BackendIntent intent) {
        // TODO
    }

    private <T> Response<T> execute(Call<T> call) {
        Response<T> res = null;
        try {
            res = call.execute();
        } catch (IOException ignored) {
        }

        if (res == null) {
            return Response.error(-1, ResponseBody.create(null, "IO Exception"));
        }
        return res;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            BackendIntent bIntent = new BackendIntent(intent);
            String type = bIntent.getManagerName();
            getManager(type).accept(bIntent);
        }
    }

    private Consumer<BackendIntent> getManager(String manager) {
        switch (manager) {
            case MANAGE_REGISTER:
                return this::register;
            case MANAGE_GET_USER_STATUS:
                return this::getUserStatus;
            case MANAGE_ADD_DATA:
                return this::addData;
            case MANAGE_FETCH_TASKS:
                return this::fetchTasks;
            case MANAGE_GET_CLUES:
                return this::getClues;
            case MANAGE_NEEDS_DATA:
                return this::needsData;
            case MANAGE_SEND_FB_TOKEN:
                return this::sendFBToken;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
