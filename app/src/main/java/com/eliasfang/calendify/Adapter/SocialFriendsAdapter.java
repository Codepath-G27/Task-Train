package com.eliasfang.calendify.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.Task;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

public class SocialFriendsAdapter extends RecyclerView.Adapter<SocialFriendsAdapter.FriendViewHolder> {

    private static final String TAG = "SocialFriendsAdapter";

    private List<User> users;
    private Context context;

    private Boolean isEnabled = false;

    private Boolean selectAll = false;

    ArrayList<User> selectedList = new ArrayList<User>();

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public SocialFriendsAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new SocialFriendsAdapter.FriendViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        if (users != null) {
            User current = users.get(position);
            holder.tvName.setText(current.getDisplayName());
            holder.tvEmail.setText(current.getEmail());
            setIcon(current.getIcon(), holder);
        } else {
            // Covers the case of data not being ready yet.
            holder.tvEmail.setText("Friend not specified yet");
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isEnabled) {
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            mode.getMenuInflater().inflate(R.menu.menu_friend, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            ClickItem(holder);
                            isEnabled = true;
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.menu_delete:
                                    //When clicked remove friends

                                    Log.i(TAG, "The click item selected list is: " + selectedList);
                                    DocumentReference mainRef = database.collection("users").document(auth.getUid());

                                    mainRef.get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    User main = documentSnapshot.toObject(User.class);
                                                    List<String> mainFriends = main.getFriends();
                                                    for (User user : selectedList) {
                                                        mainFriends.remove(user.getUid());
                                                    }
                                                    mainRef.update("friends", mainFriends)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.i(TAG, "Successfully updated friends list for main user: " + main.getUid());
                                                                    for (User user : selectedList) {
                                                                        DocumentReference ref = database.collection("users").document(user.getUid());


                                                                        List<String> friends = user.getFriends();
                                                                        Log.i(TAG, "user friends before " + friends);
                                                                        friends.remove(auth.getUid());
                                                                        Log.i(TAG, "user friends after " + friends);
                                                                        ref.update("friends", friends)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        Log.i(TAG, "Successfully updated friends list for user: " + user.getUid());
                                                                                        mode.finish();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.e(TAG, "Error updating document", e);
                                                                                    }
                                                                                });
                                                                    }

                                                                }
                                                            });
                                                    Log.i(TAG, "The selected items are: " + selectedList);
                                                }
                                            });

                                    break;
                                case R.id.select_all:
                                    if (selectedList.size() == users.size()) {
                                        //If all seledted then unselect
                                        selectAll = false;
                                        selectedList.clear();
                                    } else {
                                        //When all item unselected
                                        selectAll = true;
                                        selectedList.clear();
                                        selectedList.addAll(users);
                                    }
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            isEnabled = false;
                            selectAll = false;
                            selectedList.clear();
                            notifyDataSetChanged();
                        }
                    };
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
                if (isEnabled) {
                    ClickItem(holder);
                }
            }
        });
        //Check condition
        if (selectAll) {
            holder.ivSelect.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.ivSelect.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.BLACK);
        }

    }

    private void setIcon(int icon, SocialFriendsAdapter.FriendViewHolder holder)  {
        switch (icon) {
            case 0:
                holder.ivSocialIcon.setImageResource(R.mipmap.ic_default);
                break;
            case 1:
                holder.ivSocialIcon.setImageResource(R.mipmap.ic_logo_round);
                break;
            case 2:
                holder.ivSocialIcon.setImageResource(R.mipmap.ic_default_foreground);
                break;
            case 3:
                holder.ivSocialIcon.setImageResource(R.mipmap.ic_launcher_foreground);
                break;
            case 4:
                holder.ivSocialIcon.setImageResource(R.mipmap.ic_launcher);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;
        TextView tvName;
        ImageView ivSelect;
        ImageView ivSocialIcon;

        private FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvFriendName);
            tvName = itemView.findViewById(R.id.tvEmail);
            ivSelect = itemView.findViewById(R.id.iv_select_friend);
            ivSelect.setVisibility(View.GONE);
            ivSocialIcon = itemView.findViewById(R.id.iv_socialIcon);

        }


    }

    public List<User> getSelectedList() {
        return this.selectedList;
    }

    public void clearSelectedList() {
        this.selectedList.clear();
    }


    private void ClickItem(SocialFriendsAdapter.FriendViewHolder holder) {
        //get selected item value
        User selected = users.get(holder.getAdapterPosition());
        //Check condidtion
        if (holder.ivSelect.getVisibility() == View.GONE) {
            //When not selected
            //Check image
            holder.ivSelect.setVisibility(View.VISIBLE);
            //Set background
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            //Add value to list of selected items
            selectedList.add(selected);
            Log.i(TAG, "The click item selected list is: " + selectedList);
        } else {
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

