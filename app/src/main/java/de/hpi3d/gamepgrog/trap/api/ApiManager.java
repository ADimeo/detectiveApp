package de.hpi3d.gamepgrog.trap.api;


import android.app.Activity;
import android.app.Application;
import android.app.Service;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
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
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Call Server endpoints.<br>
 * Example:
 * <pre><code>
 * ApiManager.api(this).isTaskFinished(userid, taskname).call((result, code) -> {})
 * </code></pre>
 * Endpoints are called asynchronously  and return an {@link ApiCall}
 *
 * If <i>safety-mode</i> is set to <code>true</code> in storage, endpoints annotated with
 * {@link UploadsData} will not be executed
 *
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-server/wiki/App-API">Endpoints</a>
 * @see <a href="https://square.github.io/retrofit/">Retrofit Library</a>
 */
public class ApiManager {

    private static OkHttpClient client = null;

    public static ServerApi api(Service a) {
        return api(a.getApplication());
    }

    public static ServerApi api(Activity a) {
        return api(a.getApplication());
    }

    /**
     * Builds a new Api connection
     * @param app the Application needed to access the storage and check safety-mode
     */
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

    /**
     * Interface for Server Endpoints
     *
     * GET/POST Annotations link the endpoint urls.
     * Parameters are enclosed in {}-Brackets and match method parameters annotated with @Path
     */
    public interface ServerApi {
        @POST("users/create")
        ApiCall<User> register();

        @PUT("users/{userid}/fbtoken/{token}")
        ApiCall<ResponseBody> sendFBToken(@Path("userid") long userid, @Path("token") String token);

        @GET("users/{userid}/tasks/{taskname}/finished")
        ApiCall<Boolean> isTaskFinished(@Path("userid") long userid, @Path("taskname") String taskname);

        @POST("users/{userid}/data/{datatype}")
        @UploadsData
        ApiCall<ResponseBody> addData(@Path("userid") int userid,
                                   @Path("datatype") String datatype,
                                   @Body List<UserData> data);

        @UploadsData
        @Multipart
        @POST("users/{userid}/data/image")
        ApiCall<ResponseBody> uploadImage(@Path("userid") int userid,
                                       @Part MultipartBody.Part file,
                                       @Part("name") RequestBody body);

        @PATCH("users/{userid}/reset")
        ApiCall<ResponseBody> reset(@Path("userid") int userid);

        @UploadsData
        @PUT("users/{userid}/telegram-code/{code}")
        ApiCall<ResponseBody> sendTelegramCode(@Path("userid") int userid, @Path("code") String code);

        @UploadsData
        @PUT("users/{userid}/phonenumber/{number}")
        ApiCall<ResponseBody> sendPhoneNumber(@Path("userid") int userid, @Path("number") String number);
    }
}

