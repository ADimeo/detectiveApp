package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;
import android.location.LocationManager;

import java.util.List;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.LocationStealer;
import de.hpi3d.gamepgrog.trap.datatypes.LocationData;
import de.hpi3d.gamepgrog.trap.future.BiConsumer;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Promise;

public class LocationTaskResolver extends AsyncTaskResolver<LocationData> {

    private static final int GPS_NOT_ENABLED = 6;

    public LocationTaskResolver(String datatypeName, String[] permissionsNeeded,
                                BiConsumer<Activity, Consumer<List<LocationData>>> fetcher) {
        super(datatypeName, permissionsNeeded, fetcher);
    }

    @Override
    protected int getResultMessage(Task task, int result) {
        if (result == GPS_NOT_ENABLED) {
            return R.string.gps_not_enabled;
        }
        return super.getResultMessage(task, result);
    }

    @Override
    protected Promise<List<LocationData>> fetchData(Activity app) {
        Promise<List<LocationData>> p = Promise.create();
        if (!LocationStealer.isGpsEnabled(app)) {
            return p.throwError(GPS_NOT_ENABLED);
        }
        return super.fetchData(app);
    }
}
