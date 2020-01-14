package de.hpi3d.gamepgrog.trap.api;


import org.parceler.ParcelConstructor;

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

    private final static String BASE_URL = "http://78.47.11.229:8080";
    private static OkHttpClient client = null;


    static API build() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
        }
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(API.class);
    }


    public interface API {
        @GET("user/create")
        Call<User> register();

        @GET("user/{userid}")
        Call<UserStatus> getUserStatus(@Path("userid") long userid);

        @GET("user/{userid}/fbtoken/{token}")
        Call<ResponseBody> sendFBToken(@Path("userid") long userid, @Path("token") String token);

        @GET("user/{userid}/task/{taskid}/finished")
        Call<Boolean> isTaskFinished(@Path("userid") long userid, @Path("taskid") long taskid);

        @GET("user/{userid}/tasks")
        Call<List<Task>> fetchTasks(@Path("userid") long userid);

        @POST("user/{userid}/data/{datatype}")
        Call<ResponseBody> addData(@Path("userid") int userid,
                                   @Path("datatype") String datatype,
                                   @Body List<UserData> data);

        @GET("user/{userid}/clues")
        Call<List<Clue>> getClues(@Path("userid") int userid);

        @GET("user/{userid}/reset")
        Call<ResponseBody> reset(@Path("userid") int userid);
    }
}

