package de.hpi3d.gamepgrog.trap.datatypes;

import android.location.Location;
import android.os.Parcel;

import java.util.Objects;

public class LocationData extends ApiDataType {

    private double longitude, latitude;
    private long time;

    public LocationData(double longitude, double latitude, long time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    @Override
    public Parcel toParcel() {
        Parcel p = Parcel.obtain();
        p.writeDouble(longitude);
        p.writeDouble(latitude);
        p.writeLong(time);
        return p;
    }

    @Override
    protected void fromParcel(Parcel p) {
        longitude = p.readDouble();
        latitude = p.readDouble();
        time = p.readLong();
    }

    @Override
    public String getTypeName() {
        return "location";
    }

    public static LocationData fromLocation(Location location) {
        return new LocationData(location.getLongitude(), location.getLatitude(), location.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationData that = (LocationData) o;
        return Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.latitude, latitude) == 0 &&
                time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude, time);
    }
}
