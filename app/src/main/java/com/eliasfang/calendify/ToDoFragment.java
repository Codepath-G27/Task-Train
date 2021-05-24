package com.eliasfang.calendify;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.TaskListAdapter;
import com.eliasfang.calendify.Database.ReminderEntity;
import com.github.jinatonic.confetti.CommonConfetti;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ToDoFragment extends Fragment {

    public static final int TASK_CREATION_FRAGMENT = 1;
    public static final String EXTRA_REPLY =
            "com.eliasfang.calendify.TASK";

    public static final String TAG = "ToDoFragment";
    private RecyclerView recyclerView;

    private FloatingActionButton fabCreate;

    protected TaskListAdapter adapter;
    protected List<Task> allTasks;

    private TaskViewModel myTaskViewModel;

    private SearchView searchView;
    private RelativeLayout clRoot;

    //Colors array for app styling
    private int colors[] = {Color.parseColor("#94C1FF"), Color.parseColor("#8080FF"), Color.parseColor("#785CF7")};



    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fabCreate = view.findViewById(R.id.fabCreate);
        recyclerView = view.findViewById(R.id.rvItems);
        clRoot = view.findViewById(R.id.clroot);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);




        myTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        adapter = new TaskListAdapter(getContext(), getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myTaskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);

            }
        });

        setHasOptionsMenu(true);

        allTasks = adapter.getMyTasks();

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DialogFragment dialog = TaskCreateDialogFragment.newInstance();
                    dialog.setTargetFragment(getActivity().getSupportFragmentManager().findFragmentById(R.id.action_todo), TASK_CREATION_FRAGMENT);
                    dialog.show(getActivity().getSupportFragmentManager(), "tag");
            }
        });



        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int direction) {
                final int position = target.getAdapterPosition();
                final Task item = adapter.getMyTasks().get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    myTaskViewModel.updateCompleted(item);
                    myTaskViewModel.refreshTasks();
                    showAlerter(view);
                    CommonConfetti.rainingConfetti(clRoot, colors).oneShot();
                } else {
                    adapter.getMyTasks().remove(position);
                    myTaskViewModel.deleteTask(item);
                    //Code to include alerter instead of snackbar for deletion
//                    Alerter.create(getActivity())
//                            .setTitle("Task Deleted")
//                            .setText("Click to Undo")
//                            .setBackgroundColorRes(R.color.colorAccent)
//                            .setIcon(R.drawable.icon_tasktrain)
//                            .setIconColorFilter(0) // Optional - Removes white tint
//                            .enableSwipeToDismiss()
//                            .enableProgress(true)
//                            .setProgressColorRes(R.color.colorPrimary)
//                            .show();
                    Snackbar snackbar = Snackbar
                            .make(target.itemView, "Item was removed from the list.", Snackbar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myTaskViewModel.insert(item);
                            adapter.getMyTasks().add(position, item);
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(position);
                        }
                    });
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();

                    adapter.notifyDataSetChanged();

                }


            }
        });
        helper.attachToRecyclerView(recyclerView);

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
        adapter.notifyDataSetChanged();

    }

    public void showAlerter(View v){
        Alerter.create(getActivity())
                .setTitle("Task Completed")
                .setText("Keep on Chugging Ahead!")
                .setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.icon_tasktrain)
                .setIconColorFilter(0) // Optional - Removes white tint
                .enableSwipeToDismiss()
                .enableProgress(true)
                .setDuration(1500)
                .setProgressColorRes(R.color.colorPrimary)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_todo, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }

    }
}