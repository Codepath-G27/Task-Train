package com.eliasfang.calendify.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> implements Filterable {
    private final LayoutInflater mInflater;
    private List<Task> myTasks; // Cached copy of Tasks
    private List<Task> myTasksFull;
    private static final String TAG = "TaskListAdapter";

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
            holder.toggle.setChecked(current.isHasAlarm());
            holder.position = position;
        } else {
            // Covers the case of data not being ready yet.
            holder.tvTitle.setText("Task not specified yet");
        }
    }



    public List<Task> getMyTasks() {
        return myTasks;
    }


    public void setTasks(List<Task> words) {
        myTasks = words;
        myTasksFull = new ArrayList<>(myTasks);
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

    public void updateTask() {

    }





    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDate;
        private Switch toggle;
        int position;


        //private final TextView tvDesc;

        private TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            toggle = itemView.findViewById(R.id.swAlarm);






            //tvDesc = itemView.findViewById(R.id.tvDescription);
        }

    }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }

    private Filter taskFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Task> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(myTasksFull);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Task item : myTasksFull){
                    Log.i(TAG, item.getName() + "And" + filterPattern);
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            myTasks.clear();
            myTasks.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
