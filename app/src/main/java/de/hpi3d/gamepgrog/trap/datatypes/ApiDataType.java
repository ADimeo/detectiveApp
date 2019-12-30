package de.hpi3d.gamepgrog.trap.datatypes;


import android.os.Parcel;

import de.hpi3d.gamepgrog.trap.api.UserDataPostRequestFactory;

public abstract class ApiDataType {

    ApiDataType() {}

    public ApiDataType(Parcel p) {
        fromParcel(p);
    }

    public abstract Parcel toParcel();
    protected abstract void fromParcel(Parcel p);

    public abstract void appendToPR(UserDataPostRequestFactory.UserDataPostRequest pr);
}
