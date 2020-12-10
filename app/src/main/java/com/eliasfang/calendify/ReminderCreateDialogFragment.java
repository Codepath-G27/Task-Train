package com.eliasfang.calendify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eliasfang.calendify.Database.DatabaseClass;
import com.eliasfang.calendify.Database.ReminderEntity;
import com.eliasfang.calendify.alarmSetup.Alarm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class ReminderCreateDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText edittext_message;
    private Button btn_set_date;
    private Button btn_set_time;
    private CheckBox cbRecur;
    DatabaseClass dataBase;
    TextView tvSave;
    String notificationTime;

    public ReminderCreateDialogFragment() {
        // Required empty constructor
    }

    // Required newInstance constructor to be called from ToDoFragment
    public static ReminderCreateDialogFragment newInstance() {
        return new ReminderCreateDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set to style defined in styles.xml
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_create_reminder, container, false);
        ImageButton imgBtnClose = view.findViewById(R.id.imgBtnClose);
        tvSave = view.findViewById(R.id.tvSave);
        edittext_message = view.findViewById(R.id.et_note);
        btn_set_date = view.findViewById(R.id.btn_set_date);
        btn_set_time = view.findViewById(R.id.btn_set_time);
        cbRecur = view.findViewById(R.id.cbRecur);
        dataBase = DatabaseClass.getDatabase(getActivity().getApplicationContext());

        // Show soft keyboard automatically and set focus on event name
        edittext_message.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Check for clicks on top buttons
        imgBtnClose.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        btn_set_date.setOnClickListener(this);
        btn_set_time.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Manage which button was clicked on event creation dialog
        switch (id) {
            case R.id.btn_set_date:
                setDate();
                break;
            case R.id.btn_set_time:
                setTime();
                break;
            case R.id.imgBtnClose:
                dismiss();
                Toast.makeText(getContext(), "Cancel reminder creation", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvSave:
                submit();
                dismiss();
                Toast.makeText(getContext(), "Reminder saved", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                btn_set_date.setText(day + "-" + (month + 1) + "-" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                notificationTime = i + ":" + i1;
                btn_set_time.setText(FormatTime(i, i1));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    public String FormatTime(int hour, int minute) {

        String time;
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

    // Helper functions to save the fields that are filled in
    private void submit() {
        String text = edittext_message.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Please Enter or record the text", Toast.LENGTH_SHORT).show();
        } else {
            if (btn_set_time.getText().toString().equals("Select Time") || btn_set_date.getText().toString().equals("Select date")) {
                Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
            } else {
                ReminderEntity reminderEntity = new ReminderEntity();
                String value = (edittext_message.getText().toString().trim());
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

    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getActivity().getApplicationContext(), Alarm.class);
        intent.putExtra("event", text);
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String TimeandDate = date + " " + notificationTime;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(TimeandDate);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}