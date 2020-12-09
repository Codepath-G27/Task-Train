package com.eliasfang.calendify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.Task;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private final LayoutInflater mInflater;
    private List<Task> myTasks; // Cached copy of Tasks

    public TaskListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_todo, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        if (myTasks != null) {
            Task current = myTasks.get(position);
            holder.tvTitle.setText(current.getName());

        } else {
            // Covers the case of data not being ready yet.
            holder.tvTitle.setText("Task not specified yet");
        }
    }

    public void setTasks(List<Task> words) {
        myTasks = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (myTasks != null)
            return myTasks.size();
        else return 0;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDate;

        private TaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
