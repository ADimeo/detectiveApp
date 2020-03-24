package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.future.Function;
import de.hpi3d.gamepgrog.trap.future.Promise;
import de.hpi3d.gamepgrog.trap.future.Supplier;

public class SyncTaskResolver<T extends UserData> extends TaskResolver<T> {

    private String taskName;
    private String datatypeName;
    private String[] permissionsNeeded;
    private Function<Activity, List<T>> fetcher;


    public SyncTaskResolver(String datatypeName, String[] permissionsNeeded, Function<Activity,
            List<T>> fetcher) {
        this.datatypeName = datatypeName;
        this.taskName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = fetcher;
    }

    public SyncTaskResolver(String taskName, String datatypeName, String[] permissionsNeeded, Function<Activity,
            List<T>> fetcher) {
        this.datatypeName = datatypeName;
        this.taskName = taskName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = fetcher;
    }

    public SyncTaskResolver(String datatypeName, String[] permissionsNeeded,
                            Supplier<List<T>> fetcher) {
        this.datatypeName = datatypeName;
        this.taskName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = (c) -> fetcher.get();
    }

    @Override
    protected String getDatatypeName() {
        return datatypeName;
    }

    @Override
    protected String getTaskName() {
        return taskName;
    }

    @Override
    protected String[] getPermissionsNeeded() {
        return permissionsNeeded;
    }

    @Override
    protected Promise<List<T>> fetchData(Activity app) {
        return Promise.createResolved(fetcher.apply(app));
    }
}
