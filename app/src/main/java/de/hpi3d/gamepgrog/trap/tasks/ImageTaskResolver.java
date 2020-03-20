package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;

import java.io.File;
import java.util.List;

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

public class ImageTaskResolver extends AsyncTaskResolver<Image> {

    public ImageTaskResolver(String datatypeName, String[] permissionsNeeded,
                             BiConsumer<Activity, Consumer<List<Image>>> fetcher) {
        super(datatypeName, permissionsNeeded, fetcher);
    }

    @Override
    protected Promise<Boolean> sendData(Activity app, List<Image> data) {
        Promise<Boolean> p = Promise.create();

        File f = data.get(0).toFile(app);

        RequestBody body = RequestBody.create(MediaType.parse("image/png"), f);
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", f.getName(), body);
        RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        ApiManager.api(app).uploadImage(
                StorageManager.with(app).userid.get(),
                part,
                desc
        ).call((result, code) -> p.resolve(code == ApiCall.SUCCESS));
        return p;
    }
}
