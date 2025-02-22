package com.eliasfang.calendify.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.Task;
import com.eliasfang.calendify.data.TaskViewModel;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tapadoo.alerter.Alerter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.TaskListAdapter;
import com.github.jinatonic.confetti.CommonConfetti;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ToDoFragment extends Fragment {

    public static final int TASK_CREATION_FRAGMENT = 1;

    public static final String TAG = "ToDoFragment";
    private RecyclerView recyclerView;

    private FloatingActionButton fabCreate;

    protected TaskListAdapter adapter;



    private TaskViewModel myTaskViewModel;

    private SearchView searchView;
    private RelativeLayout clRoot;

    private MenuItem sortAlpha;
    private MenuItem sortCategory;

    //Colors array for app styling
    private int colors[] = {Color.parseColor("#94C1FF"), Color.parseColor("#8080FF"), Color.parseColor("#785CF7")};

    private FirebaseFirestore database;
    private FirebaseAuth auth;


    public ToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        fabCreate = view.findViewById(R.id.fabCreate);
        recyclerView = view.findViewById(R.id.rvItems);

        adapter = new TaskListAdapter(getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        clRoot = view.findViewById(R.id.clroot);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null


        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        myTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        myTaskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                Log.i(TAG, "TASKS: " + tasks);
                adapter.setTasks(tasks);
            }
        });



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
                final Task item = adapter.getTaskAt(position);


                if (direction == ItemTouchHelper.LEFT) {
                    if (item.isHasAlarm()) {
                        item.cancelAlarm(getContext());
                        removeAlarmFromFirebase(item);
                    }

                    myTaskViewModel.delete(item);
                    showAlerter(view);
                    CommonConfetti.rainingConfetti(clRoot, colors).oneShot();
                } else {
                    myTaskViewModel.delete(item);

                    //Cancel alarm when deleted
                    if (item.isHasAlarm()) {
                        item.cancelAlarm(getContext());
                        removeAlarmFromFirebase(item);
                    }

                    Log.i(TAG, String.valueOf(item.getExpanded()));


                    Snackbar snackbar = Snackbar
                            .make(target.itemView, "Item was removed from the list.", Snackbar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myTaskViewModel.insert(item);
                            recyclerView.scrollToPosition(position);
                            //restore alarm if brought back
                        }
                    });
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();


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




    private void removeAlarmFromFirebase(Task task) {

        if (auth.getCurrentUser() != null && task.isHasAlarmBuddy()) {
            String alarmId = String.valueOf(task.getAlarmId());

            database.collection("users").document(auth.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            Map<String, Map<String, String>> tasks = user.getTasks();
                            if (tasks.containsKey(alarmId)) {
                                tasks.remove(alarmId);
                                database.collection("users").document(auth.getUid()).update("tasks", tasks)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i(TAG, "Tasks updated");
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    public void showAlerter(View v) {
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

        sortAlpha = menu.findItem(R.id.sort_alpha);
        sortCategory = menu.findItem(R.id.sort_category);


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
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sort_alpha:
                if(item.isChecked() && sortCategory.isChecked()) {
                    item.setChecked(false);
                    sortCategory();
                }
                else if(item.isChecked()){
                    item.setChecked(false);
                    sortNormal();
                }
                else if(sortCategory.isChecked()){
                    item.setChecked(true);
                    sortAlphaCategory();
                }
                else{
                    item.setChecked(true);
                    sortAlpha();
                }
                return true;
            case R.id.sort_category:
                if(item.isChecked() && sortAlpha.isChecked()) {
                    item.setChecked(false);
                    sortAlpha();
                }
                else if(item.isChecked()){
                    item.setChecked(false);
                    sortNormal();
                }
                else if(sortAlpha.isChecked()){
                    item.setChecked(true);
                    sortAlphaCategory();
                }
                else{
                    item.setChecked(true);
                    sortCategory();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortAlphaCategory() {
        myTaskViewModel.getAllTasksAlphaCategory().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
            }
        });
    }

    private void sortCategory() {
        myTaskViewModel.getAllTasksCategory().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
            }
        });
    }

    private void sortNormal() {
        myTaskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
            }
        });
    }

    private void sortAlpha() {
        myTaskViewModel.getAllTasksAlpha().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
            }
        });

    }


    //Work in progress. When restoring alarm it always immediately goes off
//    public void setAlarm(Context context, Task task) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(context, Alarm.class);
//
//        intent.putExtra("event", task.getName());
//        intent.putExtra("date", task.getEventDate());
//        intent.putExtra("time", task.getEventDate());
//        intent.putExtra("id", task.getAlarmId());
//        Log.i("Id", task.getAlarmId() + "");
//
//        task.setHasAlarm(true);
//
//        Toast.makeText(context, "Retored", Toast.LENGTH_SHORT).show();
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getAlarmId(), intent, 0);
//        String TimeandDate = task.getEventDate() + " " + task.getEventTime();
//        DateFormat formatter = new SimpleDateFormat("M-d-yyyy hh:mm");
//        try {
//            Date date1 = formatter.parse(TimeandDate);
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
}