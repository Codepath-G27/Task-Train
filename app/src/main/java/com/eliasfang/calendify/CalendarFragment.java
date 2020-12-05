package com.eliasfang.calendify;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kizitonwose.calendarview.CalendarView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment subclass}
 */
public class CalendarFragment extends Fragment {

    private CalendarView cvCalendar;
    private TextView tvDate;
    private FloatingActionButton btnAdd;

    private LocalDate selectedDate;
    private LocalDate today = LocalDate.now();
    private DateTimeFormatter titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM");
    private DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
    private DateTimeFormatter selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");

    private AlertDialog dialog = null;

    List<Task> tasks;
    
    // Create the adapter
    // final CalendarAdapter calendarAdapter = new CalendarAdapter(this);

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
        tasks = new ArrayList<>();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    EditText etDialog = new AppCompatEditText(getContext());
                    FrameLayout flDialog = new FrameLayout(getContext());
                    int padding = dpToPx(20);

                    flDialog.setPadding(padding, padding, padding, padding);
                    flDialog.addView(etDialog, new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

                    dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Calendify")
                            .setView(flDialog)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Save event
                                    etDialog.setText("");
                                }
                            })
                            .setNegativeButton("Close", null)
                            .create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            // Show the keyboard
                            etDialog.requestFocus();
                            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            // Hide the keyboard
                            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    });
                }
            }
        });
    }

    // Convert dp to px
    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}