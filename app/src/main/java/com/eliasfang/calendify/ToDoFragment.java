package com.eliasfang.calendify;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.TaskListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ToDoFragment extends Fragment {

    public static final String TAG = "PostsFragment";
    private RecyclerView rvPosts;

    private FloatingActionButton fabCreate;

    protected TaskListAdapter adapter;
    protected List<Task> allTasks;


    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fabCreate = view.findViewById(R.id.fabCreate);
        RecyclerView recyclerView = view.findViewById(R.id.rvItems);
        final TaskListAdapter adapter = new TaskListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = TaskCreateDialogFragment.newInstance();
                dialog.show(getFragmentManager(), "tag");
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

}