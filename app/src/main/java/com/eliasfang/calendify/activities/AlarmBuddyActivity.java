package com.eliasfang.calendify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eliasfang.calendify.MainActivity;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.alarmSetup.AlarmReceiver;
import com.eliasfang.calendify.data.TaskViewModel;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static com.eliasfang.calendify.fragments.TaskCreateDialogFragment.TAG;

public class AlarmBuddyActivity extends AppCompatActivity {

    private static final String TAG = "AlarmBuddyActivity";

    private TextView btn_accept;

    TaskViewModel myTaskViewModel;

    FirebaseAuth auth;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_buddy);

        btn_accept = findViewById(R.id.btn_accept);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();


        myTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        Bundle bundle = getIntent().getExtras();
        String alarm_id = bundle.getString("alarm_id");
        String senderUid = bundle.getString("senderUid");

        Log.i(TAG, "The UID: " + senderUid + " and the alarmID: " + alarm_id);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference docRef = database.collection("users").document(senderUid);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                User user = document.toObject(User.class);
                                Log.i(TAG, user.toString());
                                Map<String, Map<String, String>> tasks = user.getTasks();
                                Map<String, String> data = tasks.get(alarm_id);

                                //Handle if the alarm is deleted before the user accepts or declines
                                if (data != null) {
                                    data.get("name");
                                    data.get("description");
                                    String eventDate = data.get("eventDate");
                                    data.get("eventTime");
                                    data.get("category");
                                    data.get("location");




                                    com.eliasfang.calendify.models.Task reminderEntity = new com.eliasfang.calendify.models.Task(data.get("name"), data.get("description"), data.get("eventDate"), data.get("eventTime"), false, true, data.get("category"), data.get("location"), false);
                                    reminderEntity.setHasAlarm(true);
                                    reminderEntity.setEventDate(data.get("eventDate"));
                                    reminderEntity.setName(data.get("name"));
                                    reminderEntity.setEventTime(data.get("eventTime"));
                                    reminderEntity.setTimezone(data.get("timezone"));

                                    reminderEntity.setAlarmId(Integer.parseInt(alarm_id));
                                    reminderEntity.setRecurrence(Boolean.parseBoolean(data.get("recurring")));
                                    reminderEntity.setNotificationTime(data.get("notificationTime"));
                                    reminderEntity.setDays(Boolean.parseBoolean(data.get("monday")), Boolean.parseBoolean(data.get("tuesday")), Boolean.parseBoolean(data.get("wednesday")), Boolean.parseBoolean(data.get("thursday")), Boolean.parseBoolean(data.get("friday")), Boolean.parseBoolean(data.get("saturday")), Boolean.parseBoolean(data.get("sunday")));

                                    reminderEntity.setAlarm(AlarmBuddyActivity.this);

                                    myTaskViewModel.insert(reminderEntity);
                                    finish();
                                } else {
                                    Toast.makeText(AlarmBuddyActivity.this, getResources().getString(R.string.alarm_deleted), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            }
        });
    }


}