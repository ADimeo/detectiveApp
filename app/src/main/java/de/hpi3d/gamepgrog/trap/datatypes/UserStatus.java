package de.hpi3d.gamepgrog.trap.datatypes;

import androidx.annotation.NonNull;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class UserStatus {

    private long userId;
    private String currentStoryPoint;
    private String telegramHandle;
    private String telegramStartToken;

    @ParcelConstructor
    public UserStatus(long userId, String currentStoryPoint, String telegramHandle, String telegramStartToken) {
        this.userId = userId;
        this.currentStoryPoint = currentStoryPoint;
        this.telegramHandle = telegramHandle;
        this.telegramStartToken = telegramStartToken;
    }

    public long getUserId() {
        return userId;
    }

    public String getCurrentStoryPoint() {
        return currentStoryPoint;
    }

    public String getTelegramHandle() {
        return telegramHandle;
    }

    public String getTelegramStartToken() {
        return telegramStartToken;
    }

    @NonNull
    @Override
    public String toString() {
        return "ID: " + userId + " ||| Handle:" + telegramHandle;
    }
}
