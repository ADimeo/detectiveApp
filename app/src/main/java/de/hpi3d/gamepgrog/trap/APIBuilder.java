package de.hpi3d.gamepgrog.trap;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * APIBuilder representation for the APIBuilder
 *
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-game/wiki/API">APIBuilder Doku</a>
 */
public class APIBuilder {

    private final static String BASE_URL = "http://localhost:5000";

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

        @GET("user/{userid}/list-clues")
        Observable<List<APIBuilder.Clue>> listClues(@Path("userid") String userid);

        @GET("user/{userid}/list-personalized-clues")
        Observable<List<APIBuilder.PersonalizedClue>> listPersonalizedClues(@Path("userid") String userid);

        @GET("user/{userid}/personalized-clue/{clue}")
        Observable<APIBuilder.PersonalizedClue> personalizedClueFrom(@Path("clue") APIBuilder.Clue clue);
    }

    public class User {
        public int id;
    }

    public class Clue {
        public String key;
    }

    public class PersonalizedClue extends Clue {
        public String text;
    }
}
