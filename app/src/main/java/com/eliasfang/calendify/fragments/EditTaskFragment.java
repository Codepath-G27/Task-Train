package com.eliasfang.calendify.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.eliasfang.calendify.data.TaskViewModel;
import com.eliasfang.calendify.models.Task;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;


public class EditTaskFragment extends Fragment {

    private static final String TAG = "EditTaskFragment";

    private TaskViewModel myViewModel;

    private EditText etTitle;
    private EditText etLocation;
    private Button btnDate;
    private Button btnTime;
    private CheckBox cbAlarm;
    private EditText etDescription;
    private Spinner spCategory;
    private String notificationTime;

    private TextView tv_alarmFriends;


    private ConstraintLayout cl_addFriend;


    private CheckBox cb_mon, cb_tues, cb_wed, cb_thur, cb_fri, cb_sat, cb_sun;

    private TextView tvSave;

    private Boolean recur = false;

    public EditTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        int id = getArguments().getInt("id");

        Task currUpdate = myViewModel.getTask(id);

        notificationTime = currUpdate.getNotificationTime();

        ImageButton imgBtnClose = view.findViewById(R.id.imgBtnClose);
        tvSave = view.findViewById(R.id.btn_accept);
        etTitle = view.findViewById(R.id.etTitle);
        etLocation = view.findViewById(R.id.etLocation);
        btnDate = view.findViewById(R.id.btnDate);
        btnTime = view.findViewById(R.id.btnTime);

        etDescription = view.findViewById(R.id.etDescription);
        cbAlarm = view.findViewById(R.id.cbAlarm);
        spCategory = view.findViewById(R.id.etCategory);


        tv_alarmFriends = view.findViewById(R.id.tv_alarmFriends);

        cl_addFriend = view.findViewById(R.id.cl_addFriend);

        cb_mon = view.findViewById(R.id.cb_mon);
        cb_mon.setChecked(currUpdate.getMonday());

        cb_tues = view.findViewById(R.id.cb_tues);
        cb_tues.setChecked(currUpdate.getTuesday());

        cb_wed = view.findViewById(R.id.cb_wed);
        cb_wed.setChecked(currUpdate.getWed());

        cb_thur = view.findViewById(R.id.cb_thur);
        cb_thur.setChecked(currUpdate.getThur());

        cb_fri = view.findViewById(R.id.cb_fri);
        cb_fri.setChecked(currUpdate.getFri());

        cb_sat = view.findViewById(R.id.cb_sat);
        cb_sat.setChecked(currUpdate.getSat());

        cb_sun = view.findViewById(R.id.cb_sun);
        cb_sun.setChecked(currUpdate.getSun());

        btnDate.setText(currUpdate.getEventDate());
        btnTime.setText(currUpdate.getEventTime());

        etTitle.setText(currUpdate.getName());
        etLocation.setText(currUpdate.getLocation());
        etDescription.setText(currUpdate.getDescription());

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category_items));
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.category_items)));


        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(myAdapter);
        spCategory.setSelection(stringList.indexOf(currUpdate.getCategory()));

        cbAlarm.setChecked(currUpdate.isHasAlarm());

        if(!currUpdate.getAlarmBuddies().isEmpty())
            tv_alarmFriends.setText(currUpdate.getAlarmBuddies());

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTime();
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDate();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etTitle.getText().toString().isEmpty()) {
                    if (cbAlarm.isChecked() && (btnDate.getText().toString().equals(getResources().getString(R.string.add_date)) || btnTime.getText().toString().equals(getResources().getString(R.string.add_time)))) {
                        Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    } else if (!cbAlarm.isChecked()) {
                        Task task = saveData();

                        task.setId(currUpdate.getId());
                        myViewModel.update(task);
                        if (currUpdate.isHasAlarm()) {
                            currUpdate.cancelAlarm(getContext());
                        }
                        dismiss();
                        StyleableToast.makeText(getContext(), "Task Saved", R.style.toastSaved).show();
                    } else {
                        Task reminderEntity = saveData();

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


                        String time_zone_default = TimeZone.getDefault().getID();


                        reminderEntity.setTimezone(time_zone_default);
                        reminderEntity.setAlarmId(rand_alarmId);
                        reminderEntity.setRecurrence(recur);
                        reminderEntity.setNotificationTime(notificationTime);
                        reminderEntity.setDays(cb_mon.isChecked(), cb_tues.isChecked(), cb_wed.isChecked(), cb_thur.isChecked(), cb_fri.isChecked(), cb_sat.isChecked(), cb_sun.isChecked());

                        reminderEntity.setId(currUpdate.getId());
                        myViewModel.update(reminderEntity);

                        if (currUpdate.isHasAlarm()) {
                            currUpdate.cancelAlarm(getContext());
                        }

                        reminderEntity.setAlarm(getContext());
                        dismiss();
                        Log.i(TAG, "The date is " + btnDate.getText().toString().trim() + " The time is " + btnTime.getText().toString().trim());
                        StyleableToast.makeText(getContext(), "Task Saved with Alarm", R.style.toastSaved).show();
                    }


                } else {
                    Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void saveDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
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

    private void dismiss() {
        //Close the keyboard after canceling
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_edit_task, container, false);

    }

    private Task saveData() {

        recur = false;
        //todo add some error handling to make sure that we have a valid task
        String title = etTitle.getText().toString();
        String location = etLocation.getText().toString();
        String date = btnDate.getText().toString();
        String time = btnTime.getText().toString();
        String category = spCategory.getSelectedItem().toString();

        Boolean alarm_set = cbAlarm.isChecked();
        recur = cb_mon.isChecked() || cb_tues.isChecked() || cb_wed.isChecked() || cb_thur.isChecked() || cb_fri.isChecked() || cb_sat.isChecked() || cb_sun.isChecked();
        String description = etDescription.getText().toString();

        //TODO set the category picker from the spinner

        Task toReturn = new Task(title, description, date, time, false, alarm_set, category, location, false);
        return toReturn;
    }
}