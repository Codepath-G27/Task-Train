package com.eliasfang.calendify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kizitonwose.calendarview.CalendarView;

import java.time.LocalDate;

/**
 * A simple {@link Fragment subclass}
 */
public class CalendarFragment extends Fragment {

    private CalendarView cvCalendar;
    private TextView tvDate;
    private FloatingActionButton btnAdd;

    private LocalDate today = LocalDate.now();

    public CalendarFragment() {
        // Required empty public constructor
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    // This eve is triggered soon after onCreateView()
    // Any view setup should occur here. E.g., view lookups and attaching view listeners
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cvCalendar = view.findViewById(R.id.cvCalendar);
        tvDate = view.findViewById(R.id.tvDate);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add click action
            }
        });
    }
}