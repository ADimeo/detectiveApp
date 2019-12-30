package de.hpi3d.gamepgrog.trap.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.InvocationTargetException;

public class ApiDataParcel implements Parcelable {

    private Parcel p;

    public <T extends ApiDataType> ApiDataParcel(T data) {
        p = data.toParcel();
    }


    private ApiDataParcel(Parcel p) {
        this.p = p;
    }

    public static final Creator<ApiDataParcel> CREATOR = new Creator<ApiDataParcel>() {
        @Override
        public ApiDataParcel createFromParcel(Parcel in) {
            return new ApiDataParcel(in);
        }

        @Override
        public ApiDataParcel[] newArray(int size) {
            return new ApiDataParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.appendFrom(p, 0, p.dataSize());
    }

    public <T extends ApiDataType> T toData(Class<T> klass) {
        try {
            return klass.getConstructor(Parcel.class).newInstance(p);
        } catch (NoSuchMethodException | IllegalAccessException
                | InstantiationException | InvocationTargetException e) {
            // Should not happen
            e.printStackTrace();
            return null;
        }
    }
}
