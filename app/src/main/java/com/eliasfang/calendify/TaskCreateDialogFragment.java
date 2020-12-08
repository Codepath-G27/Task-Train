package com.eliasfang.calendify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class TaskCreateDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String EXTRA_REPLY =
            "com.eliasfang.calendify.TASK";

    public static final int TASK_CREATION_FRAGMENT = 1;
    private EditText etTitle;
    private EditText etLocation;
    private TextView tvDate;
    private TextView tvTime;
    private CheckBox cbRecur;
    private EditText etDescription;

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
        tvDate = view.findViewById(R.id.tvDate);
        tvTime = view.findViewById(R.id.tvTime);
        cbRecur = view.findViewById(R.id.cbRecur);
        etDescription = view.findViewById(R.id.etDescription);

        // Show soft keyboard automatically and set focus on event name
        etTitle.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Check for clicks on top buttons
        imgBtnClose.setOnClickListener(this);
        tvSave.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // Manage which button was clicked on event creation dialog
        switch (id) {
            case R.id.imgBtnClose:
                dismiss();
                Toast.makeText(getContext(), "Cancelled task creation", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvSave:
                Task task = saveFields();
                Intent replyIntent = new Intent();
                replyIntent.putExtra(EXTRA_REPLY, task);

                //getActivity().getSupportFragmentManager().findFragmentById(R.id.action_todo).onActivityResult(TASK_CREATION_FRAGMENT, RESULT_OK, replyIntent);
                dismiss();

                Toast.makeText(getContext(), "Task saved", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Helper functions to save the fields that are filled in
    private Task saveFields() {
        //todo add some error handling to make sure that we have a valid task
        String title = etTitle.getText().toString();
        String location = etLocation.getText().toString();
        String date = tvDate.getText().toString();
        String time = tvTime.getText().toString();
        boolean recur = cbRecur.isChecked();
        String description = etDescription.getText().toString();

        Task toReturn = new Task(title, description, (long) 0.0, false, false, 0);
        return toReturn;
    }

}