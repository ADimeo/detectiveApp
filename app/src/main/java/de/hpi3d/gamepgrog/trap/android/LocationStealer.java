package de.hpi3d.gamepgrog.trap.android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.future.Consumer;

public class LocationStealer {

    public static void takeLocationData(Context c, Consumer<List<LocationData>> callback) {
        LocationManager manager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        if (manager == null ||
                !hasPermissions(c) ||
                !isLocationEnabled(manager)) {
            callback.accept(Collections.emptyList());
            return;
        }

        try {
            Location oldLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (oldLocation != null) {
                long diff = Calendar.getInstance().getTimeInMillis() - oldLocation.getTime();
                if (diff < 60000) {
                    callback.accept(Collections.singletonList(new LocationData(oldLocation)));
                    return;
                }
            }

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1.0f, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        callback.accept(Collections.singletonList(new LocationData(location)));
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Toast.makeText(c, "GPS Enabled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(c, "GPS Disabled", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            // Ignore
            e.printStackTrace();
        }
    }

    private static boolean hasPermissions(Context c) {
        return c.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                c.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean isLocationEnabled(LocationManager manager) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.P || manager.isLocationEnabled();
    }
}
