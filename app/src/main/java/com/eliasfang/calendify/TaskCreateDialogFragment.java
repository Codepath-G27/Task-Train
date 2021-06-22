package com.eliasfang.calendify;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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

import com.eliasfang.calendify.Database.DatabaseClass;
import com.eliasfang.calendify.Database.ReminderEntity;
import com.eliasfang.calendify.alarmSetup.Alarm;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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


    private Boolean checked = false;

    DatabaseClass dataBase;


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
        dataBase = DatabaseClass.getDatabase(getContext());
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
                            Task task = saveData();
                            Intent replyIntent = new Intent();
                            replyIntent.putExtra(EXTRA_REPLY, task);
                            TaskViewModel myTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
                            myTaskViewModel.insert(task);
                            task.setHasAlarm(true);
                            cbAlarm.setChecked(true);
                            //add alarm in a very brute force way
                            ReminderEntity reminderEntity = new ReminderEntity();
                            String value = etTitle.getText().toString();
                            String date = (btnDate.getText().toString().trim());
                            String time = (btnTime.getText().toString().trim());
                            reminderEntity.setEventdate(date);
                            reminderEntity.setEventname(value);
                            reminderEntity.setEventtime(time);
                            dataBase.EventDao().insertAll(reminderEntity);
                            setAlarm(value, date, time);
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
                btnDate.setText(day + "-" + (month + 1) + "-" + year);
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

    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getContext(), Alarm.class);
        intent.putExtra("event", text);
        intent.putExtra("date", date);
        intent.putExtra("time", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
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