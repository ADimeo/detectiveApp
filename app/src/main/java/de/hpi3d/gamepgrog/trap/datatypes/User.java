package de.hpi3d.gamepgrog.trap.datatypes;

import androidx.annotation.NonNull;

public class User {
    public int userId;
    public String registerURL;

    @NonNull
    @Override
    public String toString() {
        return userId + " ||| " + registerURL;
    }
}
