package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;

import java.util.List;

import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.future.BiConsumer;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import de.hpi3d.gamepgrog.trap.future.Promise;

/**
 * Resolver for Tasks where the fetching of Data is asynchronous
 */
public class AsyncTaskResolver<T extends UserData> extends TaskResolver<T> {

    private String datatypeName;
    private String taskName;
    private String[] permissionsNeeded;
    private BiConsumer<Activity, Consumer<List<T>>> fetcher;

    public AsyncTaskResolver(String datatypeName, String[] permissionsNeeded,
                             BiConsumer<Activity, Consumer<List<T>>> fetcher) {
        this.datatypeName = datatypeName;
        this.taskName = datatypeName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = fetcher;
    }

    public AsyncTaskResolver(String taskName, String datatypeName, String[] permissionsNeeded,
                             BiConsumer<Activity, Consumer<List<T>>> fetcher) {
        this.datatypeName = datatypeName;
        this.taskName = taskName;
        this.permissionsNeeded = permissionsNeeded;
        this.fetcher = fetcher;
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

    /**
     * Calls the given fetcher asynchronously
     */
    @Override
    protected Promise<List<T>> fetchData(Activity app) {
        Promise<List<T>> p = Promise.create();
        fetcher.accept(app, p::resolve);
        return p;
    }
}
