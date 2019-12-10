package de.hpi3d.gamepgrog.trap.datatypes;

import androidx.annotation.NonNull;

public class UserStatus {
    public static final String STORY_POINT_INITIAL = "start_point";

    public long userId;
    public String currentStoryPoint;
    public String telegramHandle;
    public String telegramStartToken;


    @NonNull
    @Override
    public String toString() {
        return "ID: " + userId + " ||| Handle:" + telegramHandle;
    }
}
