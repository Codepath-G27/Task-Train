package com.eliasfang.calendify;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.TaskListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ToDoFragment extends Fragment {

    public static final int TASK_CREATION_FRAGMENT = 1;

    public static final String EXTRA_REPLY =
            "com.eliasfang.calendify.TASK";

    public static final String TAG = "PostsFragment";
    private RecyclerView rvPosts;

    private FloatingActionButton fabCreate;

    protected TaskListAdapter adapter;
    protected List<Task> allTasks;

    private TaskViewModel myTaskViewModel;

    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fabCreate = view.findViewById(R.id.fabCreate);
        RecyclerView recyclerView = view.findViewById(R.id.rvItems);
        myTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        final TaskListAdapter adapter = new TaskListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myTaskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);

            }
        });

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = TaskCreateDialogFragment.newInstance();
                //todo just write it into the database directly
                dialog.setTargetFragment(getActivity().getSupportFragmentManager().findFragmentById(R.id.action_todo), TASK_CREATION_FRAGMENT);
                dialog.show(getActivity().getSupportFragmentManager(), "tag");
                Toast.makeText(getContext(), "Create a task", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TASK_CREATION_FRAGMENT && resultCode == RESULT_OK && data != null) {
            Bundle info = data.getExtras();
            Task newTask = info.getParcelable(EXTRA_REPLY);
            myTaskViewModel.insert(newTask);
        } else {
            Toast.makeText(getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
        }

    }

}