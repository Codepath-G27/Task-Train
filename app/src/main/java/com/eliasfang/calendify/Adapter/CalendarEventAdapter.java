package com.eliasfang.calendify.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.data.TaskViewModel;
import com.eliasfang.calendify.models.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.CalendarViewHolder> {


    private Context context;
    private TaskViewModel mainViewModel;
    private List<Task> myTasks = new ArrayList<>();



    public CalendarEventAdapter() {

    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) parent.getContext()).get(TaskViewModel.class);

        return new CalendarViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Task current = myTasks.get(position);

        holder.tvTitle.setText(current.getName());
        holder.toggle.setChecked(current.isHasAlarm());
        holder.tvCategory.setText(current.getCategory());

        if (!current.getEventTime().equals(context.getResources().getString(R.string.add_time)))
            holder.tvTime.setText(current.getEventTime());

        if (!current.getEventDate().equals(context.getResources().getString(R.string.add_date)))
            holder.tvDate.setText(current.getEventDate());

        if (current.getRecurrence())
            holder.tvDate.setText(R.string.recurring);


        holder.tvDescription.setText(current.getDescription());
        holder.tvLocation.setText(current.getLocation());

        if (current.getExpanded())
            holder.rLayout.setVisibility(View.VISIBLE);
        else
            holder.rLayout.setVisibility(View.GONE);


        holder.ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task current = myTasks.get(position);


                if (!current.getExpanded()) {
                    holder.ivDown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                    current.setExpanded(true);
                } else {
                    holder.ivDown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                    current.setExpanded(false);
                }


                mainViewModel.update(current);


            }
        });

    }

    @Override
    public int getItemCount() {
        return myTasks.size();
    }

    public void setTasks(List<Task> tasks) {
        myTasks = tasks;
        notifyDataSetChanged();
    }

    public void clearTasks() {
        myTasks.clear();
        notifyDataSetChanged();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvCategory;
        private final TextView tvTime;
        private final TextView tvDate;
        private Switch toggle;
        private final TextView tvLocation;
        private final TextView tvDescription;
        private RelativeLayout rLayout;
        private ImageView ivDown;


        private CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvFriendName);
            tvCategory = itemView.findViewById(R.id.tvToTitle);
            toggle = itemView.findViewById(R.id.swAlarm);
            rLayout = itemView.findViewById(R.id.rLayout);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivDown = itemView.findViewById(R.id.ivDown);


        }


    }


}
