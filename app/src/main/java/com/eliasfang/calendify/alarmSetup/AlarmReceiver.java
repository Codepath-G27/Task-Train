package com.eliasfang.calendify.alarmSetup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.eliasfang.calendify.models.User;
import com.eliasfang.calendify.service.AlarmService;
import com.eliasfang.calendify.service.RestartAlarmService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    private FirebaseAuth auth;
    private FirebaseFirestore database;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Alarm Received");

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = String.format("Alarm Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        }
        else {
            if (!intent.getBooleanExtra("RECURRING", false)) {
                String toastText = String.format("Alarm Received");
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                //removeAlarmFromFirebase(intent);
                startAlarmService(context, intent);
            } else {
                if (alarmIsToday(intent)) {
                    String toastText = String.format("Alarm Received");
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                    startAlarmService(context, intent);
                }
            }
        }


    }

    private void removeAlarmFromFirebase(Intent intent) {

        Bundle bundle = intent.getExtras();
        String alarmId = String.valueOf(bundle.getInt("id"));

        if(auth.getCurrentUser() != null && bundle.getBoolean("alarmBuddy")){

            database.collection("users").document(auth.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            Map<String, Map<String,String>> tasks = user.getTasks();
                            if(tasks.containsKey(alarmId)) {
                                tasks.remove(alarmId);
                                database.collection("users").document(auth.getUid()).update("tasks", tasks)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i(TAG, "Tasks updated");
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RestartAlarmService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        Bundle bundle = intent.getExtras();
        String text = bundle.getString("event");


        intentService.putExtra("event", text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private boolean alarmIsToday(Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch (today) {
            case Calendar.MONDAY:
                if (intent.getBooleanExtra("MONDAY", false))
                    return true;
                return false;
            case Calendar.TUESDAY:
                if (intent.getBooleanExtra("TUESDAY", false))
                    return true;
                return false;
            case Calendar.WEDNESDAY:
                if (intent.getBooleanExtra("WEDNESDAY", false))
                    return true;
                return false;
            case Calendar.THURSDAY:
                if (intent.getBooleanExtra("THURSDAY", false))
                    return true;
                return false;
            case Calendar.FRIDAY:
                if (intent.getBooleanExtra("FRIDAY", false))
                    return true;
                return false;
            case Calendar.SATURDAY:
                if (intent.getBooleanExtra("SATURDAY", false))
                    return true;
                return false;
            case Calendar.SUNDAY:
                if (intent.getBooleanExtra("SUNDAY", false))
                    return true;
                return false;
        }
        return false;
    }


//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Bundle bundle = intent.getExtras();
//        String text = bundle.getString("event");
//        String date = bundle.getString("date");
//        String time = bundle.getString("time");
//        Integer id = bundle.getInt("id");
//        Log.i(TAG, time);
//        Log.i(TAG, id + "");
//
//        //Click on Notification
//
//        Intent intent1 = new Intent(context, NotificationMessage.class);
//        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent1.putExtra("time", time);
//        intent1.putExtra("message", text);
//        //Notification Builder
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent1, 0);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notify_001");
//
//        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
//        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
//
//        //PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//        contentView.setTextViewText(R.id.message, text);
//        contentView.setTextViewText(R.id.time_message, time);
//        mBuilder.setSmallIcon(R.drawable.ic_alarm_white_24dp);
//        mBuilder.setAutoCancel(true);
//        mBuilder.setOngoing(true);
//        mBuilder.setPriority(Notification.PRIORITY_HIGH);
//        mBuilder.setOnlyAlertOnce(true);
//        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
//        mBuilder.setContent(contentView);
//        mBuilder.setContentIntent(pendingIntent);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "channel_id";
//            NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableVibration(true);
//            notificationManager.createNotificationChannel(channel);
//            mBuilder.setChannelId(channelId);
//        }
//
//        Notification notification = mBuilder.build();
//        notificationManager.notify(1, notification);
//
//
//
//
//    }
}
