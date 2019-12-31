package de.hpi3d.gamepgrog.trap.datatypes;

import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.Objects;

public class UserStatus extends ApiDataType {

    private long userId;
    private String currentStoryPoint;
    private String telegramHandle;
    private String telegramStartToken;

    public UserStatus(long userId, String currentStoryPoint, String telegramHandle, String telegramStartToken) {
        this.userId = userId;
        this.currentStoryPoint = currentStoryPoint;
        this.telegramHandle = telegramHandle;
        this.telegramStartToken = telegramStartToken;
    }

    @Override
    public Parcel toParcel() {
        Parcel p = Parcel.obtain();
        p.writeLong(userId);
        p.writeString(currentStoryPoint);
        p.writeString(telegramHandle);
        p.writeString(telegramStartToken);
        return p;
    }

    @Override
    protected void fromParcel(Parcel p) {
        userId = p.readLong();
        currentStoryPoint = p.readString();
        telegramHandle = p.readString();
        telegramStartToken = p.readString();
    }

    @Override
    public String getTypeName() {
        return "user_status";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStatus that = (UserStatus) o;
        return userId == that.userId &&
                Objects.equals(currentStoryPoint, that.currentStoryPoint) &&
                Objects.equals(telegramHandle, that.telegramHandle) &&
                Objects.equals(telegramStartToken, that.telegramStartToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, currentStoryPoint, telegramHandle, telegramStartToken);
    }
}
