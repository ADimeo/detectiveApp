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

    protected static final int SUCCESS = 1;
    protected static final int ANOTHER_TASK_EXECUTING = 2;
    protected static final int PERMISSION_FAILED = 3;
    protected static final int UPLOAD_FAILED = 4;
    protected static final int TASK_FAILED = 5;

    protected static boolean inExecution = false;

    protected abstract String getDatatypeName();

    protected abstract String getTaskName();

    protected abstract String[] getPermissionsNeeded();

    protected void showResultMessage(Activity app, Task task, int result) {
        Toast.makeText(app, getResultMessage(task, result), Toast.LENGTH_SHORT).show();
    }

    protected int getResultMessage(Task task, int result) {
        switch (result) {
            case SUCCESS:
                return R.string.task_success;
            case ANOTHER_TASK_EXECUTING:
                return R.string.task_is_executing;
            case PERMISSION_FAILED:
                return R.string.task_permission_failed;
            case UPLOAD_FAILED:
                return R.string.task_upload_failed;
            case TASK_FAILED:
                return R.string.task_data_failed;
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

    public Promise<Integer> execute(Activity app, Task task) {
        Promise<Integer> p = Promise.create();
        if (inExecution) {
            p.resolve(ANOTHER_TASK_EXECUTING);
            return p;
        }

        inExecution = true;
        if (applicableFor(task)) {
            if (hasPermissions(app)) {
                executeWithPermissionsPresent(app, task, p);
            } else {

                // Show Dialog
                showPermissionDialog(app, task).then((success) -> {
                    if (!success) {
                        p.resolve(PERMISSION_FAILED);
                        inExecution = false;
                    } else {

                        // Show Permission Request
                        PermissionHelper.setPermissions(app, getPermissionsNeeded()).then((granted) -> {
                            if (!granted) {
                                p.resolve(PERMISSION_FAILED);
                                inExecution = false;
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

    protected void executeWithPermissionsPresent(Activity app, Task task, Promise<Integer> p) {
        // Steal Data
        fetchData(app).then((data) -> {
            if (data.isEmpty()) {
                p.resolve(TASK_FAILED);
                inExecution = false;
                return;
            }

            // Send data to Server
            sendData(app, data).then((success) -> {
                if (!success) {
                    p.resolve(UPLOAD_FAILED);
                    inExecution = false;
                } else {

                    // Check if task is finished
                    isTaskFinished(app, task).then((isFinished) -> {
                        if (isFinished) {
                            task.setFinished(true);
                        }

                        p.resolve(isFinished ? SUCCESS : TASK_FAILED);
                        inExecution = false;
                    });
                }
            });
        }).error(code -> {
            p.resolve(code);
            inExecution = false;
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
                .put(ApiService.KEY_TASK_NAME, task.getName())
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

    protected Promise<Boolean> showPermissionDialog(Activity app, Task task) {
        Promise<Boolean> p = Promise.create();

        CharSequence explanation = task.getPermissionExplanation();
        if (explanation == null) {
            explanation = app.getApplicationContext().getText(R.string.permission_dialog_explanation);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(app);
        builder.setMessage(explanation)
                .setPositiveButton(R.string.permissions_dialog_yes, (dialog, which) -> {
                    p.resolve(true);
                })
                .setNegativeButton(R.string.permissions_dialog_no, (dialog, which) -> {
                    p.resolve(false);
                })
                .setOnCancelListener(dialog -> {
                    p.resolve(false);
                })
                .create().show();
        return p;
    }

    protected boolean hasPermissions(Activity app) {
        return PermissionHelper.hasPermissions(app, getPermissionsNeeded());
    }

    protected boolean applicableFor(Task task) {
        return this.getTaskName().equals(task.getDatatype());
    }
}
