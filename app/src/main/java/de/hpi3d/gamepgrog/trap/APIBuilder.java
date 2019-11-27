package de.hpi3d.gamepgrog.trap;


import java.util.List;

import androidx.annotation.NonNull;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.UserDataPostRequestBuilder;
import io.reactivex.Observable;
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
 * APIBuilder representation for the APIBuilder
 *
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-game/wiki/API">APIBuilder Doku</a>
 */
public class APIBuilder {

    private final static String BASE_URL = "http://78.47.11.229:5000";

    public static API build() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();


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
        Call<ResponseBody> addData(@Path("userid") int userid,
                                   @Body UserDataPostRequestBuilder.UserDataPostRequest userData);

        @GET("user/{userid}/clues")
        Observable<List<Clue>> getClues(@Path("userid") int userid);
    }

    public class User {
        public int userId;
        public String registerURL;

        @NonNull
        @Override
        public String toString() {
            return userId + " ||| " + registerURL;
        }
    }


    /**
     * Found at endpoint /user/<user-id>
     */
    public class UserStatus {

        public static final String STORY_POINT_INITIAL = "start_point";

        public String currentStoryPoint;
        public String telegramHandle;
        public String telegramStartToken;
        public long userId;


        @NonNull
        @Override
        public String toString() {
            return "ID: " + userId + " ||| Handle:" + telegramHandle;
        }
    }

}
