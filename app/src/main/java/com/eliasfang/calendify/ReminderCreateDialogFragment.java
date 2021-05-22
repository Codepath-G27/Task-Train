package com.eliasfang.calendify;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eliasfang.calendify.Database.DatabaseClass;
import com.eliasfang.calendify.Database.ReminderEntity;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.alarmSetup.Alarm;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReminderCreateDialogFragment extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "ReminderCreateDialog";
    Button btn_set_time;
    Button btn_set_date;
    TextView btn_submit;
    EditText editext_message;
    String notificationTime;
    DatabaseClass dataBase;
    ImageButton imgBtnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_create_reminder);
        imgBtnClose = findViewById(R.id.imgBtnClose);
        btn_submit = findViewById(R.id.tvSave);
        btn_set_date = findViewById(R.id.btn_set_date);
        editext_message = findViewById(R.id.et_note);
        btn_set_time = findViewById(R.id.btn_set_time);
        btn_set_date.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_set_time.setOnClickListener(this);
        imgBtnClose.setOnClickListener(this);
        dataBase = DatabaseClass.getDatabase(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        if (view == btn_set_date) {
            setDate();
        } else if (view == btn_set_time) {
            setTime();
        } else if (view == imgBtnClose) {
            StyleableToast.makeText(this, "Task Creation Cancelled", R.style.toastDeleted).show();
            finish();
        } else {
            submit();
        }
    }


    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                notificationTime = i + ":" + i1;
                btn_set_time.setText(FormatTime(i, i1));
            }
        }, hour, minute, false);
        timePickerDialog.show();

    }

    private void submit() {
        String text = editext_message.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "Please Enter a Title", Toast.LENGTH_SHORT).show();
        }
        else {
            if (btn_set_time.getText().toString().trim().equals("Add time") || btn_set_date.getText().toString().trim().equals("Add date")) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "The time is " + btn_set_time.getText().toString().trim() + " The date is " + btn_set_date.getText().toString().trim());
                ReminderEntity reminderEntity = new ReminderEntity();
                String value = (editext_message.getText().toString().trim());
                String date = (btn_set_date.getText().toString().trim());
                String time = (btn_set_time.getText().toString().trim());
                reminderEntity.setEventdate(date);
                reminderEntity.setEventname(value);
                reminderEntity.setEventtime(time);
                dataBase.EventDao().insertAll(reminderEntity);
                setAlarm(value, date, time);
            }
        }
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                btn_set_date.setText(day + "-" + (month + 1) + "-" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public String FormatTime(int hour, int minute) {

        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }
        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }
        return time;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                editext_message.setText(text.get(0));
            }
        }

    }

    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtra("event", text);
        intent.putExtra("date", date);
        intent.putExtra("time", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String TimeandDate = date + " " + notificationTime;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(TimeandDate);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        finish();

    }
}