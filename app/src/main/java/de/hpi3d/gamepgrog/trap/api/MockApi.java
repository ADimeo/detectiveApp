package de.hpi3d.gamepgrog.trap.api;

import java.util.ArrayList;
import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MockApi implements ApiBuilder.API {

    @Override
    public Call<User> register() {
        return new NoUploadApi.NoCall<>(() ->
                new User(10, "telegram.me/AndyAbbot"));
    }

    @Override
    public Call<UserStatus> getUserStatus(long userid) {
        return new NoUploadApi.NoCall<>(() ->
                new UserStatus(10, "", "", ""));
    }

    @Override
    public Call<ResponseBody> sendFBToken(long userid, String token) {
        return NoUploadApi.NoCall.emptyResponse();
    }

    @Override
    public Call<Boolean> isTaskFinished(long userid, long taskid) {
        return new NoUploadApi.NoCall<>(() -> false);
    }

    @Override
    public Call<List<Task>> fetchTasks(long userid) {
        return null;
    }

    @Override
    public Call<ResponseBody> addData(int userid, String datatype, List<UserData> data) {
        return NoUploadApi.NoCall.emptyResponse();
    }

    @Override
    public Call<List<Clue>> getClues(int userid) {
        return new NoUploadApi.NoCall<>(ArrayList::new);
    }

    @Override
    public Call<ResponseBody> reset(int userid) {
        return NoUploadApi.NoCall.emptyResponse();
    }

    @Override
    public Call<ResponseBody> sendTelegramCode(int userid, String code) {
        return NoUploadApi.NoCall.emptyResponse();
    }
}
