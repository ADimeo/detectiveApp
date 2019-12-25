package de.hpi3d.gamepgrog.trap.api;


import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class OfflineAPI implements ApiBuilder.API {

    @Override
    public Observable<User> register() {
        return null;
    }

    @Override
    public Observable<UserStatus> getUserStatus(long userid) {
        return new Observable<UserStatus>() {
            @Override
            protected void subscribeActual(Observer<? super UserStatus> observer) {

            }
        };
    }

    @Override
    public Observable<Response> addData(int userid, UserDataPostRequestFactory.UserDataPostRequest userData) {
        return new Observable<Response>() {
            @Override
            protected void subscribeActual(Observer<? super Response> observer) {

            }
        };
    }

    @Override
    public Observable<List<Clue>> getClues(int userid) {
        return new Observable<List<Clue>>() {
            @Override
            protected void subscribeActual(Observer<? super List<Clue>> observer) {

            }
        };
    }
}
