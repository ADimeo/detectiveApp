package de.hpi3d.gamepgrog.trap;


import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.UserDataPostRequestFactory;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
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
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(API.class);
    }

    public interface API {
        @GET("user/create")
        Observable<User> register();

        @POST("user/{userid}/data")
        Observable<ResponseBody> addData(@Path("userid") int userid,
                                   @Body UserDataPostRequestFactory.UserDataPostRequest userData);

        @GET("user/{userid}/clues")
        Observable<List<Clue>> getClues(@Path("userid") int userid);

        @GET("user/{userid}/trust")
        Observable<Float> getTrust(@Path("userid") int userid);

        @GET("user/{userid}/security")
        Observable<Float> getSecurity(@Path("userid") int userid);
    }

    public class User {
        public int id;
    }
}
