package de.hpi3d.gamepgrog.trap.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class UserStatus implements Parcelable {
    public static final String STORY_POINT_INITIAL = "start_point";

    public long userId;
    public String currentStoryPoint;
    public String telegramHandle;
    public String telegramStartToken;

    public UserStatus(long userId, String currentStoryPoint, String telegramHandle, String telegramStartToken) {
        this.userId = userId;
        this.currentStoryPoint = currentStoryPoint;
        this.telegramHandle = telegramHandle;
        this.telegramStartToken = telegramStartToken;
    }

    private UserStatus(Parcel in) {
        userId = in.readLong();
        currentStoryPoint = in.readString();
        telegramHandle = in.readString();
        telegramStartToken = in.readString();
    }

    public static final Creator<UserStatus> CREATOR = new Creator<UserStatus>() {
        @Override
        public UserStatus createFromParcel(Parcel in) {
            return new UserStatus(in);
        }

        @Override
        public UserStatus[] newArray(int size) {
            return new UserStatus[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "ID: " + userId + " ||| Handle:" + telegramHandle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(currentStoryPoint);
        dest.writeString(telegramHandle);
        dest.writeString(telegramStartToken);
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
