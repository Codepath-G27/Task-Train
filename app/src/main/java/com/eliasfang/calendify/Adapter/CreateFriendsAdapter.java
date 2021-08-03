package com.eliasfang.calendify.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateFriendsAdapter extends RecyclerView.Adapter<CreateFriendsAdapter.FriendViewHolder> {

    private List<User> users;
    private Context context;

    ArrayList<User> selectedList = new ArrayList<User>();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public CreateFriendsAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new CreateFriendsAdapter.FriendViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        if (users != null) {
            User current = users.get(position);
            holder.tvName.setText(current.getDisplayName());
            holder.tvEmail.setText(current.getEmail());
        } else {
            // Covers the case of data not being ready yet.
            holder.tvEmail.setText("Friend not specified yet");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickItem(holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;
        TextView tvName;
        ImageView ivSelect;

        private FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvFriendName);
            tvName = itemView.findViewById(R.id.tvEmail);
            ivSelect = itemView.findViewById(R.id.iv_select_friend);
            ivSelect.setVisibility(View.GONE);



        }


    }

    public List<User> getSelectedList(){
        return this.selectedList;
    }

    public void clearSelectedList(){
        this.selectedList.clear();
    }


    private void ClickItem(CreateFriendsAdapter.FriendViewHolder holder) {
        //get selected item value
        User selected = users.get(holder.getAdapterPosition());
        //Check condidtion
        if(holder.ivSelect.getVisibility() == View.GONE){
            //When not selected
            //Check image
            holder.ivSelect.setVisibility(View.VISIBLE);
            //Set background
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            //Add value to list of selected items
            selectedList.add(selected);
        }
        else{
            //when item selected
            //Hide the check box
            holder.ivSelect.setVisibility(View.GONE);
            //Set background
            holder.itemView.setBackgroundColor(Color.BLACK);
            //remove value
            selectedList.remove(selected);
        }
    }
}
