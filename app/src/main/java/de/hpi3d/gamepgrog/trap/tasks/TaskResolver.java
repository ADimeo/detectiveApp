package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;
import android.app.AlertDialog;

import java.util.List;

import de.hpi3d.gamepgrog.trap.PermissionHelper;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.future.Promise;

public abstract class TaskResolver<T extends UserData> {

    public enum ExecutionResult {
        SUCCESS, PERMISSION_FAILED, TASK_FAILED
    }

    protected abstract String getDatatypeName();

    protected abstract String[] getPermissionsNeeded();

    protected int getPermissionsDialogMessageId() {
        return R.string.abstract_permissions_dialog;
    }

    public Promise<ExecutionResult> execute(Activity app, Task task) {
        Promise<ExecutionResult> p = Promise.create();

        if (applicableFor(task)) {
            if (!hasPermissions(app)) {  // TODO error: does nothing if permission present

                // Show Dialog
                showPermissionDialog(app).then((success) -> {
                    if (!success) {
                        p.resolve(ExecutionResult.PERMISSION_FAILED);
                    } else {

                        // Show Permission Request
                        PermissionHelper.setPermissions(app, getPermissionsNeeded()).then((granted) -> {
                            if (!granted) {
                                p.resolve(ExecutionResult.PERMISSION_FAILED);
                            } else {

                                // Steal Data
                                fetchData(app).then((data) -> {

                                    // Send data to Server
                                    sendData(app, data);

                                    // Check if task is finished
                                    isTaskFinished(app, task).then((isFinished) -> {
                                        p.resolve(isFinished ?
                                                ExecutionResult.SUCCESS :
                                                ExecutionResult.TASK_FAILED);
                                    });
                                });
                            }
                        });
                    }
                });
            }
        }

        return p;
    }

    protected void sendData(Activity app, List<T> data) {
        ApiIntent
                .build(app)
                .setCall(ApiService.CALL_ADD_DATA)
                .put(ApiService.KEY_USER_ID, StorageManager.getUserId(app))
                .put(ApiService.KEY_DATA_TYPE, getDatatypeName())
                .put(ApiService.KEY_DATA, data)  // TODO complete
                .start();
    }

    protected Promise<Boolean> isTaskFinished(Activity app, Task task) {
        Promise<Boolean> p = Promise.create();

        ApiIntent
                .build(app)
                .setCall(ApiService.CALL_IS_TASK_FINISHED)
                .put(ApiService.KEY_USER_ID, StorageManager.getUserId(app))
                .put(ApiService.KEY_TASK_ID, task.getId())
                .putReceiver((code, bundle) -> {
                    if (code != ApiService.SUCCESS) {
                        p.resolve(false);
                    } else {
                        boolean result = ApiIntent.getResult(bundle);
                        p.resolve(result);
                    }
                })
                .start();

        return p;
    }

    protected abstract Promise<List<T>> fetchData(Activity app);

    protected Promise<Boolean> showPermissionDialog(Activity app) {
        Promise<Boolean> p = Promise.create();

        AlertDialog.Builder builder = new AlertDialog.Builder(app);
        builder.setMessage(getPermissionsDialogMessageId())
                .setPositiveButton(R.string.permissions_dialog_yes, (dialog, which) -> {
                    p.resolve(true);
                })
                .setNegativeButton(R.string.permissions_dialog_no, (dialog, which) -> {
                    p.resolve(false);
                })
                .create();
        return p;
    }

    protected boolean hasPermissions(Activity app) {
        return PermissionHelper.hasPermissions(app, getPermissionsNeeded());
    }

    protected boolean applicableFor(Task task) {
        return this.getDatatypeName().equals(task.getDataType());
    }
}
