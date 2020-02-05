package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

import de.hpi3d.gamepgrog.trap.android.PermissionHelper;
import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.future.EmptyPromise;
import de.hpi3d.gamepgrog.trap.future.Promise;

public abstract class TaskResolver<T extends UserData> {

    public enum ExecutionResult {
        SUCCESS, PERMISSION_FAILED, UPLOAD_FAILED, TASK_FAILED
    }

    protected abstract String getDatatypeName();

    protected abstract String[] getPermissionsNeeded();

    protected int getPermissionsDialogMessageId() {
        return R.string.abstract_permissions_dialog;
    }

    protected void showResultMessage(Activity app, Task task, ExecutionResult result) {
        Toast.makeText(app, getResultMessage(task, result), Toast.LENGTH_SHORT).show();
    }

    protected String getResultMessage(Task task, ExecutionResult result) {
        switch (result) {
            case SUCCESS:
                return "Task fulfilled successfully";
            case PERMISSION_FAILED:
                return "Task cannot be fulfilled without permissions";
            case UPLOAD_FAILED:
                return "No network connection, please try again later";
            case TASK_FAILED:
                return "Task failed, there is something wrong with your data";
            default:
                throw new IllegalStateException("");
        }
    }

    public EmptyPromise executeAndShowResult(Activity app, Task task) {
        EmptyPromise p = EmptyPromise.create();

        execute(app, task).then((result) -> {
            showResultMessage(app, task, result);
            p.resolve();
        });

        return p;
    }

    public Promise<ExecutionResult> execute(Activity app, Task task) {
        Promise<ExecutionResult> p = Promise.create();

        if (applicableFor(task)) {
            if (hasPermissions(app)) {
                executeWithPermissionsPresent(app, task, p);
            } else {

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
                                executeWithPermissionsPresent(app, task, p);
                            }
                        });
                    }
                });
            }
        }

        return p;
    }

    protected void executeWithPermissionsPresent(Activity app, Task task, Promise<ExecutionResult> p) {
        // Steal Data
        fetchData(app).then((data) -> {

            // Send data to Server
            sendData(app, data).then((success) -> {
                if (!success) {
                    p.resolve(ExecutionResult.UPLOAD_FAILED);
                } else {

                    // Check if task is finished
                    isTaskFinished(app, task).then((isFinished) -> {
                        if (isFinished) {
                            task.setFinished(true);
                        }

                        p.resolve(isFinished ?
                                ExecutionResult.SUCCESS :
                                ExecutionResult.TASK_FAILED);
                    });
                }
            });
        });
    }

    protected Promise<Boolean> sendData(Activity app, List<T> data) {
        Promise<Boolean> p = Promise.create();
        ApiIntent
                .build(app)
                .setCall(ApiService.CALL_ADD_DATA)
                .put(ApiService.KEY_USER_ID, StorageManager.with(app).userid.get())
                .put(ApiService.KEY_DATA_TYPE, getDatatypeName())
                .put(ApiService.KEY_DATA, data)
                .putReceiver((code, bundle) -> p.resolve(code == ApiService.SUCCESS))
                .start();
        return p;
    }

    protected Promise<Boolean> isTaskFinished(Activity app, Task task) {
        Promise<Boolean> p = Promise.create();

        ApiIntent
                .build(app)
                .setCall(ApiService.CALL_IS_TASK_FINISHED)
                .put(ApiService.KEY_USER_ID, StorageManager.with(app).userid.get())
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
                .create().show();
        return p;
    }

    protected boolean hasPermissions(Activity app) {
        return PermissionHelper.hasPermissions(app, getPermissionsNeeded());
    }

    protected boolean applicableFor(Task task) {
        return this.getDatatypeName().equals(task.getDatatype());
    }
}
