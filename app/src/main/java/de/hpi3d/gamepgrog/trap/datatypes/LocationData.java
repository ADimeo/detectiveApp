package de.hpi3d.gamepgrog.trap.datatypes;

import android.location.Location;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class LocationData implements UserData {

    private double longitude, latitude;
    private long time;

    public LocationData(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.time = location.getTime();
    }

    @ParcelConstructor
    public LocationData(double longitude, double latitude, long time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public long getTime() {
        return time;
    }
}
