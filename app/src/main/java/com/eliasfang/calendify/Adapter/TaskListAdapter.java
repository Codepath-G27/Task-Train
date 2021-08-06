package com.eliasfang.calendify.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.Task;
import com.eliasfang.calendify.data.TaskViewModel;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> implements Filterable {
    private static final String TAG = "TaskListAdapter";


    private List<Task> myTasks = new ArrayList<>();

    private List<Task> myTasksFull;
    private boolean isEnabled = false;
    private boolean isSelectAll = false;


    private Activity activity;
    private Context context;

    ArrayList<Task> selectedList = new ArrayList<Task>();
    private TaskViewModel mainViewModel;

    public TaskListAdapter(Activity actInput) {
        activity = actInput;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) activity).get(TaskViewModel.class);

        return new TaskViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final TaskViewHolder holder, final int position) {

        Task current = myTasks.get(position);
        Log.i(TAG, "INIT " + current.getExpanded());
        holder.tvTitle.setText(current.getName());
        holder.toggle.setChecked(current.isHasAlarm());
        holder.tvCategory.setText(current.getCategory());
        if(!current.getEventTime().equals("Add time"))
            holder.tvTime.setText(current.getEventTime());

        if(!current.getEventDate().equals("Add date"))
            holder.tvDate.setText(current.getEventDate());
        else if(current.getRecurrence())
            holder.tvDate.setText("Recurring Alarm");

        holder.tvDescription.setText(current.getDescription());
        holder.tvLocation.setText(current.getLocation());

        if (current.getExpanded())
            holder.rLayout.setVisibility(View.VISIBLE);
        else
            holder.rLayout.setVisibility(View.GONE);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Click Condiditon
                if (!isEnabled) {
                    //When action mode not enabled
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            mode.getMenuInflater().inflate(R.menu.menu_select, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            //When action mode is preper
                            //set isEnabled true
                            isEnabled = true;
                            //Create method for item click
                            ClickItem(holder);
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            //When Clicked on action mode item
                            //get item id
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.menu_delete:
                                    //When cliked delte items


                                    for (Task task : selectedList) {
                                        mainViewModel.delete(task);
                                    }
                                    if (myTasks.size() == 0) {
                                        //When empty
                                    }

                                    mode.finish();
                                    break;
                                case R.id.select_all:
                                    if (selectedList.size() == myTasks.size()) {
                                        //If all seledted then unselect
                                        isSelectAll = false;
                                        selectedList.clear();
                                    } else {
                                        //When all item unselected
                                        isSelectAll = true;
                                        selectedList.clear();
                                        selectedList.addAll(myTasks);
                                    }
                                    notifyDataSetChanged();
                                    break;
                                case R.id.complete:

                                    for (Task task : selectedList) {
                                        mainViewModel.delete(task);
                                    }
                                    if (!selectedList.isEmpty()) {
                                        Alerter.create(activity)
                                                .setTitle("Selected Tasks Completed")
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

                                    mode.finish();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            //When action mode is detroy
                            //Set is Enabled as false
                            isEnabled = false;
                            isSelectAll = false;
                            selectedList.clear();
                            notifyDataSetChanged();
                        }
                    };
                    //Start aciton mode
                    ((AppCompatActivity) v.getContext()).startActionMode(callback);
                } else {
                    //when already enabled
                    ClickItem(holder);
                }
                return true;
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task current = myTasks.get(position);

                if (isEnabled) {
                    ClickItem(holder);
                } else {

                    if (!current.getExpanded()) {

                        current.setExpanded(true);
                    } else {
                        current.setExpanded(false);
                    }

                    mainViewModel.update(current);

                }
            }
        });


        //Check condition
        if (isSelectAll) {
            holder.ivCheckBox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.ivCheckBox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.BLACK);
        }
    }

    private void ClickItem(TaskViewHolder holder) {
        //get selected item value
        Task selected = myTasks.get(holder.getAdapterPosition());
        //Check condidtion
        if (holder.ivCheckBox.getVisibility() == View.GONE) {
            //When not selected
            //Check image
            holder.ivCheckBox.setVisibility(View.VISIBLE);
            //Set background
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            //Add value to list of selected items
            selectedList.add(selected);
        } else {
            //when item selected
            //Hide the check box
            holder.ivCheckBox.setVisibility(View.GONE);
            //Set background
            holder.itemView.setBackgroundColor(Color.BLACK);
            //remove value
            selectedList.remove(selected);
        }
    }


    public List<Task> getMyTasks() {
        return myTasks;
    }

    public Task getTaskAt(int position) {
        return myTasks.get(position);
    }


    public void setTasks(List<Task> tasks) {
        myTasks = tasks;
        myTasksFull = new ArrayList<>(myTasks);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return myTasks.size();
    }


    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvCategory;
        private final TextView tvTime;
        private final TextView tvDate;
        private Switch toggle;
        private final TextView tvLocation;
        private final TextView tvDescription;
        private ImageView ivCheckBox;
        private RelativeLayout rLayout;


        private TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvFriendName);
            tvCategory = itemView.findViewById(R.id.tvToTitle);
            toggle = itemView.findViewById(R.id.swAlarm);
            ivCheckBox = itemView.findViewById(R.id.ivCheckBox);
            rLayout = itemView.findViewById(R.id.rLayout);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);


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

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(myTasksFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Task item : myTasksFull) {
                    Log.i(TAG, item.getName() + "And" + filterPattern);
                    if (item.getName().toLowerCase().contains(filterPattern)) {
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
