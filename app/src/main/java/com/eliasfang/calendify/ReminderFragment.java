package com.eliasfang.calendify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.EventAdapter;
import com.eliasfang.calendify.Adapter.TaskListAdapter;
import com.eliasfang.calendify.Database.ReminderEntity;
import com.eliasfang.calendify.Database.DatabaseClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment subclass}
 */
public class ReminderFragment extends Fragment {
    public static final int REMINDER_CREATION_FRAGMENT = 1;

    private RecyclerView recyclerView;
    private FloatingActionButton btn_createEvent;
    EventAdapter eventAdapter;
    DatabaseClass databaseClass;

    public ReminderFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    // This eve is triggered soon after onCreateView()
    // Any view setup should occur here. E.g., view lookups and attaching view listeners
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_createEvent = view.findViewById(R.id.btn_createEvent);
        recyclerView = view.findViewById(R.id.reminder_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        btn_createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Create a Reminder", Toast.LENGTH_SHORT).show();
                goToCreateEventActivity();


            }
        });



        databaseClass = DatabaseClass.getDatabase(getContext());


        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int direction) {
                final int position = target.getAdapterPosition();
                final ReminderEntity item = eventAdapter.reminderEntities.get(position);
                eventAdapter.reminderEntities.remove(position);
                databaseClass.EventDao().deleteTask(item.getId());
                Snackbar snackbar = Snackbar
                        .make(target.itemView, "Item was removed from the list.", Snackbar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            databaseClass.EventDao().insertAll(item);
                            eventAdapter.reminderEntities.add(position, item);
                            eventAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(position);
                        }
                    });
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();


                eventAdapter.notifyDataSetChanged();

            }
        });
        helper.attachToRecyclerView(recyclerView);



    }

    private void setAdapter() {
        List<ReminderEntity> classList = databaseClass.EventDao().getAllData();
        eventAdapter = new EventAdapter(getContext(), classList);
        recyclerView.setAdapter(eventAdapter);
        final int position = eventAdapter.reminderEntities.size()-1;
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    private void goToCreateEventActivity() {
        Intent intent = new Intent(getContext(), ReminderCreateDialogFragment.class);
        startActivity(intent);

    }

}