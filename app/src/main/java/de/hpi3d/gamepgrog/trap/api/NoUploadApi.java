package de.hpi3d.gamepgrog.trap.api;

import android.util.Log;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import de.hpi3d.gamepgrog.trap.future.Supplier;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoUploadApi implements ApiBuilder.API {

    private final static String TAG = "NoUploadApi";

    private ApiBuilder.API api;

    NoUploadApi(ApiBuilder.API api) {
        this.api = api;
    }

    @Override
    public Call<User> register() {
        return api.register();
    }

    @Override
    public Call<UserStatus> getUserStatus(long userid) {
        return api.getUserStatus(userid);
    }

    @Override
    public Call<ResponseBody> sendFBToken(long userid, String token) {
        return api.sendFBToken(userid, token);
    }

    @Override
    public Call<Boolean> isTaskFinished(long userid, String taskname) {
        return api.isTaskFinished(userid, taskname);
    }

    @Override
    public Call<List<Task>> fetchTasks(long userid) {
        return api.fetchTasks(userid);
    }

    @Override
    public Call<ResponseBody> addData(int userid, String datatype, List<UserData> data) {
        Log.d(TAG, "Safety Mode is on. Blocked Data Upload");
        return NoCall.emptyResponse();
    }

    @Override
    public Call<ResponseBody> uploadImage(int userid, MultipartBody.Part file, RequestBody body) {
        Log.d(TAG, "Safety Mode is on. Blocked Data Upload");
        return NoCall.emptyResponse();
    }


    @Override
    public Call<ResponseBody> reset(int userid) {
        return api.reset(userid);
    }

    @Override
    public Call<ResponseBody> sendTelegramCode(int userid, String code) {
        return api.sendTelegramCode(userid, code);
    }

    @Override
    public Call<ResponseBody> sendPhoneNumber(int userid, String number) {
        return api.sendPhoneNumber(userid, number);
    }

    static class NoCall<T> implements Call<T> {

        private Supplier<T> defaultSupplier;
        private boolean executed = false;
        private boolean canceled = false;

        NoCall(Supplier<T> defaultSupplier) {
            this.defaultSupplier = defaultSupplier;
        }

        @Override
        public Response<T> execute() {
            executed = true;
            return Response.success(defaultSupplier.get());
        }

        @Override
        public void enqueue(Callback callback) {}

        @Override
        public boolean isExecuted() {
            return executed;
        }

        @Override
        public void cancel() {
            canceled = true;
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public Call<T> clone() {
            return new NoCall<>(defaultSupplier);
        }

        @Override
        public Request request() {
            return null;
        }

        public static Call<ResponseBody> emptyResponse() {
            return new NoCall<>(() -> ResponseBody.create(null, ""));
        }
    }
}
