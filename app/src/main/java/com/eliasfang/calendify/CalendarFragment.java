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
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Adapter.CalendarTaskAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A simple {@link Fragment subclass}
 */
public class CalendarFragment extends Fragment {

    private CalendarView cvCalendar;
    private TextView tvDate;
    private FloatingActionButton btnAdd;
    private RecyclerView rvCalendar;

    private LocalDate selectedDate = null;
    private LocalDate today = LocalDate.now();
    private DateTimeFormatter titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM");
    private DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
    private DateTimeFormatter selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");

    private AlertDialog dialog;
    private Map<LocalDate, List<Task>> tasks;

    private CalendarTaskAdapter adapter;

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
        rvCalendar = view.findViewById(R.id.rvCalendar);

        dialog = null;
        tasks = new HashMap<>();

        if (savedInstanceState == null) {
            selectDate(today);
        }

        adapter = new CalendarTaskAdapter(getContext(), tasks.get(selectedDate));
        rvCalendar.setAdapter(adapter);
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvCalendar.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));

        YearMonth currentMonth = YearMonth.now();

        cvCalendar.setup(currentMonth.minusMonths(20), currentMonth.plusMonths(20), WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
        cvCalendar.scrollToMonth(currentMonth);

        class DayViewContainer extends ViewContainer {
            CalendarDay day;

            public DayViewContainer(@NotNull View view) {
                super(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (day.getOwner() == DayOwner.THIS_MONTH) {
                            selectDate(day.getDate());
                        }
                    }
                });
            }

            public TextView getTextView() {
                return view.findViewById(R.id.tvDay);
            }

            public View getDotView() {
                return view.findViewById(R.id.vDot);
            }
        }

        cvCalendar.setDayBinder(new DayBinder<DayViewContainer>() {

            @NotNull
            @Override
            public DayViewContainer create(@NotNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NotNull DayViewContainer dayViewContainer, @NotNull CalendarDay calendarDay) {
                dayViewContainer.day = calendarDay;
                TextView tvDay = dayViewContainer.getTextView();
                View vDot = dayViewContainer.getDotView();

                tvDay.setText(calendarDay.getDate().getDayOfMonth());

                if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                    tvDay.setVisibility(TextView.VISIBLE);
                    if (calendarDay.getDate().equals(today)) {
                        tvDay.setTextColor(getResources().getColor(R.color.white));
                        tvDay.setBackgroundResource(R.drawable.today_bg);
                    } else if (calendarDay.getDate().equals(selectedDate)) {
                        tvDay.setTextColor(getResources().getColor(R.color.blue));
                        tvDay.setBackgroundResource(R.drawable.selected_bg);
                    } else {
                        tvDay.setTextColor(getResources().getColor(R.color.black));
                        tvDay.setBackground(null);
                        if (tasks.containsKey(calendarDay)) {
                            vDot.setVisibility(View.VISIBLE);
                        } else {
                            vDot.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    tvDay.setVisibility(TextView.INVISIBLE);
                    vDot.setVisibility(View.INVISIBLE);
                }
            }
        });

        cvCalendar.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth calendarMonth) {
                Toolbar toolbar = new Toolbar(getContext());
                // TODO: Figure out what it keyword means
                if (selectedDate.getYear() == today.getYear()) {
                    toolbar.setTitle(titleSameYearFormatter.format(selectedDate.getMonth()));
                } else {
                    toolbar.setTitle(titleFormatter.format(selectedDate.getMonth()));
                }
                selectDate(selectedDate);
                return Unit.INSTANCE;
            }
        });

        class MonthViewContainer extends ViewContainer {

            public MonthViewContainer(@NotNull View view) {
                super(view);
            }
        }

        cvCalendar.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {

            @NotNull
            @Override
            public MonthViewContainer create(@NotNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NotNull MonthViewContainer monthViewContainer, @NotNull CalendarMonth calendarMonth) {
                // TODO: Figure out bind function
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog();
            }
        });
    }

    // Display event input dialog
    public void inputDialog() {
        if (dialog == null) {
            EditText etDialog = new AppCompatEditText(getContext());
            FrameLayout flDialog = new FrameLayout(getContext());
            int padding = dpToPx(20);

            flDialog.setPadding(padding, padding, padding, padding);
            flDialog.addView(etDialog, new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

            dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Enter event title")
                    .setView(flDialog)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveTask(etDialog.toString());
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
        dialog.show();
    }

    // Convert dp to px
    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // Select date
    private void selectDate(LocalDate date) {
        if (selectedDate != date) {
            selectedDate = date;
            // TODO: Update adapter with date
        }
    }

    // Save new event
    private void saveTask(String name) {
        if (name.length() == 0) {
            Toast.makeText(getContext(), "Input is empty", Toast.LENGTH_LONG).show();
        } else {
            if (selectedDate != null) {
                tasks.get(selectedDate).add(new Task(name, "desc", "date", "time", false, false, 10));
                // TODO: Change to update to database
            }
        }
    }

    // Delete event
    private void deleteTask(Task task) {
        // String date = task.getDate();
        LocalDate date = selectedDate;
        tasks.get(date).remove(task);
        // TODO: Change to match Task
    }

    // Update adapter

}