package de.hpi3d.gamepgrog.trap;


import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * APIBuilder representation for the Server
 *
 * @see <a href="https://github.com/EatingBacon/gameprog-detective-game/wiki/API">APIBuilder Doku</a>
 */
public class APIBuilder {

    private final static String BASE_URL = "https://localhost"; //TODO: Server location

    public static API build() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(API.class);
    }

    interface API {
        @GET("user")
        Call<String> register();

        @GET("user/{userid}/list-clues")
        Call<List<Clue>> listClues(@Path("userid") String userid);

        @GET("user/{userid}/list-personalized-clues")
        Call<List<PersonalizedClue>> listPersonalizedClues(@Path("userid") String userid);

        @GET("user/{userid}/personalized-clue/{clue}")
        Call<PersonalizedClue> personalizedClueFrom(@Path("clue") Clue clue);
    }

    class Clue {
        public String key;
    }

    class PersonalizedClue extends Clue {
        public String text;
    }
}
