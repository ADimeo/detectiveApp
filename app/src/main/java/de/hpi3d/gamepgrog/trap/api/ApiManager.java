package de.hpi3d.gamepgrog.trap.api;


import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * ApiBuilder representation for the ApiBuilder
 *
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-game/wiki/API">ApiBuilder Doku</a>
 */
public class ApiManager {

    private static OkHttpClient client = null;

    public static ServerApi api(Service a) {
        return api(a.getApplication());
    }

    public static ServerApi api(Activity a) {
        return api(a.getApplication());
    }

    public static ServerApi api(Application app) {
        String url = StorageManager.with(app).serverUrl.get();

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
                .addCallAdapterFactory(ApiCallAdapter.Factory.create(app))
                .client(client)
                .build()
                .create(ServerApi.class);
    }

    public interface ServerApi {
        @GET("users/create")
        ApiCall<User> register();

        @GET("users/{userid}")
        ApiCall<UserStatus> getUserStatus(@Path("userid") long userid);

        @GET("users/{userid}/fbtoken/{token}")
        ApiCall<ResponseBody> sendFBToken(@Path("userid") long userid, @Path("token") String token);

        @GET("users/{userid}/tasks/{taskname}/finished")
        ApiCall<Boolean> isTaskFinished(@Path("userid") long userid, @Path("taskname") String taskname);

        @Deprecated
        @GET("users/{userid}/tasks")
        ApiCall<List<Task>> fetchTasks(@Path("userid") long userid);

        @UploadsData
        @POST("users/{userid}/data/{datatype}")
        ApiCall<ResponseBody> addData(@Path("userid") int userid,
                                   @Path("datatype") String datatype,
                                   @Body List<UserData> data);

        @UploadsData
        @Multipart
        @POST("users/{userid}/data/image")
        ApiCall<ResponseBody> uploadImage(@Path("userid") int userid,
                                       @Part MultipartBody.Part file,
                                       @Part("name") RequestBody body);

        @Deprecated
        @GET("users/{userid}/clues")
        ApiCall<List<Clue>> getClues(@Path("userid") int userid);

        @GET("users/{userid}/reset")
        ApiCall<ResponseBody> reset(@Path("userid") int userid);

        @UploadsData
        @POST("users/{userid}/telegram-code/{code}")
        ApiCall<ResponseBody> sendTelegramCode(@Path("userid") int userid, @Path("code") String code);

        @UploadsData
        @POST("users/{userid}/phonenumber/{number}")
        ApiCall<ResponseBody> sendPhoneNumber(@Path("userid") int userid, @Path("number") String number);
    }
}

