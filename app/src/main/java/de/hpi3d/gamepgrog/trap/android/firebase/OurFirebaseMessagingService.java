package de.hpi3d.gamepgrog.trap.android.firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.hpi3d.gamepgrog.trap.android.NotificationHelper;
import de.hpi3d.gamepgrog.trap.api.ApiIntent;
import de.hpi3d.gamepgrog.trap.api.ApiService;
import de.hpi3d.gamepgrog.trap.api.StorageManager;
import de.hpi3d.gamepgrog.trap.datatypes.Clue;
import de.hpi3d.gamepgrog.trap.tasks.Task;


public class OurFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "OurFBMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();

        Log.d(TAG, "Firebase Message Received: " + data.toString());

        if (FirebaseDataParser.isValid(data)) {
            switch (FirebaseDataParser.getCall(data)) {
                case FirebaseDataParser.CALL_EXECUTE_TASK:
                    onTasksReceived(FirebaseDataParser.parseTasks(data));
                    return;
                case FirebaseDataParser.CALL_NEW_CLUE:
                    onClueReceived(FirebaseDataParser.parseClue(data));
                    return;
            }
        }

        // If this line is reached something went wrong
        throw new IllegalArgumentException("Firebase message is malformed: " + data.toString());
    }

    private void onTasksReceived(ArrayList<Task> tasks) {
        StorageManager.setTasks(getApplication(), tasks);

        int amount = tasks.size();
        String title = String.format("Andy Abbot has %s new Task%s for you",
                amount, amount == 1 ? "" : "s");
        String message = String.format("Open the App to see %s",
                amount == 1 ? "it" : "them");

        NotificationHelper.sendNotification(getApplicationContext(),
                title, message);
    }

    private void onClueReceived(Clue clue) {
        ArrayList<Clue> list = new ArrayList<>(Arrays.asList(clue));
        StorageManager.addClues(getApplication(), list);
        NotificationHelper.sendNotification(getApplicationContext(),
                "You received a new Clue",
                "Open the App to view it");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        setNewToken(getApplicationContext(), s);
    }

    public static void setNewToken(Context c, @NonNull String token) {
        Log.d(TAG, "New Firebase token: " + token);
        if (StorageManager.hasRegisteredUser(c)) {
            int userid = StorageManager.getUserId(c);
            sendNewToken(c, userid, token);
        }
        StorageManager.setPlayerFBToken(c, token);
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

    public static void init(Context context) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Log.w("Firebase", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    Log.d("Firebase", "send new token: " + token);
                    setNewToken(context, token);
                });
    }

}
