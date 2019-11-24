package de.hpi3d.gamepgrog.trap.datatypes;

import android.location.Location;

public class LocationData {

    private double longitude, latitude;
    private long time;

    public LocationData(double longitude, double latitude, long time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public static LocationData fromLocation(Location location) {
        return new LocationData(location.getLongitude(), location.getLatitude(), location.getTime());
    }
}
