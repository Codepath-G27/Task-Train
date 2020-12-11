package com.eliasfang.calendify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class TaskCreateDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String EXTRA_REPLY =
            "com.eliasfang.calendify.TASK";

    private EditText etTitle;
    private EditText etLocation;
    private Button btnDate;
    private Button btnTime;
    private CheckBox cbRecur;
    private EditText etDescription;
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
        cbRecur = view.findViewById(R.id.cbRecur);
        etDescription = view.findViewById(R.id.etDescription);



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
                dismiss();
                Toast.makeText(getContext(), "Cancelled task creation", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvSave:
                Task task = saveData();
                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, task);
                TaskViewModel myTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
                myTaskViewModel.insert(task);

                //add alarm in a very brute force way
                ReminderEntity reminderEntity = new ReminderEntity();
                String value = etTitle.getText().toString();
                String date = (btnDate.getText().toString().trim());
                String time = (btnTime.getText().toString().trim());
                reminderEntity.setEventdate(date);
                reminderEntity.setEventname(value);
                reminderEntity.setEventtime(time);
                dataBase.EventDao().insertAll(reminderEntity);
                dismiss();

                Toast.makeText(getContext(), "Task saved", Toast.LENGTH_SHORT).show();
                break;
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
        boolean recur = cbRecur.isChecked();
        String description = etDescription.getText().toString();

        Task toReturn = new Task(title, description, (long) 0.0, false, false, 0);
        return toReturn;
    }

}