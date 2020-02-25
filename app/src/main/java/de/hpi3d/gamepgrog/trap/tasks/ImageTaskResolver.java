package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;

import java.util.List;

import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.Image;
import de.hpi3d.gamepgrog.trap.future.BiConsumer;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Promise;

public class ImageTaskResolver extends AsyncTaskResolver<Image> {

    public ImageTaskResolver(String datatypeName, String[] permissionsNeeded,
                             BiConsumer<Activity, Consumer<List<Image>>> fetcher) {
        super(datatypeName, permissionsNeeded, fetcher);
    }

    @Override
    protected Promise<Boolean> sendData(Activity app, List<Image> data) {
        Promise<Boolean> p = Promise.create();
        ApiIntent
                .build(app)
                .setCall(ApiService.CALL_UPLOAD_IMAGE)
                .put(ApiService.KEY_USER_ID, StorageManager.with(app).userid.get())
                .put(ApiService.KEY_DATA, data.get(0))
                .putReceiver((code, bundle) -> p.resolve(code == ApiService.SUCCESS))
                .start();
        return p;
    }
}
