package com.eliasfang.calendify.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

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


import com.eliasfang.calendify.API.NotificationApi;
import com.eliasfang.calendify.Adapter.RequestsAdapter;
import com.eliasfang.calendify.MainActivity;
import com.eliasfang.calendify.activities.AlarmBuddyActivity;
import com.eliasfang.calendify.activities.PreferencesActivity;
import com.eliasfang.calendify.dialogs.SocialBottomSheetDialog;
import com.eliasfang.calendify.dialogs.TaskCreateBottomSheetDialog;
import com.eliasfang.calendify.models.NotificationData;
import com.eliasfang.calendify.models.PushNotification;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.API.RetrofitInit;
import com.eliasfang.calendify.alarmSetup.AlarmReceiver;
import com.eliasfang.calendify.models.Task;
import com.eliasfang.calendify.data.TaskViewModel;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class TaskCreateDialogFragment extends DialogFragment implements View.OnClickListener, TaskCreateBottomSheetDialog.BottomSheetListener {
    public static final String TAG = "TaskCreateDialog";

    private EditText etTitle;
    private EditText etLocation;
    private Button btnDate;
    private Button btnTime;
    private CheckBox cbAlarm;
    private EditText etDescription;
    private Spinner spCategory;
    String notificationTime;


    List<User> usersSelected = new ArrayList<User>();


    private TextView tv_alarmFriends;

    private TaskViewModel myTaskViewModel;

    private ConstraintLayout cl_addFriend;


    private CheckBox cb_mon, cb_tues, cb_wed, cb_thur, cb_fri, cb_sat, cb_sun;


    private Boolean alarm_set = false;

    private String selectedDate;

    private TextView tvSave;

    private Boolean recur = false;

    private FirebaseFirestore database;
    private FirebaseAuth auth;

    private NotificationApi notificationApi;

    public TaskCreateDialogFragment() {
        // Required empty constructor
    }

    public TaskCreateDialogFragment(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    // Required newInstance constructor to be called from ToDoFragment
    public static TaskCreateDialogFragment newInstance() {
        return new TaskCreateDialogFragment();
    }

    // Required newInstance constructor to be called from ToDoFragment
    public static TaskCreateDialogFragment newInstance(String selectedDate) {
        return new TaskCreateDialogFragment(selectedDate);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrofit
        Retrofit retro = RetrofitInit.getClient();
        notificationApi = retro.create(NotificationApi.class);

        myTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_create_task, container, false);
        ImageButton imgBtnClose = view.findViewById(R.id.imgBtnClose);
        tvSave = view.findViewById(R.id.tvSave);
        etTitle = view.findViewById(R.id.etTitle);
        etLocation = view.findViewById(R.id.etLocation);
        btnDate = view.findViewById(R.id.btnDate);
        btnTime = view.findViewById(R.id.btnTime);


        if(selectedDate!=null){
            btnDate.setText(selectedDate);
        }

        etDescription = view.findViewById(R.id.etDescription);
        cbAlarm = view.findViewById(R.id.cbAlarm);
        spCategory = view.findViewById(R.id.etCategory);




        tv_alarmFriends = view.findViewById(R.id.tv_alarmFriends);

        cl_addFriend = view.findViewById(R.id.cl_addFriend);

        cb_mon = view.findViewById(R.id.cb_mon);
        cb_tues = view.findViewById(R.id.cb_tues);
        cb_wed = view.findViewById(R.id.cb_wed);
        cb_thur = view.findViewById(R.id.cb_thur);
        cb_fri = view.findViewById(R.id.cb_fri);
        cb_sat = view.findViewById(R.id.cb_sat);
        cb_sun = view.findViewById(R.id.cb_sun);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category_items));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(myAdapter);


        // Show soft keyboard automatically and set focus on event name
//        etTitle.requestFocus();
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Check for clicks on buttons
        imgBtnClose.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);

        cl_addFriend.setOnClickListener(this);

        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


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
            case R.id.cl_addFriend:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    TaskCreateBottomSheetDialog sheetDialog = new TaskCreateBottomSheetDialog();
                    sheetDialog.show(getChildFragmentManager(), "Task Create Bottom Sheet");
                } else {
                    Toast.makeText(getContext(), "Please login to add alarm buddies", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgBtnClose:
                StyleableToast.makeText(getContext(), "Task Creation Cancelled", R.style.toastDeleted).show();
                dismiss();
                break;
            case R.id.tvSave:
                if (!etTitle.getText().toString().isEmpty()) {
                    if (cbAlarm.isChecked() && (btnDate.getText().toString().equals(getResources().getString(R.string.add_date)) || btnTime.getText().toString().equals(getResources().getString(R.string.add_time)))) {
                        Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    } else if (!cbAlarm.isChecked()) {
                        Task task = saveData();

                        myTaskViewModel.insert(task);
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

                        if (!tv_alarmFriends.getText().equals("Add Alarm Buddies") && !usersSelected.isEmpty()) {
                            DocumentReference doc = database.collection("users").document(auth.getUid());

                            HashMap<String, String> data = new HashMap<String, String>();

                            data.put("rand_alarmId", String.valueOf(rand_alarmId));
                            data.put("eventDate", date);
                            data.put("eventTime", time);
                            data.put("name", value);
                            data.put("timezone", time_zone_default);
                            data.put("recurring", String.valueOf(recur));
                            data.put("notificationTime", notificationTime);
                            data.put("monday", String.valueOf(cb_mon.isChecked()));
                            data.put("tuesday", String.valueOf(cb_tues.isChecked()));
                            data.put("wednesday", String.valueOf(cb_wed.isChecked()));
                            data.put("thursday", String.valueOf(cb_thur.isChecked()));
                            data.put("friday", String.valueOf(cb_fri.isChecked()));
                            data.put("saturday", String.valueOf(cb_sat.isChecked()));
                            data.put("sunday", String.valueOf(cb_sun.isChecked()));
                            data.put("description", etDescription.getText().toString());
                            data.put("category", spCategory.getSelectedItem().toString());
                            data.put("location", etLocation.getText().toString());


                            reminderEntity.setHasAlarmBuddy(true);
                            reminderEntity.setAlarmBuddies(tv_alarmFriends.getText().toString().trim());


                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.i(TAG, "The size is" + usersSelected.size());
                                    User user = documentSnapshot.toObject(User.class);
                                    Map<String, Map<String, String>> userTasks =  user.getTasks();
                                    userTasks.put(String.valueOf(rand_alarmId), data);
                                    doc.update("tasks", userTasks)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated with new alarm!");
                                                    for (User user : usersSelected) {
                                                        Log.i(TAG, "Token " + user.getFmcToken());
                                                        NotificationData alarmData = new NotificationData("New Alarm Buddy Request", "The user " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + " has sent you a linked alarm!", FirebaseAuth.getInstance().getUid(), String.valueOf(rand_alarmId));
                                                        PushNotification notification = new PushNotification(alarmData, user.getFmcToken());
                                                        createNotification(notification);
                                                    }
                                                    usersSelected.clear();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }
                            });

                        }
                        reminderEntity.setTimezone(time_zone_default);
                        reminderEntity.setAlarmId(rand_alarmId);
                        reminderEntity.setRecurrence(recur);
                        reminderEntity.setNotificationTime(notificationTime);
                        reminderEntity.setDays(cb_mon.isChecked(), cb_tues.isChecked(), cb_wed.isChecked(), cb_thur.isChecked(), cb_fri.isChecked(), cb_sat.isChecked(), cb_sun.isChecked());

                        myTaskViewModel.insert(reminderEntity);

//                        Intent intent = new Intent(getContext(), AlarmBuddyActivity.class);
//                        intent.putExtra("alarm_id", String.valueOf(rand_alarmId));
//                        startActivity(intent);
                        reminderEntity.setAlarm(getContext());
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

    // Helper functions to save the fields that are filled in
    private Task saveData() {
        alarm_set = false;
        recur = false;
        //todo add some error handling to make sure that we have a valid task
        String title = etTitle.getText().toString();
        String location = etLocation.getText().toString();
        String date = btnDate.getText().toString();
        String time = btnTime.getText().toString();
        String category = spCategory.getSelectedItem().toString();

        alarm_set = cbAlarm.isChecked();
        recur = cb_mon.isChecked() || cb_tues.isChecked() || cb_wed.isChecked() || cb_thur.isChecked() || cb_fri.isChecked() || cb_sat.isChecked() || cb_sun.isChecked();
        String description = etDescription.getText().toString();

        //TODO set the category picker from the spinner

        Task toReturn = new Task(title, description, date, time, false, alarm_set, category, location, false);
        return toReturn;
    }


    private void createNotification(PushNotification notification) {

        Call<PushNotification> call = notificationApi.postNotification(notification);

        call.enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error: " + response.code());
                    return;
                } else {
                    Log.i(TAG, "Error: " + response.code());
                    Gson gson = new Gson();
                   // Log.d(TAG, "Response: " + gson.toJson(response));
                    Log.i(TAG, "Body:" + response.body());
                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {

            }
        });
    }

    @Override
    public void onButtonClicked(List<User> selectedUsers) {
        usersSelected = selectedUsers;
        tv_alarmFriends.setText("");
        if(selectedUsers.size() <= 5) {
            for (User user : selectedUsers) {
                if (tv_alarmFriends.getText().toString().equals(""))
                    tv_alarmFriends.setText(user.getEmail());
                else
                    tv_alarmFriends.append("\n" + user.getEmail());
            }
        }
        else{
            Toast.makeText(getContext(), "No more than 5 buddies can be added", Toast.LENGTH_SHORT).show();
        }
    }
}