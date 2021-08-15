package com.eliasfang.calendify.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;

import java.time.LocalDate;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<LocalDate> daysOfMonth;
    private Context context;
    private LocalDate selectedDate;

    private final OnItemListener onItemListener;

    public CalendarAdapter(List<LocalDate> daysOfMonth, OnItemListener onItemListener, LocalDate selectedDate) {
        this.onItemListener = onItemListener;
        boolean flag = true;
        for(int i=0; i<7; i++){
            flag = flag && daysOfMonth.get(i) == null;
        }

        if(flag){
            daysOfMonth.addAll(daysOfMonth.subList(0,7));
            daysOfMonth.subList(0,7).clear();
        }

        this.daysOfMonth = daysOfMonth;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        context = parent.getContext();

        layoutParams.height = WRAP_CONTENT;
        layoutParams.setMargins(3, 3, 3, 3);
        return new CalendarViewHolder(view, onItemListener);
    }



    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        if(daysOfMonth.get(position) == (null)) {
            holder.cvCal.setVisibility(View.INVISIBLE);
        }
        else {
            LocalDate date = daysOfMonth.get(position);
            holder.tvDate.setText(String.valueOf(date.getDayOfMonth()));

            if(date.equals(selectedDate)){
                holder.llCal.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.tvDate.setTextColor(context.getResources().getColor(R.color.white));
            }
        }



    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate;
        CardView cvCal;
        LinearLayout llCal;
        private final OnItemListener onItemListener;

        private CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_Cell);
            cvCal = itemView.findViewById(R.id.cv_cal);
            llCal = itemView.findViewById(R.id.ll_cal);

            this.onItemListener = onItemListener;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onItemClick(getAdapterPosition(), daysOfMonth.get(getAdapterPosition()));
                }
            });
        }


    }

    public interface OnItemListener{
        void onItemClick(int position, LocalDate day);
    }




}
