package de.hpi3d.gamepgrog.trap.api;


import android.content.Context;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
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
public class ApiBuilder {

    private final static String BASE_URL = "http://78.47.11.229:5000";
    private static OkHttpClient client = null;


    public static API build(Context context) {
        if (BackendManagerIntentService.isInSafetyMode(context)) {
            throw new SecurityException("APPLICATION IS IN SAFETY MODE; UPLOADS ARE NOT ALLOWED");
        }

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
        Observable<User> register();

        @GET("user/{userid}")
        Observable<UserStatus> getUserStatus(@Path("userid") long userid);

        @POST("user/{userid}/data")
        Observable<Response> addData(@Path("userid") int userid,
                                     @Body UserDataPostRequestFactory.UserDataPostRequest userData);

        @GET("user/{userid}/clues")
        Observable<List<Clue>> getClues(@Path("userid") int userid);
    }
}

