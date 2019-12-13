package de.hpi3d.gamepgrog.trap.api;


import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.datatypes.User;
import de.hpi3d.gamepgrog.trap.datatypes.UserStatus;
import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.ResponseBody;

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
    public Observable<ResponseBody> addData(int userid, UserDataPostRequestFactory.UserDataPostRequest userData) {
        return new Observable<ResponseBody>() {
            @Override
            protected void subscribeActual(Observer<? super ResponseBody> observer) {

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
