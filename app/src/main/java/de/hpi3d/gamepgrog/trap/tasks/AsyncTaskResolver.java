package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.future.BiConsumer;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Promise;

public class AsyncTaskResolver<T extends UserData> extends TaskResolver<T> {

    private String datatypeName;
    private String[] permissionsNeeded;
    private int permissionsDialogMessageId = super.getPermissionsDialogMessageId();
    private BiConsumer<Activity, Consumer<List<T>>> fetcher;

    public AsyncTaskResolver(String datatypeName, String[] permissionsNeeded,
                             int permissionsDialogMessageId,
                             BiConsumer<Activity, Consumer<List<T>>> fetcher) {
        this.datatypeName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.permissionsDialogMessageId = permissionsDialogMessageId;
        this.fetcher = fetcher;
    }

    public AsyncTaskResolver(String datatypeName, String[] permissionsNeeded,
                             BiConsumer<Activity, Consumer<List<T>>> fetcher) {
        this.datatypeName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = fetcher;
    }

    @Override
    protected String getDatatypeName() {
        return datatypeName;
    }

    @Override
    protected String[] getPermissionsNeeded() {
        return permissionsNeeded;
    }

    @Override
    protected Promise<List<T>> fetchData(Activity app) {
        Promise<List<T>> p = Promise.create();
        fetcher.accept(app, p::resolve);
        return p;
    }

    @Override
    protected int getPermissionsDialogMessageId() {
        return permissionsDialogMessageId;
    }
}
