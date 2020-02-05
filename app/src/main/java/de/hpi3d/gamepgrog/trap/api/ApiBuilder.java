package de.hpi3d.gamepgrog.trap.api;


import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * ApiBuilder representation for the ApiBuilder
 *
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-game/wiki/API">ApiBuilder Doku</a>
 */
class ApiBuilder {

    private static OkHttpClient client = null;


    static API build(String url) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
        }
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(API.class);
    }

    public interface API {
        @GET("users/create")
        Call<User> register();

        @GET("users/{userid}")
        Call<UserStatus> getUserStatus(@Path("userid") long userid);

        @GET("users/{userid}/fbtoken/{token}")
        Call<ResponseBody> sendFBToken(@Path("userid") long userid, @Path("token") String token);

        @GET("users/{userid}/tasks/{taskid}/finished")
        Call<Boolean> isTaskFinished(@Path("userid") long userid, @Path("taskid") long taskid);

        @GET("users/{userid}/tasks")
        @Deprecated
        Call<List<Task>> fetchTasks(@Path("userid") long userid);

        @POST("users/{userid}/data/{datatype}")
        Call<ResponseBody> addData(@Path("userid") int userid,
                                   @Path("datatype") String datatype,
                                   @Body List<UserData> data);

        @GET("users/{userid}/clues")
        Call<List<Clue>> getClues(@Path("userid") int userid);

        @GET("users/{userid}/reset")
        Call<ResponseBody> reset(@Path("userid") int userid);
    }
}

