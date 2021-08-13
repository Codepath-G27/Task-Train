package com.eliasfang.calendify.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.CalendarAdapter;
import com.eliasfang.calendify.Adapter.CalendarEventAdapter;
import com.eliasfang.calendify.R;
import com.eliasfang.calendify.data.TaskViewModel;
import com.eliasfang.calendify.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.eliasfang.calendify.fragments.ToDoFragment.TASK_CREATION_FRAGMENT;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView tv_Month;
    private RecyclerView rv_Calendar;
    private RecyclerView rv_calEvents;
    private LocalDate selectedDate;
    private ImageView ivForward;
    private ImageView ivBack;
    private TaskViewModel myTaskViewModel;
    private TextView emptyView;
    private Observer observer;
    private LiveData<List<Task>> data;
    private FloatingActionButton fabCalCreate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();



        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);

        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        
        initView(view);
        ivForward = view.findViewById(R.id.iv_forward);
        ivBack = view.findViewById(R.id.iv_backward);

        rv_calEvents= view.findViewById(R.id.rv_calEvents);
        emptyView = view.findViewById(R.id.empty_view);

        fabCalCreate = view.findViewById(R.id.fabCalCreate);

        fabCalCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("M-dd-yyyy"));

                DialogFragment dialog = TaskCreateDialogFragment.newInstance(formattedDate);


                dialog.setTargetFragment(getActivity().getSupportFragmentManager().findFragmentById(R.id.action_todo), TASK_CREATION_FRAGMENT);
                dialog.show(getActivity().getSupportFragmentManager(), "tag");





            }
        });


        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextMonth();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPreviousMonth();
            }
        });

        selectedDate = LocalDate.now();


        setMonthView();

    }


    private void setMonthView() {
        tv_Month.setText(getMonthFromDate(selectedDate));

        ArrayList<LocalDate> monthDays = daysInMonthArray(selectedDate);
        String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("M-dd-yyyy"));


        CalendarEventAdapter calendarEventAdapter = new CalendarEventAdapter();
        rv_calEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_calEvents.setAdapter(calendarEventAdapter);

        //Remove the observer from the old view of data
            //Check if not null bc it will be null for first call
        if(data != null){
            data.removeObserver(observer);
        }

        //Get the new data and store the observer for deletion later
        myTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        data = myTaskViewModel.getTasksByDate(formattedDate);

        data.observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                calendarEventAdapter.setTasks(tasks);
                Log.i("BRAD", "TASks: " + tasks + "\n" + "date : " + formattedDate);
                observer = this;
                if(tasks.isEmpty()){
                    rv_calEvents.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else{
                    rv_calEvents.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

            }
        });


        CalendarAdapter calendarAdapter = new CalendarAdapter(monthDays, this, selectedDate);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 7);
        rv_Calendar.setLayoutManager(manager);
        rv_Calendar.setAdapter(calendarAdapter);

    }

    private ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add(null);
            }
            else
            {
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(), i- dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    private String getMonthFromDate(LocalDate selectedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return  selectedDate.format(formatter);
    }

    private void initView(View view) {
        rv_Calendar = view.findViewById(R.id.rv_cal);
        tv_Month = view.findViewById(R.id.tv_monthYear);
    }

    public void goPreviousMonth(){
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void goNextMonth(){
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate day) {
        if(day != null) {
            selectedDate = day;
            setMonthView();
        }
    }
}
