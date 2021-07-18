package com.eliasfang.calendify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.activities.AlarmBuddyActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.app.PendingIntent.FLAG_ONE_SHOT;


public class FirebasePushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "Firebase Service";
    private final String CHANNEL_ID = "MY_CHANNEL";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        String toastText = String.format("Alarm Received");
        Log.i(TAG, "Recieved");


        Map<String, String> data = remoteMessage.getData();

        String title = data.get("title");
        String message = data.get("message");
        String eventTime = data.get("evenTime");

        Intent intent = new Intent(this, AlarmBuddyActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(Integer.MAX_VALUE);

        createNotificationChannel(notificationManager);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_baseline_alarm_on_24_black)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(notificationID, notification);

    }

    private void createNotificationChannel(NotificationManager notificationManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Alarm Buddies";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH);
            channel.setDescription("Alarm Friends Channel");
            channel.enableLights(true);
            channel.setLightColor(R.color.purple);

            notificationManager.createNotificationChannel(channel);
        }

    }

}
