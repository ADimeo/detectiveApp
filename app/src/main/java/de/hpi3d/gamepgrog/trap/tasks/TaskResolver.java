package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.PermissionHelper;
import de.hpi3d.gamepgrog.trap.api.ApiCall;
import de.hpi3d.gamepgrog.trap.api.ApiManager;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.UserData;
import de.hpi3d.gamepgrog.trap.future.ArrayExt;
import de.hpi3d.gamepgrog.trap.future.EmptyPromise;
import de.hpi3d.gamepgrog.trap.future.Promise;

/**
 * Executes a {@link Task}.<br>
 * Registered in {@link TaskResolverManager}.<br>
 * Depending of the type of data stealer use {@link SyncTaskResolver} or {@link AsyncTaskResolver}<br>
 * Will first show the permission explanation to the user, than the real permission request.
 * After that the data is fetched and send to the server and isTaskFinished is send.
 * In the end a result message is shown
 */
public abstract class TaskResolver<T extends UserData> {

    protected static final int SUCCESS = 1;
    protected static final int ANOTHER_TASK_EXECUTING = 2;
    protected static final int PERMISSION_FAILED = 3;
    protected static final int UPLOAD_FAILED = 4;
    protected static final int TASK_FAILED = 5;
    protected static final int UPLOAD_FAILED_SAFETY = 6;

    protected static final int NO_MESSAGE = -1;

    protected static boolean inExecution = false;

    protected abstract String getDatatypeName();
    protected abstract String getTaskName();
    protected abstract String[] getPermissionsNeeded();
    protected abstract Promise<List<T>> fetchData(Activity app);

    /**
     * Shows result message as {@link Toast}
     */
    protected void showResultMessage(Activity app, Task task, int result) {
        int message = getResultMessage(task, result);
        if (message != NO_MESSAGE)
            Toast.makeText(app, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Matches result message to result code
     */
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
            case UPLOAD_FAILED_SAFETY:
                return R.string.task_upload_failed_safety;
            default:
                throw new IllegalStateException("");
        }
    }

    /**
     * Executes the task and show the result message
     */
    public EmptyPromise executeAndShowResult(Activity app, Task task) {
        EmptyPromise p = EmptyPromise.create();

        execute(app, task).then((result) -> {
            isTaskFinished(app, task).then((finished) -> {
                int message = result;
                if (finished) {
                    message = SUCCESS;
                    task.setFinished(true);
                } else if (message == SUCCESS) {
                    message = TASK_FAILED;
                }

                showResultMessage(app, task, message);
                p.resolve();
            });
        });

        return p;
    }

    /**
     * executes the task.
     * @return a promise with result code
     */
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
                showPermissionExplanation(app, task).then((success) -> {
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
            sendData(app, data).then((code) -> {
                if (code == ApiCall.ERROR_SAFETY_MODE) {
                    p.resolve(UPLOAD_FAILED_SAFETY);
                } else if (code != ApiCall.SUCCESS) {
                    p.resolve(UPLOAD_FAILED);
                } else {
                    p.resolve(SUCCESS);
                }
                inExecution = false;
            });
        }).error(code -> {
            p.resolve(code);
            inExecution = false;
        });
    }

    /**
     * Sends fetched data to server
     * @return http code
     */
    protected Promise<Integer> sendData(Activity app, List<T> data) {
        Promise<Integer> p = Promise.create();
        ApiManager.api(app).addData(
                StorageManager.with(app).userid.get(),
                getDatatypeName(),
                ArrayExt.map(data, d -> (UserData) d)
        ).call((result, code) -> p.resolve(code));
        return p;
    }

    /**
     * Sends isTaskFinished to api
     * @return if task is finished
     */
    protected Promise<Boolean> isTaskFinished(Activity app, Task task) {
        Promise<Boolean> p = Promise.create();

        ApiManager.api(app).isTaskFinished(
                StorageManager.with(app).userid.get(),
                task.getName()
        ).call((result, code) -> p.resolve(code == ApiCall.SUCCESS && result));
        return p;
    }

    /**
     * Shows permission explanation as Dialog
     */
    protected Promise<Boolean> showPermissionExplanation(Activity app, Task task) {
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
