package com.eliasfang.calendify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.Database.ReminderEntity;
import com.eliasfang.calendify.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    Context context;
    public List<ReminderEntity> reminderEntities;

    public EventAdapter(Context context, List<ReminderEntity> reminderEntities) {
        this.context = context;
        this.reminderEntities = reminderEntities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listings_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.eventText.setText(reminderEntities.get(position).getEventname());
        holder.timeAndDateText.setText(reminderEntities.get(position).getEventdate() + " " + reminderEntities.get(position).getEventtime());
    }

    @Override
    public int getItemCount() {
        return reminderEntities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eventText, timeAndDateText;
        private LinearLayout toplayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventText = (TextView) itemView.findViewById(R.id.reminder);
            timeAndDateText = (TextView) itemView.findViewById(R.id.time_and_date);
            toplayout = (LinearLayout) itemView.findViewById(R.id.toplayout);
        }
    }


}
