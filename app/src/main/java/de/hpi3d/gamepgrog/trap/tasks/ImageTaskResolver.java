package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;

import java.io.File;
import java.util.List;

import de.hpi3d.gamepgrog.trap.android.CameraStealer;
import de.hpi3d.gamepgrog.trap.api.ApiCall;
import de.hpi3d.gamepgrog.trap.api.ApiManager;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.Image;
import de.hpi3d.gamepgrog.trap.future.BiConsumer;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Promise;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Takes an image with {@link CameraStealer#takeUserImage(Activity, Consumer)}.
 * Send it to the server
 *
 * @see <a href="https://github.com/ADimeo/gameprog-detective-app/issues/60">#60</a>
 */
public class ImageTaskResolver extends AsyncTaskResolver<Image> {

    public ImageTaskResolver(String datatypeName, String[] permissionsNeeded,
                             BiConsumer<Activity, Consumer<List<Image>>> fetcher) {
        super(datatypeName, permissionsNeeded, fetcher);
    }

    /**
     * Sends image
     */
    @Override
    protected Promise<Integer> sendData(Activity app, List<Image> data) {
        Promise<Integer> p = Promise.create();

        // Create file
        File f = data.get(0).toFile(app);

        // Create Request for Api
        RequestBody body = RequestBody.create(MediaType.parse("image/png"), f);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", f.getName(), body);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        ApiManager.api(app).uploadImage(
                StorageManager.with(app).userid.get(),
                part,
                desc
        ).call((result, code) -> p.resolve(code));
        return p;
    }
}
