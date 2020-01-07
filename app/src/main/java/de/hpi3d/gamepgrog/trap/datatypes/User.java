package de.hpi3d.gamepgrog.trap.datatypes;

import androidx.annotation.NonNull;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class User {
    private int userId;
    private String registerURL;

    @ParcelConstructor
    public User(int userId, String registerURL) {
        this.userId = userId;
        this.registerURL = registerURL;
    }

    public int getUserId() {
        return userId;
    }

    public String getRegisterURL() {
        return registerURL;
    }

    @NonNull
    @Override
    public String toString() {
        return userId + " ||| " + registerURL;
    }
}
