package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.future.Promise;

/**
 * When clicking on the contact button the first time the app will collect contacts and send them
 * to the server. This works like a Task, but is hardcoded.
 * Since the Task is not from the server isTaskFinished does not have to be called
 */
public class FakeContactsTaskResolver extends SyncTaskResolver<Contact> {

    public FakeContactsTaskResolver(String[] permissionsNeeded) {
        super("contact", permissionsNeeded, DataStealer::takeContactData);
    }

    /**
     * Custom error messages
     */
    @Override
    protected int getResultMessage(Task task, int result) {
        switch (result) {
            case PERMISSION_FAILED:
                return R.string.fake_contact_permission_failed;
            case UPLOAD_FAILED:
                return R.string.fake_contact_upload_failed;
            case UPLOAD_FAILED_SAFETY:
                return R.string.task_upload_failed_safety;
            default:
                return NO_MESSAGE;
        }
    }

    /**
     * Skip isTaskFinished call
     */
    @Override
    protected Promise<Boolean> isTaskFinished(Activity app, Task task) {
        return Promise.createResolved(true);
    }
}
