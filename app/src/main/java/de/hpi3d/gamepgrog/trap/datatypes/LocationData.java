package de.hpi3d.gamepgrog.trap.datatypes;

import android.location.Location;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class LocationData implements UserData {

    private double longitude, latitude;
    private long timeInUtcSeconds;

    public LocationData(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.timeInUtcSeconds = location.getTime() / 1000;
    }

    @ParcelConstructor
    public LocationData(double longitude, double latitude, long timeInUtcSeconds) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timeInUtcSeconds = timeInUtcSeconds;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public long getTimeInUtcSeconds() {
        return timeInUtcSeconds;
    }
}
