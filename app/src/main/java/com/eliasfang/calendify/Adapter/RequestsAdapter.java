package com.eliasfang.calendify.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.eliasfang.calendify.R;
import com.eliasfang.calendify.models.FriendRequest;
import com.eliasfang.calendify.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    private static final String TAG = "RequestsAdapter";

    private FriendRequest requestList;
    private List<User> users;
    private Context context;
    private String toId;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();



    public RequestsAdapter(Context context, FriendRequest requestList, List<User> users) {
        this.requestList = requestList;
        this.context = context;
        this.users = users;
    }


    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {

        Set<String> keys = requestList.getRequests().keySet();

        List<String> keyList = new ArrayList<String>();
        for(String key : keys)
            keyList.add(key);


        Log.e(TAG,"Pos" + position);
        ArrayList<String> requestData = requestList.getRequests().get(keyList.get(position));
        Log.e(TAG, "Key:" + keyList.get(position));
        String fromId = requestData.get(0);
        Log.e(TAG,"FromID " + fromId);
        String toId = requestData.get(1);
        Log.e(TAG,"toID " + toId);



        for(User user: users) {
            String uid = user.getUid();
            Log.i(TAG, "UID: " + uid);
            if(uid.equals(fromId)) {
                Log.i(TAG, "from ID set: " + uid);
                holder.tvFrom.setText(user.getDisplayName());
            }
            else if(uid.equals(toId)) {
                holder.tvTo.setText(user.getDisplayName());
                Log.i(TAG, "to ID set: " + uid);
            }

            if(fromId.equals(auth.getUid())) {
                holder.tvRequestType.setText("Outgoing");
                holder.ivAccept.setVisibility(View.INVISIBLE);
                holder.ivDecline.setVisibility(View.INVISIBLE);
            }
            else {
                holder.tvRequestType.setText("Incoming");
                holder.ivDecline.setVisibility(View.VISIBLE);
                holder.ivAccept.setVisibility(View.VISIBLE);
            }

            holder.ivAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptFriendRequest(fromId, toId);
                    acceptFriendRequest(toId, fromId);
                }
            });

            holder.ivDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check out the getting of fromID and toID from the position number
                    declineFriendRequest(fromId, toId);
                    declineFriendRequest(toId, fromId);
                }
            });
        }

    }


    private void acceptFriendRequest(String userId, String userId2) {
        //To remove the two friend requests
        declineFriendRequest(userId, userId2);
        declineFriendRequest(userId2, userId);

        //Now add them to the respective friend list in Users
        DocumentReference doc = database.collection("users").document(userId);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        List<String> listFriends = user.getFriends();

                        Log.i(TAG, "Friends list before: " + listFriends);
                        listFriends.add(userId2);
                        Log.i(TAG, "Friends list after: " + listFriends);

                        doc.update("friends", listFriends).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void declineFriendRequest(String userId, String removeId) {
        DocumentReference doc = database.collection("friend_requests").document(userId);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();


                    if (document.exists()) {
                        FriendRequest requests = document.toObject(FriendRequest.class);
                        Map<String, ArrayList<String>> map = requests.getRequests();

                        map.remove(removeId);


                        doc.update("requests", map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return requestList.getRequests().size();
    }




    class RequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFrom;
        private final TextView tvTo;
        private TextView tvRequestType;
        private ImageView ivAccept;
        private ImageView ivDecline;


        private RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            tvRequestType= itemView.findViewById(R.id.tvRequestType);
            ivAccept= itemView.findViewById(R.id.ivAccept);
            ivDecline= itemView.findViewById(R.id.ivDecline);


        }


    }
}
