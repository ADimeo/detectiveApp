package de.hpi3d.gamepgrog.trap.datatypes;

import android.Manifest;
import android.location.Location;

import androidx.annotation.Nullable;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class LocationData implements UserData {

    private double longitude, latitude;
    private long time;

    @ParcelConstructor
    public LocationData(double longitude, double latitude, long time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public static LocationData fromLocation(Location location) {
        return new LocationData(location.getLongitude(), location.getLatitude(), location.getTime());
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

    @Override
    public String[] requiredPermission() {
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    }
}
