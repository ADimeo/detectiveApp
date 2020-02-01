package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.future.Function;
import de.hpi3d.gamepgrog.trap.future.Promise;
import de.hpi3d.gamepgrog.trap.future.Supplier;

public class SyncTaskResolver<T extends UserData> extends TaskResolver<T> {

    private String datatypeName;
    private String[] permissionsNeeded;
    private int permissionsDialogMessageId = super.getPermissionsDialogMessageId();
    private Function<Context, List<T>> fetcher;


    public SyncTaskResolver(String datatypeName, String[] permissionsNeeded, Function<Context,
            List<T>> fetcher) {
        this.datatypeName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = fetcher;
    }

    public SyncTaskResolver(String datatypeName, String[] permissionsNeeded,
                            Supplier<List<T>> fetcher) {
        this.datatypeName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = (c) -> fetcher.get();
    }

    public SyncTaskResolver(String datatypeName, String[] permissionsNeeded,
                            int permissionsDialogMessageId, Function<Context, List<T>> fetcher) {
        this.datatypeName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.permissionsDialogMessageId = permissionsDialogMessageId;
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
        return Promise.createResolved(fetcher.apply(app));
    }

    @Override
    protected int getPermissionsDialogMessageId() {
        return permissionsDialogMessageId;
    }
}
