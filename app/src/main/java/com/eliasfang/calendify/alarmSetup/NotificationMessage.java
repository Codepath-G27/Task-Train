package com.eliasfang.calendify.alarmSetup;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eliasfang.calendify.R;

public class NotificationMessage extends AppCompatActivity {
    TextView textView;
    TextView timeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);
        textView = findViewById(R.id.tv_message);
        timeMessage = findViewById(R.id.time_message);
        Bundle bundle = getIntent().getExtras();
        timeMessage.setText(bundle.getString("date"));
        textView.setText(bundle.getString("message"));
    }
}
