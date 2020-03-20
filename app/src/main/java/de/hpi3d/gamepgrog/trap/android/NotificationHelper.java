package de.hpi3d.gamepgrog.trap.android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import de.hpi3d.gamepgrog.trap.R;
import de.hpi3d.gamepgrog.trap.ui.MainActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "de.hpi3d.gamepgrog.trap.fcmnotification";

    public static void sendNotification(Context c, String messageTitle, String messageBody) {
        Intent intent = new Intent(c, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(c, CHANNEL_ID)
                        .setContentTitle(messageTitle)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Detective Game FCM Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
