package com.eliasfang.calendify.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.eliasfang.calendify.R;
import com.eliasfang.calendify.alarmSetup.Alarm;
import com.eliasfang.calendify.data.Task;
import com.eliasfang.calendify.data.TaskViewModel;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TaskCreateDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String EXTRA_REPLY = "com.eliasfang.calendify.TASK";
    public static final String TAG = "TaskCreateDialog";

    private EditText etTitle;
    private EditText etLocation;
    private Button btnDate;
    private Button btnTime;
    private CheckBox cbRecur;
    private EditText etDescription;
    private Spinner spCategory;
    private CheckBox cbAlarm;
    String notificationTime;

    private Integer alarm_month, alarm_year, alarm_day, alarm_hour, alarm_minute;


    private Boolean checked = false;



    public TaskCreateDialogFragment() {
        // Required empty constructor
    }

    // Required newInstance constructor to be called from ToDoFragment
    public static TaskCreateDialogFragment newInstance() {
        return new TaskCreateDialogFragment();
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
        View view = inflater.inflate(R.layout.dialog_fragment_create_task, container, false);
        ImageButton imgBtnClose = view.findViewById(R.id.imgBtnClose);
        TextView tvSave = view.findViewById(R.id.tvSave);
        etTitle = view.findViewById(R.id.etTitle);
        etLocation = view.findViewById(R.id.etLocation);
        btnDate = view.findViewById(R.id.btnDate);
        btnTime = view.findViewById(R.id.btnTime);
        cbRecur = view.findViewById(R.id.cbAlarm);
        etDescription = view.findViewById(R.id.etDescription);
        cbAlarm = view.findViewById(R.id.cbAlarm);
        spCategory = view.findViewById(R.id.etCategory);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category_items));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(myAdapter);


        // Show soft keyboard automatically and set focus on event name
        etTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Check for clicks on buttons
        imgBtnClose.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Manage which button was clicked on event creation dialog
        switch (id) {
            case R.id.btnDate:
                saveDate();
                break;
            case R.id.btnTime:
                saveTime();
                break;
            case R.id.imgBtnClose:
                StyleableToast.makeText(getContext(), "Task Creation Cancelled", R.style.toastDeleted).show();
                dismiss();
                break;
            case R.id.tvSave:
                if (!etTitle.getText().toString().isEmpty()) {
                        if (cbAlarm.isChecked() && (btnDate.getText().toString().equals("Add date") || btnTime.getText().toString().equals("Add time"))) {
                            Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                        } else if (!cbAlarm.isChecked()) {
                            Task task = saveData();
                            Intent replyIntent = new Intent();
                            replyIntent.putExtra(EXTRA_REPLY, task);
                            TaskViewModel myTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
                            myTaskViewModel.insert(task);
                            dismiss();
                            StyleableToast.makeText(getContext(), "Task Saved", R.style.toastSaved).show();
                        } else {
                            Task reminderEntity = saveData();
                            Intent replyIntent = new Intent();
                            replyIntent.putExtra(EXTRA_REPLY, reminderEntity);
                            TaskViewModel myTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
                            reminderEntity.setHasAlarm(true);
                            cbAlarm.setChecked(true);
                            //add alarm in a very brute force way

                            String value = etTitle.getText().toString();
                            String date = (btnDate.getText().toString().trim());
                            String time = (btnTime.getText().toString().trim());
                            reminderEntity.setEventDate(date);
                            reminderEntity.setName(value);
                            reminderEntity.setEventTime(time);

                            int rand_alarmId = new Random().nextInt(Integer.MAX_VALUE);

                            reminderEntity.setHour(alarm_hour);
                            reminderEntity.setMinute(alarm_minute);
                            reminderEntity.setDay(alarm_day);
                            reminderEntity.setYear(alarm_year);
                            reminderEntity.setMonth(alarm_month);
                            reminderEntity.setAlarmId(rand_alarmId);


                            myTaskViewModel.insert(reminderEntity);



                            setAlarm(value, date, time, rand_alarmId);
                            dismiss();
                            Log.i(TAG, "The date is " + btnDate.getText().toString().trim() + " The time is " + btnTime.getText().toString().trim());
                            StyleableToast.makeText(getContext(), "Task Saved with Alarm", R.style.toastSaved).show();
                        }


                } else {
                    Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void saveDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                alarm_year = year;alarm_month = month; alarm_day = day;
                btnDate.setText((month + 1) + "-" + day + "-" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                alarm_hour = i; alarm_minute = i1;
                notificationTime = i + ":" + i1;
                btnTime.setText(FormatTime(i, i1));
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
    private Task saveData() {
        //todo add some error handling to make sure that we have a valid task
        String title = etTitle.getText().toString();
        String location = etLocation.getText().toString();
        String date = btnDate.getText().toString();
        String time = btnTime.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        Log.i(TAG, category);

        boolean recur = cbRecur.isChecked();
        String description = etDescription.getText().toString();

        //TODO set the category picker from the spinner

        Task toReturn = new Task(title, description, date, time, (long) 0.0, false, recur, 0, category, location, false);
        return toReturn;
    }

    private void setAlarm(String text, String date, String time, Integer id) {
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getContext(), Alarm.class);
        intent.putExtra("event", text);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("id", id);
        Log.i("Id", id + "");


        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, 0);
        String TimeandDate = date + " " + notificationTime;
        DateFormat formatter = new SimpleDateFormat("M-d-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(TimeandDate);
            am.setExact(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


}